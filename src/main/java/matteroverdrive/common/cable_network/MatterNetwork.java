package matteroverdrive.common.cable_network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import matteroverdrive.common.block.type.TypeMatterNetworkCable;
import matteroverdrive.common.tile.matter_network.TileMatterAnalyzer;
import matteroverdrive.common.tile.matter_network.TileMatterReplicator;
import matteroverdrive.common.tile.matter_network.TileMatterTank;
import matteroverdrive.common.tile.matter_network.TileNetworkPowerSupply;
import matteroverdrive.common.tile.matter_network.TilePatternDrive;
import matteroverdrive.common.tile.matter_network.TilePatternMonitor;
import matteroverdrive.core.cable.AbstractNetwork;
import matteroverdrive.core.cable.CableNetworkRegistry;
import matteroverdrive.core.cable.types.matter_network.IMatterNetworkCable;
import matteroverdrive.core.cable.types.matter_network.IMatterNetworkMember;
import matteroverdrive.core.cable.types.matter_network.MatterNetworkEMPack;
import matteroverdrive.core.cable.types.matter_network.MatterNetworkUtils;
import matteroverdrive.core.utils.UtilsMatter;
import matteroverdrive.core.utils.UtilsTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MatterNetwork
		extends AbstractNetwork<IMatterNetworkCable, TypeMatterNetworkCable, BlockEntity, MatterNetworkEMPack> {

	private List<TileMatterAnalyzer> analyzers = new ArrayList<>();
	private List<TileMatterReplicator> replicators = new ArrayList<>();
	private List<TilePatternDrive> patternDrives = new ArrayList<>();
	private List<TilePatternMonitor> patternMonitors = new ArrayList<>();
	private List<TileMatterTank> matterTanks = new ArrayList<>();
	private List<TileNetworkPowerSupply> powerSupplies = new ArrayList<>();

	private int feTransmittedThisTick = 0;
	private int feTransmittedLastTick = 0;

	public MatterNetwork() {
		this(new HashSet<IMatterNetworkCable>());
	}

	public MatterNetwork(Collection<? extends IMatterNetworkCable> varCables) {
		conductorSet.addAll(varCables);
		CableNetworkRegistry.register(this);
	}

	public MatterNetwork(
			Set<AbstractNetwork<IMatterNetworkCable, TypeMatterNetworkCable, BlockEntity, MatterNetworkEMPack>> networks) {
		for (AbstractNetwork<IMatterNetworkCable, TypeMatterNetworkCable, BlockEntity, MatterNetworkEMPack> net : networks) {
			if (net != null) {
				conductorSet.addAll(net.conductorSet);
				net.deregister();
			}
		}
		refresh();
		CableNetworkRegistry.register(this);
	}

	public MatterNetwork(Set<MatterNetwork> networks, boolean special) {
		for (MatterNetwork net : networks) {
			if (net != null) {
				conductorSet.addAll(net.conductorSet);
				net.deregister();
			}
		}
		refresh();
		CableNetworkRegistry.register(this);
	}

	@Override
	public MatterNetworkEMPack emit(MatterNetworkEMPack maxTransfer, ArrayList<BlockEntity> ignored,
			boolean debug) {
		if (maxTransfer.fe() > 0 || maxTransfer.matter() > 0) {
			Set<BlockEntity> availableAcceptors = getNetworkAcceptors();
			double matterSent = 0;
			int feSent = 0;
			availableAcceptors.removeAll(ignored);
			if (!availableAcceptors.isEmpty()) {
				Iterator<BlockEntity> it = availableAcceptors.iterator();
				double totalMatterUsage = 0;
				int totalFEUsage = 0;
				HashMap<BlockEntity, MatterNetworkEMPack> usage = new HashMap<>();
				while (it.hasNext()) {
					BlockEntity receiver = it.next();
					double localMatterUsage = 0;
					int localFEUsage = 0;
					if (acceptorInputMap.containsKey(receiver)) {
						boolean shouldRemove = true;
						for (Direction connection : acceptorInputMap.get(receiver)) {
							MatterNetworkEMPack pack = MatterNetworkUtils.recieveEM(receiver, connection, maxTransfer,
									true);
							if (pack.fe() > 0 || pack.matter() > 0) {
								shouldRemove = false;
								totalMatterUsage += pack.matter();
								totalFEUsage += pack.fe();
								localMatterUsage += pack.matter();
								localFEUsage += pack.fe();
								break;
							}
						}
						if (shouldRemove) {
							it.remove();
						}
					}
					usage.put(receiver, new MatterNetworkEMPack(localFEUsage, localMatterUsage));
				}
				for (BlockEntity receiver : availableAcceptors) {
					MatterNetworkEMPack recieved = usage.get(receiver);
					int dedicatedFe = totalFEUsage > 0
							? (int) (maxTransfer.fe() * ((double) recieved.fe() / (double) totalFEUsage))
							: 0;
					double dedicatedMatter = totalMatterUsage > 0
							? maxTransfer.matter() * (recieved.matter() / totalMatterUsage)
							: 0;
					MatterNetworkEMPack dedicated = new MatterNetworkEMPack(dedicatedFe, dedicatedMatter);
					if (acceptorInputMap.containsKey(receiver)) {
						double size = acceptorInputMap.get(receiver).size();
						MatterNetworkEMPack perConnection = new MatterNetworkEMPack(
								(int) ((double) dedicated.fe() / size), dedicated.matter() / size);
						for (Direction connection : acceptorInputMap.get(receiver)) {
							MatterNetworkEMPack pack = MatterNetworkUtils.recieveEM(receiver, connection, perConnection,
									debug);
							matterSent += pack.matter();
							feSent += pack.fe();
							if (!debug) {
								transmittedThisTick += pack.matter();
								feTransmittedThisTick += pack.fe();
							}
						}
					}
				}
			}
			return new MatterNetworkEMPack(Math.min(feSent, maxTransfer.fe()),
					Math.min(matterSent, maxTransfer.matter()));
		}
		return MatterNetworkEMPack.EMPTY;
	}

	public Set<BlockEntity> getNetworkAcceptors() {
		return new HashSet<>(acceptorSet);
	}

	@Override
	public void tick() {
		super.tick();
		feTransmittedLastTick = feTransmittedThisTick;
		feTransmittedThisTick = 0;

		Iterator<IMatterNetworkCable> it = conductorSet.iterator();
		boolean broken = false;
		while (it.hasNext()) {
			IMatterNetworkCable conductor = it.next();
			if (conductor instanceof BlockEntity entity && entity.isRemoved() || conductor.getNetwork() != this) {
				broken = true;
				break;
			}
		}
		if (broken) {
			refresh();
		}
		if (getSize() == 0) {
			deregister();
		}
	}

	@Override
	public void refresh() {
		Iterator<IMatterNetworkCable> it = conductorSet.iterator();
		acceptorSet.clear();
		acceptorInputMap.clear();
		while (it.hasNext()) {
			IMatterNetworkCable conductor = it.next();
			if (conductor == null || ((BlockEntity) conductor).isRemoved()) {
				it.remove();
			} else {
				conductor.setNetwork(this);
			}
		}
		for (IMatterNetworkCable conductor : conductorSet) {
			BlockEntity tileEntity = (BlockEntity) conductor;
			for (Direction direction : Direction.values()) {
				BlockEntity acceptor = tileEntity.getLevel()
						.getBlockEntity(new BlockPos(tileEntity.getBlockPos()).offset(direction.getNormal()));
				if (acceptor != null && !isConductor(acceptor)) {
					if (isAcceptor(acceptor, direction)) {
						if (canConnect(acceptor, direction)) {
							BlockEntity casted = (BlockEntity) acceptor;
							acceptorSet.add(casted);
							HashSet<Direction> directions = acceptorInputMap.containsKey(acceptor)
									? acceptorInputMap.get(acceptor)
									: new HashSet<>();
							directions.add(direction.getOpposite());
							acceptorInputMap.put(casted, directions);
							addTileToCategory(casted);
						}
					}
				}
			}
		}
		updateStatistics();
	}

	private void addTileToCategory(BlockEntity entity) {
		if (entity instanceof TileMatterAnalyzer analyzer) {
			analyzers.add(analyzer);
		} else if (entity instanceof TileMatterReplicator replicator) {
			replicators.add(replicator);
		} else if (entity instanceof TilePatternMonitor monitor) {
			patternMonitors.add(monitor);
		} else if (entity instanceof TilePatternDrive drive) {
			patternDrives.add(drive);
		} else if (entity instanceof TileMatterTank tank) {
			matterTanks.add(tank);
		} else if (entity instanceof TileNetworkPowerSupply supply) {
			powerSupplies.add(supply);
		}
	}

	@Override
	public void deregister() {
		analyzers.clear();
		replicators.clear();
		patternMonitors.clear();
		patternDrives.clear();
		matterTanks.clear();
		powerSupplies.clear();
		super.deregister();

	}

	@Override
	public boolean isConductor(BlockEntity tile) {
		return tile instanceof IMatterNetworkCable;
	}

	@Override
	public boolean isAcceptor(BlockEntity acceptor, Direction orientation) {
		return acceptor instanceof IMatterNetworkMember member && member.canConnectToFace(orientation);
	}

	@Override
	public AbstractNetwork<IMatterNetworkCable, TypeMatterNetworkCable, BlockEntity, MatterNetworkEMPack> createInstance() {
		return new MatterNetwork();
	}

	@Override
	public AbstractNetwork<IMatterNetworkCable, TypeMatterNetworkCable, BlockEntity, MatterNetworkEMPack> createInstanceConductor(
			Set<IMatterNetworkCable> conductors) {
		return new MatterNetwork(conductors);
	}

	@Override
	public AbstractNetwork<IMatterNetworkCable, TypeMatterNetworkCable, BlockEntity, MatterNetworkEMPack> createInstance(
			Set<AbstractNetwork<IMatterNetworkCable, TypeMatterNetworkCable, BlockEntity, MatterNetworkEMPack>> networks) {
		return new MatterNetwork(networks);

	}

	@Override
	public TypeMatterNetworkCable[] getConductorTypes() {
		return TypeMatterNetworkCable.values();
	}

	@Override
	public boolean canConnect(BlockEntity acceptor, Direction orientation) {
		Direction opposite = orientation.getOpposite();
		return UtilsTile.isFEReciever(acceptor, opposite) || UtilsMatter.isMatterReceiver(acceptor, opposite);
	}

	public List<TileMatterAnalyzer> getAnalyzers() {
		return analyzers;
	}

	public List<TileMatterReplicator> getReplicators() {
		return replicators;
	}

	public List<TilePatternDrive> getPatternDrives() {
		return patternDrives;
	}

	public List<TilePatternMonitor> getPatternMonitors() {
		return patternMonitors;
	}

	public List<TileMatterTank> getMatterTanks() {
		return matterTanks;
	}

	public List<TileNetworkPowerSupply> getPowerSupplies() {
		return powerSupplies;
	}

}
