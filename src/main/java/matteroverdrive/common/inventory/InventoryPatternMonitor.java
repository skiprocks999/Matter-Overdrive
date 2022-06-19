package matteroverdrive.common.inventory;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.tile.matter_network.TilePatternMonitor;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.inventory.GenericInventoryTile;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class InventoryPatternMonitor extends GenericInventoryTile<TilePatternMonitor> {

	public InventoryPatternMonitor(int id, Inventory playerinv, CapabilityInventory invcap,
			ContainerData tilecoords) {
		super(DeferredRegisters.MENU_PATTERN_MONITOR.get(), id, playerinv, invcap, tilecoords);
	}
	
	public InventoryPatternMonitor(int id, Inventory playerinv) {
		this(id, playerinv, new CapabilityInventory(0, false, false), new SimpleContainerData(3));
	}

	@Override
	public void addInvSlots(CapabilityInventory invcap, Inventory playerinv) {
		//unused
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
