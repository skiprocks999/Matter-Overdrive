package matteroverdrive.core.inventory.slot;

import matteroverdrive.core.screen.component.ScreenComponentIcon.IconType;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import net.minecraftforge.items.IItemHandler;

public class SlotUpgrade extends SlotGeneric {

	public SlotUpgrade(IItemHandler itemHandler, int index, int xPosition, int yPosition, int[] screenNumbers) {
		super(itemHandler, index, xPosition, yPosition, screenNumbers, SlotType.BIG, IconType.UPGRADE);
	}

}
