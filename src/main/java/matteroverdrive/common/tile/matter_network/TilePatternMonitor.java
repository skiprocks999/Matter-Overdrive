package matteroverdrive.common.tile.matter_network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.block.states.OverdriveBlockStates;
import matteroverdrive.common.block.states.OverdriveBlockStates.VerticalFacing;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryPatternMonitor;
import matteroverdrive.common.network.NetworkMatter;
import matteroverdrive.common.tile.matter_network.TilePatternStorage.PatternStorageDataWrapper;
import matteroverdrive.common.tile.matter_network.matter_replicator.TileMatterReplicator;
import matteroverdrive.common.tile.matter_network.matter_replicator.TileMatterReplicator.MatterReplicatorDataWrapper;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.capability.types.item_pattern.ItemPatternWrapper;
import matteroverdrive.core.network.utils.IMatterNetworkMember;
import matteroverdrive.core.packet.NetworkHandler;
import matteroverdrive.core.packet.type.serverbound.PacketQueueReplication;
import matteroverdrive.core.tile.GenericTile;
import matteroverdrive.core.utils.UtilsDirection;
import matteroverdrive.core.utils.UtilsTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TilePatternMonitor extends GenericTile implements IMatterNetworkMember {

	private HashMap<BlockPos, PatternStorageDataWrapper> clientPatternStorageData = new HashMap<>();
	private HashMap<BlockPos, MatterReplicatorDataWrapper> clientMatterReplicatorData = new HashMap<>();
	
	public TilePatternMonitor(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_PATTERN_MONITOR.get(), pos, state);
		addCapability(new CapabilityInventory(0, false, false));
		setMenuProvider(
				new SimpleMenuProvider(
						(id, inv, play) -> new InventoryPatternMonitor(id, play.getInventory(),
								exposeCapability(CapabilityType.Item), getCoordsData()),
						getContainerName(TypeMachine.PATTERN_MONITOR.id())));
		setTickable();
		setHasMenuData();
	}
	
	@Override
	public void tickServer() {
		if(getTicks() % 4 == 0) {
			if(getConnectedNetwork() != null) {
				UtilsTile.updateLit(this, true);
			} else {
				UtilsTile.updateLit(this, false);
			}
		}
	}

	@Override
	public boolean canConnectToFace(Direction face) {
		VerticalFacing vertical = getBlockState().getValue(OverdriveBlockStates.VERTICAL_FACING);
		if(vertical == null || vertical == VerticalFacing.NONE) {
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
		if(vertical == null || vertical == VerticalFacing.NONE) {
			back = UtilsDirection.getRelativeSide(Direction.NORTH, handleEastWest(getFacing()));
		} else {
			back = vertical.mapped.getOpposite();
		}
		BlockEntity entity = getLevel().getBlockEntity(getBlockPos().relative(back));
		if(entity != null && entity instanceof TileMatterNetworkCable cable) {
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
		for(int i = 0; i < data.getInt("drivesize"); i++) {
			pos = NbtUtils.readBlockPos(data.getCompound("drivepos" + i));
			entity = world.getBlockEntity(pos);
			if(entity != null && entity instanceof TilePatternStorage storage) {
				clientPatternStorageData.put(new BlockPos(pos), storage.handleNetworkData(data.getCompound("drivedata" + i)));
				data.remove("drivepos" + i);
				data.remove("drivedata" + i);
			}
		}
		clientMatterReplicatorData = new HashMap<>();
		for(int i = 0; i < data.getInt("replicatorsize"); i++) {
			pos = NbtUtils.readBlockPos(data.getCompound("reppos" + i));
			entity = world.getBlockEntity(pos);
			if(entity != null && entity instanceof TileMatterReplicator replicator) {
				clientMatterReplicatorData.put(new BlockPos(pos), replicator.handleNetworkData(data.getCompound("repdata" + i)));
				data.remove("reppos" + i);
				data.remove("repdata" + i);
			}
		}
	}
	
	public List<ItemPatternWrapper> getStoredPatterns(boolean checkPowered){
		List<ItemPatternWrapper> patterns = new ArrayList<>();
		PatternStorageDataWrapper wrapper;
		for(Entry<BlockPos, PatternStorageDataWrapper> entry : clientPatternStorageData.entrySet()) {
			if(entry != null) {
				wrapper = entry.getValue();
				if(wrapper != null && checkPowered ? wrapper.isPowered() : true) {
					patterns.addAll(wrapper.getPatterns());
				}
			}
		}
		return patterns;
	}
	
	public boolean postOrderToNetwork(ItemPatternWrapper pattern, int count, boolean checkPowered) {
		int smallestQueue = -1;
		Entry<BlockPos, MatterReplicatorDataWrapper> val = null;
		MatterReplicatorDataWrapper wrapper;
		int queueSize;
		for(Entry<BlockPos, MatterReplicatorDataWrapper> entry : clientMatterReplicatorData.entrySet()) {
			if(entry != null) {
				wrapper = entry.getValue();
				if(entry != null && checkPowered ? wrapper.isPowered() : true) {
					queueSize = wrapper.getOrders().size();
					if(queueSize > smallestQueue) {
						smallestQueue = queueSize;
						val = entry;
						if(queueSize == 0) {
							NetworkHandler.CHANNEL.sendToServer(new PacketQueueReplication(entry.getKey(), pattern, count));
							return true;
						}
					} 
				}
			}
		}
		if(val != null) {
			NetworkHandler.CHANNEL.sendToServer(new PacketQueueReplication(val.getKey(), pattern, count));
			return true;
		}
		return false;
	}

}
