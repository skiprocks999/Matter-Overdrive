package matteroverdrive.core.inventory.slot;

import matteroverdrive.common.item.ItemUpgrade;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.screen.component.ScreenComponentIcon.IconType;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import net.minecraft.world.item.ItemStack;

public class SlotUpgrade extends SlotGeneric {

	public SlotUpgrade(CapabilityInventory inventory, int index, int xPosition, int yPosition, int[] screenNumbers) {
		super(inventory, index, xPosition, yPosition, screenNumbers, SlotType.BIG, IconType.UPGRADE_DARK);
	}
	
	@Override
	public boolean mayPlace(ItemStack stack) {
		if(stack.getItem() instanceof ItemUpgrade) {
			return super.mayPlace(stack);
		}
		return false;
	}

}
