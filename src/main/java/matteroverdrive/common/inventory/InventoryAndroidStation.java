package matteroverdrive.common.inventory;

import matteroverdrive.common.tile.station.TileAndroidStation;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.inventory.GenericInventoryTile;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import matteroverdrive.registry.MenuRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class InventoryAndroidStation extends GenericInventoryTile<TileAndroidStation> {

	public InventoryAndroidStation(int id, Inventory playerinv, CapabilityInventory invcap, ContainerData tilecoords) {
		super(MenuRegistry.MENU_ANDROID_STATION.get(), id, playerinv, invcap, tilecoords, 45, 89, 45, 150, SlotType.SMALL, SlotType.SMALL);
	}

	public InventoryAndroidStation(int id, Inventory playerinv) {
		this(id, playerinv, CapabilityInventory.EMPTY, new SimpleContainerData(3));
	}

	@Override
	public void addInvSlots(CapabilityInventory invcap, Inventory playerinv) {
	}

	@Override
	public int[] getHotbarNumbers(Player player) {
		return new int[] { 0, 1, 2 };
	}

	@Override
	public int[] getPlayerInvNumbers(Player player) {
		return new int[] { 0 };
	}
}
