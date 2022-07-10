package matteroverdrive.common.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import matteroverdrive.MatterOverdrive;
import matteroverdrive.common.block.cable.ICableType;
import matteroverdrive.common.block.type.TypeMatterNetworkCable;
import matteroverdrive.common.tile.cable.AbstractCableTile;
import matteroverdrive.common.tile.matter_network.TileMatterAnalyzer;
import matteroverdrive.common.tile.matter_network.TileMatterNetworkCable;
import matteroverdrive.common.tile.matter_network.TileMatterReplicator;
import matteroverdrive.common.tile.matter_network.TilePatternStorage;
import matteroverdrive.common.tile.matter_network.TilePatternMonitor;
import matteroverdrive.core.capability.types.item_pattern.ItemPatternWrapper;
import matteroverdrive.core.network.AbstractCableNetwork;
import matteroverdrive.core.network.utils.IMatterNetworkMember;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.Item;
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
	
	public List<ItemPatternWrapper> getStoredPatterns(boolean client, boolean network){
		List<ItemPatternWrapper> patterns = new ArrayList<>();
		for(TilePatternStorage storage : patternDrives) {
			if(storage != null) {
				for(ItemPatternWrapper[] wrapperArr : storage.getWrappers(client, network)) {
					for(ItemPatternWrapper wrapper : wrapperArr) {
						if(wrapper.isNotAir()) {
							patterns.add(wrapper);
						}
					}
				}
			}
		}
		return patterns;
	}
	
	/**
	 * Gets the highest index of an item on a network
	 * 
	 * @param stack
	 * @return A 4 int array representing the drive's location in the ArrayList, and the relative coordinates on
	 * the Storage itself. return[0] will be -1 if no pattern is found.
	 */
	public int[] getHighestStorageLocationForItem(Item item, boolean checkPowered, boolean client, boolean network) {
		
		int[] highestStorageLoc = {-1, -1, -1, -1};
		int[] currData;
		int counter = 0;
		for(TilePatternStorage drive : patternDrives) {
			//Ternary in an if statement fight me
			if(drive != null && checkPowered ? drive.isPowered(client, network) : true) {
				currData = drive.getHighestStorageLocForItem(item, client, network);
				if(currData[2] > highestStorageLoc[3]) {
					highestStorageLoc[0] = counter;
					highestStorageLoc[1] = currData[0];
					highestStorageLoc[2] = currData[1];
					highestStorageLoc[3] = currData[2];
				}
			}
			counter++;
		}
		return highestStorageLoc;
	}
	
	@Nullable
	public TilePatternStorage getStorageFromIndex(int index) {
		try {
			return patternDrives.get(index);
		} catch(Exception e) {
			MatterOverdrive.LOGGER.info("Attempted to access drive at index " + index + " for network " + this.toString() + "; returning null");
			return null;
		}
		
	}
	
	//Serverside only
	public boolean storeItemFirstChance(Item item, int amt, boolean checkPowered) {
		for(TilePatternStorage drive : patternDrives) {
			if(drive != null && checkPowered ? drive.isPowered(false, false) : true) {
				if(drive.storeItemFirstChance(item, amt)) {
					return true;
				}
			}
		}
		return false;
	}

}
