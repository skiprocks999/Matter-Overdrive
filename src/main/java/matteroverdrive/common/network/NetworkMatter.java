package matteroverdrive.common.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
import matteroverdrive.common.tile.matter_network.TilePatternStorage;
import matteroverdrive.common.tile.matter_network.matter_replicator.QueuedReplication;
import matteroverdrive.common.tile.matter_network.matter_replicator.TileMatterReplicator;
import matteroverdrive.common.tile.matter_network.TilePatternMonitor;
import matteroverdrive.core.capability.types.item_pattern.ItemPatternWrapper;
import matteroverdrive.core.network.AbstractCableNetwork;
import matteroverdrive.core.network.utils.IMatterNetworkMember;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;

public class NetworkMatter extends AbstractCableNetwork {

	private HashMap<BlockPos, TileMatterAnalyzer> analyzers = new HashMap<>();
	private HashMap<BlockPos, TileMatterReplicator> replicators = new HashMap<>();
	private HashMap<BlockPos, TilePatternStorage> patternDrives = new HashMap<>();
	private HashMap<BlockPos, TilePatternMonitor> patternMonitors = new HashMap<>();

	public NetworkMatter(List<? extends AbstractCableTile<?>> varCables) {
		super(varCables, false);
	}

	public NetworkMatter(Collection<? extends AbstractCableNetwork> networks) {
		super(networks, false);
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
		analyzers = new HashMap<>();
		replicators = new HashMap<>();
		patternDrives = new HashMap<>();
		patternMonitors = new HashMap<>();
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
				BlockEntity acceptor = tileEntity.getLevel()
						.getBlockEntity(tileEntity.getBlockPos().relative(direction));
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
		BlockPos pos = entity.getBlockPos();
		if (entity instanceof TileMatterAnalyzer analyzer) {
			analyzers.put(pos, analyzer);
		} else if (entity instanceof TileMatterReplicator replicator) {
			replicators.put(pos, replicator);
		} else if (entity instanceof TilePatternMonitor monitor) {
			patternMonitors.put(pos, monitor);
		} else if (entity instanceof TilePatternStorage drive) {
			patternDrives.put(pos, drive);
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
		return new NetworkMatter(networks);
	}

	@Override
	public AbstractCableNetwork newInstance(List<? extends AbstractCableTile<?>> cables, boolean client) {
		return new NetworkMatter(cables);
	}

	public List<TileMatterAnalyzer> getAnalyzers() {
		return new ArrayList<>(analyzers.values());
	}

	public List<TileMatterReplicator> getReplicators() {
		return new ArrayList<>(replicators.values());
	}

	public List<TilePatternStorage> getPatternDrives() {
		return new ArrayList<>(patternDrives.values());
	}

	public List<TilePatternMonitor> getPatternMonitors() {
		return new ArrayList<>(patternMonitors.values());
	}

	public CompoundTag serializeNetworkNbt() {
		CompoundTag data = new CompoundTag();

		List<TilePatternStorage> drives = new ArrayList<>(patternDrives.values());
		Iterator<TilePatternStorage> storages = drives.iterator();
		TilePatternStorage storage;
		while (storages.hasNext()) {
			storage = storages.next();
			if (storage == null || storage.isRemoved()) {
				storages.remove();
			}
		}
		data.putInt("drivesize", drives.size());
		for (int i = 0; i < drives.size(); i++) {
			storage = drives.get(i);
			data.put("drivedata" + i, storage.getNetworkData());
			data.put("drivepos" + i, NbtUtils.writeBlockPos(storage.getBlockPos()));
		}

		List<TileMatterReplicator> replicators = new ArrayList<>(this.replicators.values());
		Iterator<TileMatterReplicator> reps = replicators.iterator();
		TileMatterReplicator replicator;
		while (reps.hasNext()) {
			replicator = reps.next();
			if (replicator == null || replicator.isRemoved()) {
				reps.remove();
			}
		}
		data.putInt("replicatorsize", replicators.size());
		for (int i = 0; i < replicators.size(); i++) {
			replicator = replicators.get(i);
			data.put("repdata" + i, replicator.getNetworkData());
			data.put("reppos" + i, NbtUtils.writeBlockPos(replicator.getBlockPos()));
		}

		return data;
	}

	public List<ItemPatternWrapper> getStoredPatterns(boolean checkPowered) {
		List<ItemPatternWrapper> patterns = new ArrayList<>();
		for (TilePatternStorage storage : patternDrives.values()) {
			if (storage != null && !storage.isRemoved() && checkPowered ? storage.isPowered(false) : true) {
				for (ItemPatternWrapper[] wrapperArr : storage.getWrappers()) {
					for (ItemPatternWrapper wrapper : wrapperArr) {
						if (wrapper.isNotAir()) {
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
	 * @return A 4 int array representing the drive's location in the ArrayList, and
	 *         the relative coordinates on the Storage itself. return[0] will be -1
	 *         if no pattern is found.
	 */
	public int[] getHighestStorageLocationForItem(Item item, boolean checkPowered) {

		int[] highestStorageLoc = { -1, -1, -1, -1 };
		int[] currData;
		int counter = 0;
		for (TilePatternStorage drive : patternDrives.values()) {
			// Ternary in an if statement fight me
			if (drive != null && !drive.isRemoved() && checkPowered ? drive.isPowered(false) : true) {
				currData = drive.getHighestStorageLocForItem(item);
				if (currData[2] > highestStorageLoc[3]) {
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
			return (TilePatternStorage) patternDrives.values().toArray()[index];
		} catch (Exception e) {
			MatterOverdrive.LOGGER.info("Attempted to access drive at index " + index + " for network "
					+ this.toString() + "; returning null");
			return null;
		}

	}

	// Serverside only
	public boolean storeItemFirstChance(Item item, int amt, boolean checkPowered) {
		for (TilePatternStorage drive : patternDrives.values()) {
			if (drive != null && !drive.isRemoved() && checkPowered ? drive.isPowered(false) : true) {
				if (drive.storeItemFirstChance(item, amt)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @param pattern the item ordered
	 * @param count   MUST BE GREATER THAN ZERO
	 * @return whether the request could be made
	 */
	public boolean postOrderToNetwork(ItemPatternWrapper pattern, int count, boolean checkPowered) {
		int smallestQueue = -1;
		TileMatterReplicator found = null;
		for (TileMatterReplicator replicator : replicators.values()) {
			if (replicator != null && !replicator.isRemoved() && checkPowered ? replicator.isPowered(false) : true) {
				int queueSize = replicator.getCurrOrders();
				if (queueSize > smallestQueue) {
					smallestQueue = queueSize;
					found = replicator;
					if (queueSize == 0) {
						found.queueOrder(new QueuedReplication(pattern, count));
						return true;
					}
				}
			}
		}
		if (found != null) {
			found.queueOrder(new QueuedReplication(pattern, count));
			return true;
		}
		return false;
	}

}
