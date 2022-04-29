package matteroverdrive.core.inventory.slot;

import javax.annotation.Nullable;

import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.screen.component.ScreenComponentIcon.IconType;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import matteroverdrive.core.utils.UtilsItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SlotRestricted extends SlotGeneric {

	@Nullable
	private Item[] validItems;

	public SlotRestricted(CapabilityInventory inventory, int index, int x, int y, int[] screenNumbers, SlotType type,
			IconType icon, Item... validItems) {
		super(inventory, index, x, y, screenNumbers, type, icon);
		this.validItems = validItems;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		if (validItems != null) {
			for (Item item : validItems) {
				if (UtilsItem.compareItems(item, stack.getItem())) {
					return super.mayPlace(stack);
				}
			}
		}
		return false;
	}

}
