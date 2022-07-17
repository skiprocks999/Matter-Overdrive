package matteroverdrive.core.inventory.slot;

import javax.annotation.Nullable;

import matteroverdrive.core.screen.component.ScreenComponentIcon.IconType;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class SlotContainer extends Slot implements IToggleableSlot {

	private SlotType type;
	@Nullable
	private IconType icon;
	private int[] screenNumbers;
	private boolean isActive;

	public SlotContainer(Container pContainer, int pIndex, int pX, int pY, int[] screenNumbers, SlotType type,
			IconType icon) {
		super(pContainer, pIndex, pX, pY);
		this.type = type;
		this.screenNumbers = screenNumbers;
		this.icon = icon;
	}

	public SlotContainer(Container pContainer, int pIndex, int pX, int pY, int[] screenNumbers, SlotType type) {
		this(pContainer, pIndex, pX, pY, screenNumbers, type, IconType.NONE);
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
		for (int num : screenNumbers) {
			if (num == number)
				return true;
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

	@Override
	public IconType getIconType() {
		return icon;
	}

}
