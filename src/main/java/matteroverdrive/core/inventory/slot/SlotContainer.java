package matteroverdrive.core.inventory.slot;

import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import matteroverdrive.core.screen.component.utils.ISlotType;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class SlotContainer extends Slot implements ISlotType {

	public SlotContainer(Container pContainer, int pIndex, int pX, int pY) {
		super(pContainer, pIndex, pX, pY);
	}

	@Override
	public SlotType getSlotType() {
		return SlotType.GENERIC;
	}

}
