package matteroverdrive.core.capability.types.item;

import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;

public record PlayerSlotDataWrapper(int pInvStartX, int pInvStartY, int pInvSlotW, int pInvSlotH, int hotbarStartX,
		int hotbarStartY, int hotbarSlotW, int hotbarSlotH, SlotType pInvSlotType, SlotType hotbarSlotType,
		int[] hotbarNumbers, int[] pInvNumbers) {

}
