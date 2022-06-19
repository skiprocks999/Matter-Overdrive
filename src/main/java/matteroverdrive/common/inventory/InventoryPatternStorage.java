package matteroverdrive.common.inventory;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.tile.matter_network.TilePatternStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.inventory.GenericInventoryTile;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class InventoryPatternStorage extends GenericInventoryTile<TilePatternStorage> {

	public InventoryPatternStorage(int id, Inventory playerinv, CapabilityInventory invcap,
			ContainerData tilecoords) {
		super(DeferredRegisters.MENU_PATTERN_STORAGE.get(), id, playerinv, invcap, tilecoords);
	}
	
	public InventoryPatternStorage(int id, Inventory playerinv) {
		this(id, playerinv, new CapabilityInventory(TilePatternStorage.SLOT_COUNT, true, true), new SimpleContainerData(3));
	}

	@Override
	public void addInvSlots(CapabilityInventory invcap, Inventory playerinv) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int[] getHotbarNumbers() {
		return new int[] {};
	}

	@Override
	public int[] getPlayerInvNumbers() {
		return new int[] {};
	}

}
