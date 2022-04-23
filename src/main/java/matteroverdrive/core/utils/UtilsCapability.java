package matteroverdrive.core.utils;

import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;

public class UtilsCapability {

	public static boolean hasEnergyCap(ItemStack stack) {
		return stack.getCapability(CapabilityEnergy.ENERGY).isPresent();
	}
	
	public static boolean hasMatterCap(ItemStack stack) {
		return stack.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE).isPresent();
	}

}
