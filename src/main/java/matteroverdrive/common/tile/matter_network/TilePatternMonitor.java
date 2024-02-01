package matteroverdrive.common.tile.matter_network;

import matteroverdrive.common.block.OverdriveBlockStates;
import matteroverdrive.common.block.OverdriveBlockStates.VerticalFacing;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryPatternMonitor;
import matteroverdrive.common.network.NetworkMatter;
import matteroverdrive.common.tile.matter_network.TilePatternStorage.PatternStorageDataWrapper;
import matteroverdrive.common.tile.matter_network.matter_replicator.TileMatterReplicator;
import matteroverdrive.common.tile.matter_network.matter_replicator.TileMatterReplicator.MatterReplicatorDataWrapper;
import matteroverdrive.common.tile.matter_network.matter_replicator.utils.QueuedReplication;
import matteroverdrive.common.tile.matter_network.matter_replicator.utils.ReplicatorOrderManager;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.capability.types.item_pattern.ItemPatternWrapper;
import matteroverdrive.core.network.utils.IMatterNetworkMember;
import matteroverdrive.core.packet.NetworkHandler;
import matteroverdrive.core.packet.type.serverbound.misc.PacketQueueReplication;
import matteroverdrive.core.tile.types.GenericTickingTile;
import matteroverdrive.core.utils.UtilsDirection;
import matteroverdrive.core.utils.UtilsTile;
import matteroverdrive.registry.TileRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;

public class TilePatternMonitor extends GenericTickingTile implements IMatterNetworkMember {

	private Map<BlockPos, PatternStorageDataWrapper> clientPatternStorageData = new HashMap<>();
	private Map<BlockPos, MatterReplicatorDataWrapper> clientMatterReplicatorData = new HashMap<>();
	private static final Map<BlockPos, ReplicatorOrderManager> orderManagers = new HashMap<>();

	public TilePatternMonitor(BlockPos pos, BlockState state) {
		super(TileRegistry.TILE_PATTERN_MONITOR.get(), pos, state);
		setMenuProvider(
				new SimpleMenuProvider(
						(id, inv, play) -> new InventoryPatternMonitor(id, play.getInventory(),
								CapabilityInventory.EMPTY, getCoordsData()),
						getContainerName(TypeMachine.PATTERN_MONITOR.id())));
		setTickable();
	}

	@Override
	public void tickServer() {
		boolean currState = getLevel().getBlockState(getBlockPos()).getValue(BlockStateProperties.LIT);
		if (currState ^ getConnectedNetwork() != null) {
			UtilsTile.updateLit(this, getConnectedNetwork() != null);
			setShouldSaveData(updateTickable(false));
		}
	}

	@Override
	public boolean canConnectToFace(Direction face) {
		VerticalFacing vertical = getBlockState().getValue(OverdriveBlockStates.VERTICAL_FACING);
		if (vertical == VerticalFacing.NONE) {
			Direction relative = UtilsDirection.getRelativeSide(Direction.NORTH, handleEastWest(getFacing()));
			return relative == face;
		} else {
			return face == vertical.mapped.getOpposite();
		}

	}

	@Override
	@Nullable
	public NetworkMatter getConnectedNetwork() {
		VerticalFacing vertical = getBlockState().getValue(OverdriveBlockStates.VERTICAL_FACING);
		Direction back;
		if (vertical == VerticalFacing.NONE) {
			back = UtilsDirection.getRelativeSide(Direction.NORTH, handleEastWest(getFacing()));
		} else {
			back = vertical.mapped.getOpposite();
		}
		BlockEntity entity = getLevel().getBlockEntity(getBlockPos().relative(back));
		if (entity instanceof TileMatterNetworkCable cable) {
			return (NetworkMatter) cable.getNetwork(false);
		}
		return null;
	}

	@Override
	public boolean isPowered(boolean client) {
		return true;
	}

	public void handleNetworkData(CompoundTag data, Level world) {
		clientPatternStorageData = new HashMap<>();
		BlockEntity entity;
		BlockPos pos;
		for (int i = 0; i < data.getInt("drivesize"); i++) {
			pos = NbtUtils.readBlockPos(data.getCompound("drivepos" + i));
			entity = world.getBlockEntity(pos);
			if (entity instanceof TilePatternStorage storage) {
				clientPatternStorageData.put(new BlockPos(pos),
						storage.handleNetworkData(data.getCompound("drivedata" + i)));
				data.remove("drivepos" + i);
				data.remove("drivedata" + i);
			}
		}
		clientMatterReplicatorData = new HashMap<>();
		for (int i = 0; i < data.getInt("replicatorsize"); i++) {
			pos = NbtUtils.readBlockPos(data.getCompound("reppos" + i));
			entity = world.getBlockEntity(pos);
			if (entity instanceof TileMatterReplicator replicator) {
				clientMatterReplicatorData.put(new BlockPos(pos),
						replicator.handleNetworkData(data.getCompound("repdata" + i)));

				// Save all order managers (these might be able to be retrieved from the
				// CMRD above, but I don't see a function), for future querying.
				orderManagers.put(new BlockPos(pos), replicator.getOrderManager());

				data.remove("reppos" + i);
				data.remove("repdata" + i);
			}
		}
	}

	public List<ItemPatternWrapper> getStoredPatterns(boolean checkPowered) {
		List<ItemPatternWrapper> patterns = new ArrayList<>();
		PatternStorageDataWrapper wrapper;
		for (Entry<BlockPos, PatternStorageDataWrapper> entry : clientPatternStorageData.entrySet()) {
			if (entry != null) {
				wrapper = entry.getValue();
				if (wrapper == null || !checkPowered || wrapper.isPowered()) {
					patterns.addAll(wrapper.getPatterns());
				}
			}
		}
		return patterns;
	}

	public int getNumOrdersFromReplicators() {
		return orderManagers.values().stream()
			.filter(Objects::nonNull)
			.map(ReplicatorOrderManager::size)
			.reduce(0, Integer::sum);
	}

	public boolean postOrderToNetwork(ItemPatternWrapper pattern, int count, boolean checkPowered, boolean checkFused) {
		int smallestQueue = -1;
		Entry<BlockPos, MatterReplicatorDataWrapper> val = null;
		MatterReplicatorDataWrapper wrapper;
		int queueSize;
		for (Entry<BlockPos, MatterReplicatorDataWrapper> entry : clientMatterReplicatorData.entrySet()) {
			if (entry != null) {
				wrapper = entry.getValue();
				if ((!checkPowered || wrapper.isPowered()) && (!checkFused || !wrapper.isFused())) {
					queueSize = wrapper.getOrders().size();
					if (queueSize > smallestQueue) {
						smallestQueue = queueSize;
						val = entry;
						if (queueSize == 0) {
							NetworkHandler.sendToServer(new PacketQueueReplication(entry.getKey(), pattern, count));
							return true;
						}
					}
				}
			}
		}
		if (val != null) {
			NetworkHandler.sendToServer(new PacketQueueReplication(val.getKey(), pattern, count));
			return true;
		}
		return false;
	}

	public List<QueuedReplication> getGlobalOrders(boolean checkPowered, boolean checkFused) {
		List<QueuedReplication> orders = new ArrayList<>();
		MatterReplicatorDataWrapper wrapper;
		for (Entry<BlockPos, MatterReplicatorDataWrapper> entry : clientMatterReplicatorData.entrySet()) {
			if (entry != null) {
				wrapper = entry.getValue();
				if (entry != null && checkPowered ? wrapper.isPowered()
							: true && checkFused ? !wrapper.isFused() : true) {
					orders.addAll(wrapper.getOrders());
				}
			}
		}

		return orders;
	}
}
