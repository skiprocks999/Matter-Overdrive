package matteroverdrive.core.inventory.slot;

import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import matteroverdrive.core.screen.component.utils.ISlotType;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class SlotContainer extends Slot implements ISlotType {

	private SlotType type;

	public SlotContainer(Container pContainer, int pIndex, int pX, int pY, SlotType type) {
		super(pContainer, pIndex, pX, pY);
		this.type = type;
	}

	@Override
	public SlotType getSlotType() {
		return type;
	}

}
