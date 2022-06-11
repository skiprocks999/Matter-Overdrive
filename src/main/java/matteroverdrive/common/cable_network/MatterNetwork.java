package matteroverdrive.common.cable_network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import matteroverdrive.common.block.cable.ICableType;
import matteroverdrive.common.block.type.TypeMatterNetworkCable;
import matteroverdrive.common.tile.cable.AbstractCableTile;
import matteroverdrive.common.tile.matter_network.TileMatterAnalyzer;
import matteroverdrive.common.tile.matter_network.TileMatterNetworkCable;
import matteroverdrive.common.tile.matter_network.TileMatterReplicator;
import matteroverdrive.common.tile.matter_network.TileMatterTank;
import matteroverdrive.common.tile.matter_network.TileNetworkPowerSupply;
import matteroverdrive.common.tile.matter_network.TilePatternDrive;
import matteroverdrive.common.tile.matter_network.TilePatternMonitor;
import matteroverdrive.core.network.BaseNetwork;
import matteroverdrive.core.network.utils.IMatterNetworkMember;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MatterNetwork extends BaseNetwork {

	private List<TileMatterAnalyzer> analyzers = new ArrayList<>();
	private List<TileMatterReplicator> replicators = new ArrayList<>();
	private List<TilePatternDrive> patternDrives = new ArrayList<>();
	private List<TilePatternMonitor> patternMonitors = new ArrayList<>();
	private List<TileMatterTank> matterTanks = new ArrayList<>();
	private List<TileNetworkPowerSupply> powerSupplies = new ArrayList<>();
	
	public MatterNetwork(Collection<? extends AbstractCableTile<?>> varCables) {
		super(varCables);
	}
	
	public MatterNetwork(Set<? extends BaseNetwork> networks) {
		super(networks);
	}
	
	public MatterNetwork(Set<? extends BaseNetwork> networks, boolean special) {
		super(networks, special);
	}
	
	public Set<BlockEntity> getNetworkAcceptors() {
		return new HashSet<>(connected);
	}

	@Override
	public void tick() {
		super.tick();

		Iterator<AbstractCableTile<?>> it = cables.iterator();
		boolean broken = false;
		while (it.hasNext()) {
			AbstractCableTile<?> conductor = it.next();
			if (conductor.isRemoved() || conductor.getNetwork() != this) {
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
		Iterator<AbstractCableTile<?>> it = cables.iterator();
		connected.clear();
		dirsPerConnectionMap.clear();
		while (it.hasNext()) {
			AbstractCableTile<?> conductor = it.next();
			if (conductor == null || conductor.isRemoved()) {
				it.remove();
			} else {
				conductor.setNetwork(this);
			}
		}
		for (AbstractCableTile<?> conductor : cables) {
			BlockEntity tileEntity = (BlockEntity) conductor;
			for (Direction direction : Direction.values()) {
				BlockEntity acceptor = tileEntity.getLevel().getBlockEntity(tileEntity.getBlockPos().relative(direction));
				if (acceptor != null && !isCable(acceptor) && canConnect(acceptor, direction)) {
					BlockEntity casted = (BlockEntity) acceptor;
					connected.add(casted);
					Set<Direction> directions = dirsPerConnectionMap.getOrDefault(acceptor, new HashSet<>()); 
					directions.add(direction.getOpposite());
					dirsPerConnectionMap.put(casted, directions);
					addTileToCategory(casted);
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
	public boolean isCable(BlockEntity tile) {
		return tile instanceof TileMatterNetworkCable;
	}

	@Override
	public ICableType[] getConductorTypes() {
		return TypeMatterNetworkCable.values();
	}

	@Override
	public boolean canConnect(BlockEntity acceptor, Direction orientation) {
		return acceptor instanceof IMatterNetworkMember member && member.canConnectToFace(orientation.getOpposite());
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

	@Override
	public BaseNetwork newInstance(Set<? extends BaseNetwork> networks) {
		return new MatterNetwork(networks);
	}

	@Override
	public BaseNetwork newInstance(Set<? extends BaseNetwork> networks, boolean special) {
		return new MatterNetwork(networks, special);
	}

}
