package matteroverdrive.common.inventory;

import matteroverdrive.common.item.ItemUpgrade.UpgradeType;
import matteroverdrive.common.tile.transporter.TileTransporter;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.capability.types.item.PlayerSlotDataWrapper;
import matteroverdrive.core.inventory.GenericInventoryTile;
import matteroverdrive.core.inventory.slot.SlotEnergyCharging;
import matteroverdrive.core.inventory.slot.SlotMatterCharging;
import matteroverdrive.core.inventory.slot.SlotRestricted;
import matteroverdrive.core.inventory.slot.SlotUpgrade;
import matteroverdrive.core.screen.component.ScreenComponentIcon.IconType;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import matteroverdrive.registry.ItemRegistry;
import matteroverdrive.registry.MenuRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class InventoryTransporter extends GenericInventoryTile<TileTransporter> {

	public static final UpgradeType[] UPGRADES = new UpgradeType[] { UpgradeType.SPEED, UpgradeType.HYPER_SPEED,
			UpgradeType.POWER, UpgradeType.POWER_STORAGE, UpgradeType.MATTER_STORAGE, UpgradeType.MUFFLER,
			UpgradeType.RANGE };

	public InventoryTransporter(int id, Inventory playerinv, CapabilityInventory invcap, ContainerData tilecoords) {
		super(MenuRegistry.MENU_TRANSPORTER.get(), id, playerinv, invcap, tilecoords);
	}

	public InventoryTransporter(int id, Inventory playerinv) {
		this(id, playerinv, new CapabilityInventory(TileTransporter.SLOT_COUNT, false, false),
				new SimpleContainerData(3));
	}

	@Override
	public void addInvSlots(CapabilityInventory invcap, Inventory playerinv) {
		addSlot(new SlotRestricted(invcap, nextIndex(), 8, 58, new int[] { 1 }, SlotType.BIG,
				IconType.COMMUNICATOR_DARK, ItemRegistry.ITEM_COMMUNICATOR.get()));
		addSlot(new SlotRestricted(invcap, nextIndex(), 8, 78, new int[] { 4 }, SlotType.BIG, IconType.FLASHDRIVE_DARK,
				ItemRegistry.ITEM_TRANSPORTER_FLASHDRIVE.get()));
		addSlot(new SlotRestricted(invcap, nextIndex(), 8, 106, new int[] { 1 }, SlotType.BIG, IconType.NONE));
		addSlot(new SlotEnergyCharging(invcap, nextIndex(), 8, 48, new int[] { 0 }));
		addSlot(new SlotMatterCharging(invcap, nextIndex(), 8, 107, new int[] { 0 }));
		addSlot(new SlotUpgrade(invcap, nextIndex(), 70, 55, new int[] { 2 }, UPGRADES));
		addSlot(new SlotUpgrade(invcap, nextIndex(), 94, 55, new int[] { 2 }, UPGRADES));
		addSlot(new SlotUpgrade(invcap, nextIndex(), 118, 55, new int[] { 2 }, UPGRADES));
		addSlot(new SlotUpgrade(invcap, nextIndex(), 142, 55, new int[] { 2 }, UPGRADES));
		addSlot(new SlotUpgrade(invcap, nextIndex(), 166, 55, new int[] { 2 }, UPGRADES));
	}

	@Override
	public PlayerSlotDataWrapper getDataWrapper(Player player) {
		return defaultOverdriveScreen(new int[] { 0, 1, 2, 3, 4 }, new int[] {});
	}

}
