package matteroverdrive.core.inventory.slot;

import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotGeneric extends SlotItemHandler implements IToggleableSlot {

	private SlotType type;
	private boolean isActive;
	private int[] screenNumbers;

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

	@Override
	public void setActive(boolean active) {
		isActive = active;
	}

	@Override
	public void setScreenNumber(int[] numbers) {
		screenNumbers = numbers;
	}

	@Override
	public boolean isScreenNumber(int number) {
		for(int num : screenNumbers) {
			if (num == number) return true;
		}
		return false;
	}

	@Override
	public int[] getScreenNumbers() {
		return screenNumbers;
	}
	
	@Override
	public boolean isActive() {
		return isActive;
	}
	
}
