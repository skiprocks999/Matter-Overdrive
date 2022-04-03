package matteroverdrive.client.screen;

import matteroverdrive.common.inventory.InventoryTritaniumCrate;
import matteroverdrive.core.screen.GenericScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ScreenTritaniumCrate extends GenericScreen<InventoryTritaniumCrate> {

	public ScreenTritaniumCrate(InventoryTritaniumCrate pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
	}

}
