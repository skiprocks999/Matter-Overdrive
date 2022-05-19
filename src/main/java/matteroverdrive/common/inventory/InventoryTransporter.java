package matteroverdrive.common.inventory;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.item.ItemUpgrade.UpgradeType;
import matteroverdrive.common.tile.TileTransporter;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.inventory.GenericInventoryTile;
import matteroverdrive.core.inventory.slot.SlotEnergyCharging;
import matteroverdrive.core.inventory.slot.SlotMatterCharging;
import matteroverdrive.core.inventory.slot.SlotRestricted;
import matteroverdrive.core.inventory.slot.SlotUpgrade;
import matteroverdrive.core.screen.component.ScreenComponentIcon.IconType;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class InventoryTransporter extends GenericInventoryTile<TileTransporter> {

	public static final UpgradeType[] UPGRADES = new UpgradeType[] { UpgradeType.SPEED, UpgradeType.HYPER_SPEED,
			UpgradeType.POWER, UpgradeType.POWER_STORAGE, UpgradeType.MATTER_STORAGE, UpgradeType.MUFFLER,
			UpgradeType.RANGE };

	public InventoryTransporter(int id, Inventory playerinv, CapabilityInventory invcap, ContainerData tilecoords) {
		super(DeferredRegisters.MENU_TRANSPORTER.get(), id, playerinv, invcap, tilecoords);
	}

	public InventoryTransporter(int id, Inventory playerinv) {
		this(id, playerinv, new CapabilityInventory(TileTransporter.SLOT_COUNT, false, false),
				new SimpleContainerData(3));
	}

	@Override
	public void addInvSlots(CapabilityInventory invcap, Inventory playerinv) {
		addSlot(new SlotRestricted(invcap, nextIndex(), 8, 78, new int[] { 4 }, SlotType.BIG, IconType.FLASHDRIVE_DARK,
				DeferredRegisters.ITEM_TRANSPORTER_FLASHDRIVE.get()));
		addSlot(new SlotEnergyCharging(invcap, nextIndex(), 8, 48, new int[] { 0 }));
		addSlot(new SlotMatterCharging(invcap, nextIndex(), 8, 107, new int[] { 0 }));
		addSlot(new SlotUpgrade(invcap, nextIndex(), 70, 55, new int[] { 2 }, UPGRADES));
		addSlot(new SlotUpgrade(invcap, nextIndex(), 94, 55, new int[] { 2 }, UPGRADES));
		addSlot(new SlotUpgrade(invcap, nextIndex(), 118, 55, new int[] { 2 }, UPGRADES));
		addSlot(new SlotUpgrade(invcap, nextIndex(), 142, 55, new int[] { 2 }, UPGRADES));
		addSlot(new SlotUpgrade(invcap, nextIndex(), 166, 55, new int[] { 2 }, UPGRADES));
	}

	@Override
	public int[] getHotbarNumbers() {
		return new int[] { 0, 1, 2, 3, 4 };
	}

	@Override
	public int[] getPlayerInvNumbers() {
		return new int[] {};
	}

}
