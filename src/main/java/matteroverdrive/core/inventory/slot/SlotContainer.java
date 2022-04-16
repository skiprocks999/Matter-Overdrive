package matteroverdrive.core.inventory.slot;

import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class SlotContainer extends Slot implements IToggleableSlot {

	private SlotType type;
	private int[] screenNumbers;
	private boolean isActive;

	public SlotContainer(Container pContainer, int pIndex, int pX, int pY, SlotType type) {
		super(pContainer, pIndex, pX, pY);
		this.type = type;
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
	public boolean isActive() {
		return isActive;
	}

	@Override
	public int[] getScreenNumbers() {
		return screenNumbers;
	}

}
