package matteroverdrive.common.inventory;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.tile.TileMatterDecomposer;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.inventory.GenericInventoryTile;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class InventoryMatterDecomposer extends GenericInventoryTile<TileMatterDecomposer> {
	
	public InventoryMatterDecomposer(int id, Inventory playerinv, CapabilityInventory invcap, ContainerData tilecoords) {
		super(DeferredRegisters.MENU_MATTER_DECOMPOSER.get(), id, playerinv, invcap, tilecoords);
	}
	
	public InventoryMatterDecomposer(int id, Inventory playerinv) {
		this(id, playerinv, new CapabilityInventory(TileMatterDecomposer.SLOT_COUNT), new SimpleContainerData(3));
	}

	@Override
	public void addInvSlots(CapabilityInventory invcap, Inventory playerinv) {
		// Upgrades: Speed, Hyper Speed, Power Reduction, Power Storage, Matter Storage, Failure Reduction, Muffler
		
	}

	@Override
	public int[] getHotbarNumbers() {
		return new int[] {0, 1, 2};
	}

	@Override
	public int[] getPlayerInvNumbers() {
		return new int[] {0};
	}

}
