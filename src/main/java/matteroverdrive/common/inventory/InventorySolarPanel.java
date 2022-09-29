package matteroverdrive.common.inventory;

import matteroverdrive.common.item.ItemUpgrade.UpgradeType;
import matteroverdrive.common.tile.TileSolarPanel;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.capability.types.item.PlayerSlotDataWrapper;
import matteroverdrive.core.inventory.GenericInventoryTile;
import matteroverdrive.core.inventory.slot.SlotUpgrade;
import matteroverdrive.registry.MenuRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class InventorySolarPanel extends GenericInventoryTile<TileSolarPanel> {

	public static final UpgradeType[] UPGRADES = new UpgradeType[] { UpgradeType.POWER_STORAGE };

	public InventorySolarPanel(int id, Inventory playerinv, CapabilityInventory invcap, ContainerData tilecoords) {
		super(MenuRegistry.MENU_SOLAR_PANEL.get(), id, playerinv, invcap, tilecoords);
	}

	public InventorySolarPanel(int id, Inventory playerinv) {
		this(id, playerinv, new CapabilityInventory(TileSolarPanel.SLOT_COUNT, false, false),
				new SimpleContainerData(3));
	}

	@Override
	public void addInvSlots(CapabilityInventory invcap, Inventory playerinv) {
		addSlot(new SlotUpgrade(invcap, nextIndex(), 105, 55, new int[] { 2 }, UPGRADES));
		addSlot(new SlotUpgrade(invcap, nextIndex(), 129, 55, new int[] { 2 }, UPGRADES));
	}
	
	@Override
	public PlayerSlotDataWrapper getDataWrapper(Player player) {
		return defaultOverdriveScreen(new int[] { 0, 1, 2 }, new int[] { 0 });
	}

}
