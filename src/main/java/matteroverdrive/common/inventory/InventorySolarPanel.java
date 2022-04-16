package matteroverdrive.common.inventory;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.tile.TileSolarPanel;
import matteroverdrive.common.tile.TileTritaniumCrate;
import matteroverdrive.core.inventory.GenericInventoryTile;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class InventorySolarPanel extends GenericInventoryTile<TileSolarPanel> {

	public InventorySolarPanel(int id, Inventory playerinv, IItemHandler invcap, ContainerData tilecoords) {
		super(DeferredRegisters.MENU_SOLARPANEL.get(), id, playerinv, invcap, tilecoords);
	}

	public InventorySolarPanel(int id, Inventory playerinv) {
		this(id, playerinv, new ItemStackHandler(TileTritaniumCrate.SIZE), new SimpleContainerData(3));
	}

	@Override
	public void addInvSlots(IItemHandler invcap, Inventory playerinv) {

	}

	@Override
	public int[] getHotbarNumbers() {
		return new int[]{0,1,2};
	}

	@Override
	public int[] getPlayerInvNumbers() {
		return new int[]{0};
	}

}
