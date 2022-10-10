package matteroverdrive.common.inventory;

import matteroverdrive.common.tile.matter_network.TileDiscManipulator;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.capability.types.item.PlayerSlotDataWrapper;
import matteroverdrive.core.inventory.GenericInventoryTile;
import matteroverdrive.core.inventory.slot.SlotGeneric;
import matteroverdrive.core.screen.component.ScreenComponentIcon.IconType;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import matteroverdrive.registry.MenuRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class InventoryDiscManipulator extends GenericInventoryTile<TileDiscManipulator> {

	public InventoryDiscManipulator(int id, Inventory playerinv, CapabilityInventory invcap, ContainerData tilecoords) {
		super(MenuRegistry.MENU_DISC_MANIPULATOR.get(), id, playerinv, invcap, tilecoords);
	}

	public InventoryDiscManipulator(int id, Inventory playerinv) {
		this(id, playerinv, new CapabilityInventory(TileDiscManipulator.SIZE, false, false),
				new SimpleContainerData(3));
	}

	@Override
	public void addInvSlots(CapabilityInventory invcap, Inventory playerinv) {
		addSlot(new SlotGeneric(invcap, nextIndex(), 8, 48, new int[] { 0, 1 }, SlotType.BIG, IconType.PATTERN_DRIVE_DARK));

	}

	@Override
	public PlayerSlotDataWrapper getDataWrapper(Player player) {
		return defaultOverdriveScreen(new int[] { 0, 1 }, new int[] { 0, 1 });
	}

}
