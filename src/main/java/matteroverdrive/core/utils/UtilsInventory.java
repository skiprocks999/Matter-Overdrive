/**
 * handleShiftClick() and mergeStack() are 
 * credited to AurilisDev https://github.com/aurilisdev/Electrodynamics
 */
package matteroverdrive.core.utils;

import java.util.List;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class UtilsInventory {

	public static ItemStack handleShiftClick(List<Slot> slots, Player player, int slotIndex) {
		Slot sourceSlot = slots.get(slotIndex);
		ItemStack inputStack = sourceSlot.getItem();
		if (inputStack == null) {
			return null;
		}

		boolean sourceIsPlayer = sourceSlot.container == player.getInventory();

		ItemStack copy = inputStack.copy();

		if (sourceIsPlayer) {
			if (!mergeStack(player.getInventory(), false, sourceSlot, slots, false)) {
				return ItemStack.EMPTY;
			}
			return copy;
		}
		boolean isMachineOutput = !sourceSlot.mayPlace(inputStack);
		if (!mergeStack(player.getInventory(), true, sourceSlot, slots, !isMachineOutput)) {
			return ItemStack.EMPTY;
		}
		return copy;
	}

	private static boolean mergeStack(Inventory playerInv, boolean mergeIntoPlayer, Slot sourceSlot, List<Slot> slots,
			boolean reverse) {
		ItemStack sourceStack = sourceSlot.getItem();

		int originalSize = sourceStack.getCount();

		int len = slots.size();
		int idx;
		if (sourceStack.isStackable()) {
			idx = reverse ? len - 1 : 0;

			while (sourceStack.getCount() > 0 && (reverse ? idx >= 0 : idx < len)) {
				Slot targetSlot = slots.get(idx);
				if (targetSlot.container == playerInv == mergeIntoPlayer) {
					ItemStack target = targetSlot.getItem();
					if (ItemStack.isSame(sourceStack, target)) {
						int targetMax = Math.min(targetSlot.getMaxStackSize(), target.getMaxStackSize());
						int toTransfer = Math.min(sourceStack.getCount(), targetMax - target.getCount());
						if (toTransfer > 0) {
							target.setCount(target.getCount() + toTransfer);
							sourceStack.setCount(sourceStack.getCount() - toTransfer);
							targetSlot.setChanged();
						}
					}
				}

				if (reverse) {
					idx--;
				} else {
					idx++;
				}
			}
			if (sourceStack.getCount() == 0) {
				sourceSlot.set(ItemStack.EMPTY);
				return true;
			}
		}

		idx = reverse ? len - 1 : 0;
		while (reverse ? idx >= 0 : idx < len) {
			Slot targetSlot = slots.get(idx);
			if (targetSlot.container == playerInv == mergeIntoPlayer && !targetSlot.hasItem()
					&& targetSlot.mayPlace(sourceStack)) {
				targetSlot.set(sourceStack.copy());
				sourceSlot.set(ItemStack.EMPTY);
				sourceStack.setCount(0);
				return true;
			}

			if (reverse) {
				idx--;
			} else {
				idx++;
			}
		}

		if (sourceStack.getCount() != originalSize) {
			sourceSlot.setChanged();
			return true;
		}
		return false;
	}

}
