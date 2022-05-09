package matteroverdrive.core.utils;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.block.cable.BlockMatterConduit;
import matteroverdrive.common.tile.TileMatterConduit;
import matteroverdrive.core.cable.api.EnumConnectType;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.matter.ICapabilityMatterStorage;
import matteroverdrive.core.tile.GenericTile;
import matteroverdrive.core.utils.misc.Scheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.CapabilityItemHandler;

public class UtilsMatter {

	public static boolean validateItem(ItemStack item) {

		if (item.isEnchanted())
			return false;
		if (item.isDamaged())
			return false;
		if (item.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent())
			return false;
		if (item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
			boolean isFilled = item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).resolve().get()
					.drain(Integer.MAX_VALUE, FluidAction.EXECUTE).getAmount() > 0;
			if (isFilled)
				return false;
		}
		if (item.getCapability(CapabilityEnergy.ENERGY).isPresent()) {
			IEnergyStorage storage = item.getCapability(CapabilityEnergy.ENERGY).resolve().get();
			if (storage.getEnergyStored() == 0)
				return false;
		}
		CompoundTag tag = BlockItem.getBlockEntityData(item);
		if (tag != null) {
			if (tag.contains("LootTable", 8))
				return false;
			if (tag.contains("Items", 9))
				return false;
		}

		return true;
	}

	public static boolean isDust(ItemStack item) {
		return isRawDust(item) || isRefinedDust(item);
	}

	public static boolean isRefinedDust(ItemStack item) {
		return UtilsItem.compareItems(item.getItem(), DeferredRegisters.ITEM_MATTER_DUST.get());
	}

	public static boolean isRawDust(ItemStack item) {
		return UtilsItem.compareItems(item.getItem(), DeferredRegisters.ITEM_RAW_MATTER_DUST.get());
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
			if (acceptor.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE, dir).isPresent()) {
				return true;
			}
		}
		return false;
	}
	
	public static double receiveMatter(BlockEntity acceptor, Direction direction, double perReceiver, boolean debug) {
		if (isMatterReceiver(acceptor, direction)) {
			LazyOptional<ICapabilityMatterStorage> cap = acceptor.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE, direction);
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
		for(Direction dir : Direction.values()) {
			offset = new BlockPos(pos.getX(), pos.getY(), pos.getZ()).relative(dir);
			BlockEntity entity = world.getBlockEntity(offset);
			if(entity != null && entity instanceof TileMatterConduit conduit) {
				updateMatterCable(offset, world, conduit, dir, tile);
			}
		}
	}
	
	private static void updateMatterCable(BlockPos offset, Level world, TileMatterConduit conduit, Direction dir, GenericTile tile) {
		Scheduler.schedule(1, () -> {
			conduit.refreshNetworkIfChange();
			BlockState state = world.getBlockState(offset);
			if (UtilsMatter.isMatterReceiver(tile, dir)) {
				state = state.setValue(BlockMatterConduit.FACING_TO_PROPERTY_MAP.get(dir.getOpposite()), EnumConnectType.INVENTORY);
			} else {
				state = state.setValue(BlockMatterConduit.FACING_TO_PROPERTY_MAP.get(dir.getOpposite()), EnumConnectType.NONE);
			}			
			world.setBlockAndUpdate(offset, state);
			});
	}

}
