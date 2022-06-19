package matteroverdrive.common.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import matteroverdrive.common.tile.matter_network.TilePatternStorage;
import matteroverdrive.common.tile.matter_network.TilePatternMonitor;
import matteroverdrive.core.network.AbstractCableNetwork;
import matteroverdrive.core.network.utils.IMatterNetworkMember;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntity;

public class NetworkMatter extends AbstractCableNetwork {

	private ArrayList<TileMatterAnalyzer> analyzers = new ArrayList<>();
	private ArrayList<TileMatterReplicator> replicators = new ArrayList<>();
	private ArrayList<TilePatternStorage> patternDrives = new ArrayList<>();
	private ArrayList<TilePatternMonitor> patternMonitors = new ArrayList<>();
	
	public NetworkMatter(List<? extends AbstractCableTile<?>> varCables, boolean client) {
		super(varCables, client);
	}
	
	public NetworkMatter(Collection<? extends AbstractCableNetwork> networks, boolean client) {
		super(networks, client);
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
			deregister(clientSide);
		}
	}

	@Override
	public void refresh() {
		analyzers = new ArrayList<>();
		replicators = new ArrayList<>();
		patternDrives = new ArrayList<>();
		patternMonitors = new ArrayList<>();
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
		sortCables();
	}

	private void addTileToCategory(BlockEntity entity) {
		if (entity instanceof TileMatterAnalyzer analyzer) {
			analyzers.add(analyzer);
		} else if (entity instanceof TileMatterReplicator replicator) {
			replicators.add(replicator);
		} else if (entity instanceof TilePatternMonitor monitor) {
			patternMonitors.add(monitor);
		} else if (entity instanceof TilePatternStorage drive) {
			patternDrives.add(drive);
		} 
	}

	@Override
	public void deregister(boolean client) {
		analyzers.clear();
		replicators.clear();
		patternMonitors.clear();
		patternDrives.clear();
		super.deregister(client);

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
	
	@Override
	public AbstractCableNetwork newInstance(Collection<? extends AbstractCableNetwork> networks, boolean client) {
		return new NetworkMatter(networks, client);
	}
	
	@Override
	public AbstractCableNetwork newInstance(List<? extends AbstractCableTile<?>> cables, boolean client) {
		return new NetworkMatter(cables, client);
	}

	public List<TileMatterAnalyzer> getAnalyzers() {
		return analyzers;
	}

	public List<TileMatterReplicator> getReplicators() {
		return replicators;
	}

	public List<TilePatternStorage> getPatternDrives() {
		return patternDrives;
	}

	public List<TilePatternMonitor> getPatternMonitors() {
		return patternMonitors;
	}
	
	public CompoundTag serializeNetworkNbt() {
		CompoundTag data = new CompoundTag();
		
		patternDrives.removeAll(Collections.singletonList(null));
		
		data.putInt("drivesize", patternDrives.size());
		TilePatternStorage storage;
		for(int i = 0; i < patternDrives.size(); i++){
			storage = patternDrives.get(i);
			data.put("drivedata" + i, storage.getNetworkData());
			data.put("drivepos" + i, NbtUtils.writeBlockPos(storage.getBlockPos()));	
		}
		
		replicators.removeAll(Collections.singletonList(null));
		
		data.putInt("replicatorsize", replicators.size());
		TileMatterReplicator replicator;
		for(int i = 0; i < replicators.size(); i++) {
			replicator = replicators.get(i);
			data.put("repdata" + i, replicator.getNetworkData());
			data.put("reppos" + i, NbtUtils.writeBlockPos(replicator.getBlockPos()));
		}
		
		return data;
	}

}
