package matteroverdrive.core.utils;

import matteroverdrive.common.block.cable.AbstractCableBlock;
import matteroverdrive.common.event.ServerEventHandler;
import matteroverdrive.common.tile.TileMatterConduit;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.matter.ICapabilityMatterStorage;
import matteroverdrive.core.tile.GenericTile;
import matteroverdrive.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;

public class UtilsMatter {

	public static boolean isDust(ItemStack item) {
		return isRawDust(item) || isRefinedDust(item);
	}

	public static boolean isRefinedDust(ItemStack item) {
		return UtilsItem.compareItems(item.getItem(), ItemRegistry.ITEM_MATTER_DUST.get());
	}

	public static boolean isRawDust(ItemStack item) {
		return UtilsItem.compareItems(item.getItem(), ItemRegistry.ITEM_RAW_MATTER_DUST.get());
	}

	public static boolean isMatterReceiver(BlockEntity acceptor) {
		for (Direction dir : Direction.values()) {
			boolean is = isMatterReceiver(acceptor, dir);
			if (is) {
				return true;
			}
		}
		return false;
	}

	public static boolean isMatterReceiver(BlockEntity acceptor, Direction dir) {
		if (acceptor != null) {
			return acceptor.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE, dir).isPresent();
		}
		return false;
	}

	public static double receiveMatter(BlockEntity acceptor, Direction direction, double perReceiver, boolean debug) {
		if (isMatterReceiver(acceptor, direction)) {
			LazyOptional<ICapabilityMatterStorage> cap = acceptor
					.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE, direction);
			if (cap.isPresent()) {
				ICapabilityMatterStorage handler = cap.resolve().get();
				if (handler.canReceive()) {
					return handler.receiveMatter(perReceiver, debug);
				}
			}
		}
		return 0;
	}

	public static void updateAdjacentMatterCables(GenericTile tile) {
		BlockPos pos = tile.getBlockPos();
		Level world = tile.getLevel();
		BlockPos offset;
		for (Direction dir : Direction.values()) {
			offset = pos.relative(dir);
			BlockEntity entity = world.getBlockEntity(offset);
			if (entity != null && entity instanceof TileMatterConduit conduit) {
				updateMatterCable(world, conduit);
			}
		}
	}

	private static void updateMatterCable(Level world, TileMatterConduit conduit) {
		ServerEventHandler.TASK_HANDLER.queueTask(() -> {
			BlockState conduitState = conduit.getBlockState();
			BlockPos conduitPos = conduit.getBlockPos();
			BlockState updatedState = ((AbstractCableBlock) conduitState.getBlock())
					.handleConnectionUpdate(conduitState, conduitPos, world);
			conduit.refreshNetworkIfChange();
			world.setBlockAndUpdate(conduitPos, updatedState);
		});
	}

}
