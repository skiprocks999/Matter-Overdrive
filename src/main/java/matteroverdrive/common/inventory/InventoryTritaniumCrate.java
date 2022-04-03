package matteroverdrive.common.inventory;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.tile.TileTritaniumCrate;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.inventory.GenericInventoryTile;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class InventoryTritaniumCrate extends GenericInventoryTile<TileTritaniumCrate> {

	public InventoryTritaniumCrate(int id, Inventory playerinv) {
		this(id, playerinv, new CapabilityInventory(TileTritaniumCrate.SIZE), new SimpleContainerData(3));
	}
	
	public InventoryTritaniumCrate(int id, Inventory playerinv, CapabilityInventory invcap, ContainerData coords) {
		super(DeferredRegisters.MENU_TRITANIUMCRATE.get(), id, playerinv, invcap, coords);
	}

	@Override
	public void addInvSlots(CapabilityInventory invcap, Inventory playerinv) {
		
	}

}
