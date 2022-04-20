package matteroverdrive.core.inventory.slot;

import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.screen.component.ScreenComponentIcon.IconType;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import net.minecraft.world.item.ItemStack;

public class SlotRestricted extends SlotGeneric {

	public SlotRestricted(CapabilityInventory inventory, int index, int x, int y, int[] screenNumbers, SlotType type,
			IconType icon) {
		super(inventory, index, x, y, screenNumbers, type, icon);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return false;
	}

}
