package matteroverdrive.core.inventory.slot;

import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.screen.component.ScreenComponentIcon.IconType;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class SlotEnergyCharging extends SlotGeneric {

	public SlotEnergyCharging(CapabilityInventory inventory, int index, int xPosition, int yPosition,
			int[] screenNumbers) {
		super(inventory, index, xPosition, yPosition, screenNumbers, SlotType.BIG, IconType.ENERGY_DARK);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		if (stack.getCapability(ForgeCapabilities.ENERGY).isPresent()) {
			return super.mayPlace(stack);
		}
		return false;
	}

}
