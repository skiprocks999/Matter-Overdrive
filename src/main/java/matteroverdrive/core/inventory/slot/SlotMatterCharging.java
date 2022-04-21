package matteroverdrive.core.inventory.slot;

import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.screen.component.ScreenComponentIcon.IconType;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import net.minecraft.world.item.ItemStack;

public class SlotMatterCharging extends SlotGeneric {

	public SlotMatterCharging(CapabilityInventory inventory, int index, int xPosition, int yPosition,
			int[] screenNumbers) {
		super(inventory, index, xPosition, yPosition, screenNumbers, SlotType.BIG, IconType.MATTER_DARK);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		if (stack.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE).isPresent()) {
			return super.mayPlace(stack);
		}
		return false;
	}

}
