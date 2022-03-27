package matteroverdrive.core.inventory.slot;

import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import matteroverdrive.core.screen.component.utils.ISlotType;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotGeneric extends SlotItemHandler implements ISlotType {

	public SlotGeneric(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	@Override
	public SlotType getSlotType() {
		return SlotType.GENERIC;
	}

}
