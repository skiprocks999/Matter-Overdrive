package matteroverdrive.common.inventory;

import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.inventory.GenericInventoryTile;
import matteroverdrive.core.inventory.slot.SlotUpgrade;
import matteroverdrive.registry.MenuRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import matteroverdrive.common.item.ItemUpgrade.UpgradeType;
import matteroverdrive.common.tile.TileCharger;

public class InventoryCharger extends GenericInventoryTile<TileCharger> {

	public static final UpgradeType[] UPGRADES = new UpgradeType[] { UpgradeType.SPEED, UpgradeType.HYPER_SPEED,
			UpgradeType.POWER, UpgradeType.POWER_STORAGE, UpgradeType.RANGE };

	public InventoryCharger(int id, Inventory playerinv, CapabilityInventory invcap, ContainerData tilecoords) {
		super(MenuRegistry.MENU_CHARGER.get(), id, playerinv, invcap, tilecoords);
	}

	public InventoryCharger(int id, Inventory playerinv) {
		this(id, playerinv, new CapabilityInventory(TileCharger.SLOT_COUNT, true, true), new SimpleContainerData(3));
	}

	@Override
	public void addInvSlots(CapabilityInventory invcap, Inventory playerinv) {
		addSlot(new SlotUpgrade(invcap, nextIndex(), 105, 55, new int[] { 2 }, UPGRADES));
		addSlot(new SlotUpgrade(invcap, nextIndex(), 129, 55, new int[] { 2 }, UPGRADES));
	}

	@Override
	public int[] getHotbarNumbers() {
		return new int[] { 0, 1, 2 };
	}

	@Override
	public int[] getPlayerInvNumbers() {
		return new int[] { 0 };
	}

}
