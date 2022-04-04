package matteroverdrive.core.inventory.slot;

import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import matteroverdrive.core.screen.component.utils.ISlotType;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotGeneric extends SlotItemHandler implements ISlotType {

	private SlotType type;

	public SlotGeneric(IItemHandler itemHandler, int index, int xPosition, int yPosition, SlotType type) {
		super(itemHandler, index, xPosition, yPosition);
		this.type = type;
	}

	public SlotGeneric(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		this(itemHandler, index, xPosition, yPosition, SlotType.SMALL);
	}

	@Override
	public SlotType getSlotType() {
		return type;
	}

}
