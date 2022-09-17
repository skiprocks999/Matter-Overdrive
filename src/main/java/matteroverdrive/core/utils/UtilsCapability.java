package matteroverdrive.core.utils;

import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class UtilsCapability {

	public static boolean hasEnergyCap(ItemStack stack) {
		return stack.getCapability(CapabilityEnergy.ENERGY).isPresent();
	}

	public static boolean hasMatterCap(ItemStack stack) {
		return stack.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE).isPresent();
	}
	
	public static void fillFluidCap(ItemStack stack, FluidStack fluid) {
		stack.getCapability(getFluidItemCap()).ifPresent(h -> h.fill(fluid, FluidAction.EXECUTE));
	}
	
	public static Capability<IFluidHandlerItem> getFluidItemCap() {
		return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
	}

}
