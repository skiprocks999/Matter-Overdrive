package matteroverdrive.core.inventory.slot;

import matteroverdrive.common.item.ItemUpgrade;
import matteroverdrive.common.item.ItemUpgrade.UpgradeType;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.screen.component.ScreenComponentIcon.IconType;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import net.minecraft.world.item.ItemStack;

public class SlotUpgrade extends SlotGeneric {

	private UpgradeType[] types = {};

	public SlotUpgrade(CapabilityInventory inventory, int index, int xPosition, int yPosition, int[] screenNumbers,
			UpgradeType... types) {
		super(inventory, index, xPosition, yPosition, screenNumbers, SlotType.BIG, IconType.UPGRADE_DARK);
		this.types = types;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		if (stack.getItem() instanceof ItemUpgrade) {
			return super.mayPlace(stack);
		}
		return false;
	}

	public UpgradeType[] getUpgrades() {
		return types;
	}

}
