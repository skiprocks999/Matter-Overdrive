package matteroverdrive.core.matter;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.core.utils.UtilsItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.CapabilityItemHandler;

public class MatterUtils {

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

}
