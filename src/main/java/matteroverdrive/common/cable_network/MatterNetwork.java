package matteroverdrive.common.cable_network;

import java.util.ArrayList;
import java.util.Collection;
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
import matteroverdrive.core.network.AbstractNetwork;
import matteroverdrive.core.network.CableNetworkRegistry;
import matteroverdrive.core.network.cable.utils.IMatterNetworkMember;
import matteroverdrive.core.network.cable.utils.INetworkCable;
import matteroverdrive.core.utils.UtilsMatter;
import matteroverdrive.core.utils.UtilsTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MatterNetwork extends AbstractNetwork<INetworkCable, TypeMatterNetworkCable, BlockEntity> {

	private List<TileMatterAnalyzer> analyzers = new ArrayList<>();
	private List<TileMatterReplicator> replicators = new ArrayList<>();
	private List<TilePatternDrive> patternDrives = new ArrayList<>();
	private List<TilePatternMonitor> patternMonitors = new ArrayList<>();
	private List<TileMatterTank> matterTanks = new ArrayList<>();
	private List<TileNetworkPowerSupply> powerSupplies = new ArrayList<>();

	public MatterNetwork() {
		this(new HashSet<INetworkCable>());
	}

	public MatterNetwork(Collection<? extends INetworkCable> varCables) {
		conductorSet.addAll(varCables);
		CableNetworkRegistry.register(this);
	}

	public MatterNetwork(
			Set<AbstractNetwork<INetworkCable, TypeMatterNetworkCable, BlockEntity>> networks) {
		for (AbstractNetwork<INetworkCable, TypeMatterNetworkCable, BlockEntity> net : networks) {
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

	public Set<BlockEntity> getNetworkAcceptors() {
		return new HashSet<>(acceptorSet);
	}

	@Override
	public void tick() {
		super.tick();

		Iterator<INetworkCable> it = conductorSet.iterator();
		boolean broken = false;
		while (it.hasNext()) {
			INetworkCable conductor = it.next();
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
		Iterator<INetworkCable> it = conductorSet.iterator();
		acceptorSet.clear();
		acceptorInputMap.clear();
		while (it.hasNext()) {
			INetworkCable conductor = it.next();
			if (conductor == null || ((BlockEntity) conductor).isRemoved()) {
				it.remove();
			} else {
				conductor.setNetwork(this);
			}
		}
		for (INetworkCable conductor : conductorSet) {
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
		return tile instanceof INetworkCable;
	}

	@Override
	public boolean isAcceptor(BlockEntity acceptor, Direction orientation) {
		return acceptor instanceof IMatterNetworkMember member && member.canConnectToFace(orientation);
	}

	@Override
	public AbstractNetwork<INetworkCable, TypeMatterNetworkCable, BlockEntity> createInstance() {
		return new MatterNetwork();
	}

	@Override
	public AbstractNetwork<INetworkCable, TypeMatterNetworkCable, BlockEntity> createInstanceConductor(
			Set<INetworkCable> conductors) {
		return new MatterNetwork(conductors);
	}

	@Override
	public AbstractNetwork<INetworkCable, TypeMatterNetworkCable, BlockEntity> createInstance(
			Set<AbstractNetwork<INetworkCable, TypeMatterNetworkCable, BlockEntity>> networks) {
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
	
	@Override
	public void split(INetworkCable splitPoint) {
		super.split(splitPoint);
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
