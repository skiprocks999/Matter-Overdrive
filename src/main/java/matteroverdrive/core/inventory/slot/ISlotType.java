package matteroverdrive.core.inventory.slot;

import matteroverdrive.core.screen.component.ScreenComponentIcon.IconType;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;

public interface ISlotType {

	public SlotType getSlotType();

	public IconType getIconType();
}
