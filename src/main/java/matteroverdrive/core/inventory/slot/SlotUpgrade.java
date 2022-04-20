package matteroverdrive.core.inventory.slot;

import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.screen.component.ScreenComponentIcon.IconType;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;

public class SlotUpgrade extends SlotGeneric {

	public SlotUpgrade(CapabilityInventory inventory, int index, int xPosition, int yPosition, int[] screenNumbers) {
		super(inventory, index, xPosition, yPosition, screenNumbers, SlotType.BIG, IconType.UPGRADE);
	}

}
