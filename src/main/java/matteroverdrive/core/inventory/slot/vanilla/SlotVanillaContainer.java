package matteroverdrive.core.inventory.slot.vanilla;

import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import matteroverdrive.core.screen.component.utils.ISlotType;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class SlotVanillaContainer extends Slot implements ISlotType {

	public SlotVanillaContainer(Container pContainer, int pIndex, int pX, int pY) {
		super(pContainer, pIndex, pX, pY);
	}

	@Override
	public SlotType getSlotType() {
		return SlotType.VANILLA;
	}

}
