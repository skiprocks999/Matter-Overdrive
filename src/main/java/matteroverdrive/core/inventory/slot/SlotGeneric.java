package matteroverdrive.core.inventory.slot;

import javax.annotation.Nullable;

import matteroverdrive.core.screen.component.ScreenComponentIcon.IconType;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotGeneric extends SlotItemHandler implements IToggleableSlot {

	private SlotType type;
	@Nullable
	private IconType icon;
	private boolean isActive;
	private int[] screenNumbers;

	public SlotGeneric(IItemHandler itemHandler, int index, int xPosition, int yPosition, int[] screenNumbers, SlotType type, IconType icon) {
		super(itemHandler, index, xPosition, yPosition);
		this.type = type;
		this.screenNumbers = screenNumbers;
		this.icon = icon;
	}

	public SlotGeneric(IItemHandler itemHandler, int index, int xPosition, int yPosition, int[] screenNumbers) {
		this(itemHandler, index, xPosition, yPosition, screenNumbers, SlotType.SMALL, null);
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

	@Override
	public IconType getIconType() {
		return icon;
	}
	
}
