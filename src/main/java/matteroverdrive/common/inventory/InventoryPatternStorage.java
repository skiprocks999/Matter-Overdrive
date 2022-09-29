package matteroverdrive.common.inventory;

import matteroverdrive.common.tile.matter_network.TilePatternStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.capability.types.item.PlayerSlotDataWrapper;
import matteroverdrive.core.inventory.GenericInventoryTile;
import matteroverdrive.core.inventory.slot.SlotEnergyCharging;
import matteroverdrive.core.inventory.slot.SlotRestricted;
import matteroverdrive.core.screen.component.ScreenComponentIcon.IconType;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import matteroverdrive.registry.ItemRegistry;
import matteroverdrive.registry.MenuRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class InventoryPatternStorage extends GenericInventoryTile<TilePatternStorage> {

	public InventoryPatternStorage(int id, Inventory playerinv, CapabilityInventory invcap, ContainerData tilecoords) {
		super(MenuRegistry.MENU_PATTERN_STORAGE.get(), id, playerinv, invcap, tilecoords);
	}

	public InventoryPatternStorage(int id, Inventory playerinv) {
		this(id, playerinv, new CapabilityInventory(TilePatternStorage.SLOT_COUNT, true, true),
				new SimpleContainerData(3));
	}

	@Override
	public void addInvSlots(CapabilityInventory invcap, Inventory playerinv) {
		addSlot(new SlotRestricted(invcap, nextIndex(), 71, 38, new int[] { 0 }, SlotType.BIG,
				IconType.PATTERN_DRIVE_DARK, ItemRegistry.ITEM_PATTERN_DRIVE.get()));
		addSlot(new SlotRestricted(invcap, nextIndex(), 96, 38, new int[] { 0 }, SlotType.BIG,
				IconType.PATTERN_DRIVE_DARK, ItemRegistry.ITEM_PATTERN_DRIVE.get()));
		addSlot(new SlotRestricted(invcap, nextIndex(), 121, 38, new int[] { 0 }, SlotType.BIG,
				IconType.PATTERN_DRIVE_DARK, ItemRegistry.ITEM_PATTERN_DRIVE.get()));
		addSlot(new SlotRestricted(invcap, nextIndex(), 71, 63, new int[] { 0 }, SlotType.BIG,
				IconType.PATTERN_DRIVE_DARK, ItemRegistry.ITEM_PATTERN_DRIVE.get()));
		addSlot(new SlotRestricted(invcap, nextIndex(), 96, 63, new int[] { 0 }, SlotType.BIG,
				IconType.PATTERN_DRIVE_DARK, ItemRegistry.ITEM_PATTERN_DRIVE.get()));
		addSlot(new SlotRestricted(invcap, nextIndex(), 121, 63, new int[] { 0 }, SlotType.BIG,
				IconType.PATTERN_DRIVE_DARK, ItemRegistry.ITEM_PATTERN_DRIVE.get()));

		addSlot(new SlotRestricted(invcap, nextIndex(), 8, 40, new int[] { 0 }, SlotType.BIG,
				IconType.MATTER_SCANNER_DARK, ItemRegistry.ITEM_MATTER_SCANNER.get()));

		addSlot(new SlotRestricted(invcap, nextIndex(), 8, 88, new int[] { 0 }, SlotType.BIG, IconType.NONE));

		addSlot(new SlotEnergyCharging(invcap, nextIndex(), 8, 115, new int[] { 0 }));
	}
	
	@Override
	public PlayerSlotDataWrapper getDataWrapper(Player player) {
		return defaultOverdriveScreen(new int[] { 0, 1 }, new int[] { 0 });
	}

}
