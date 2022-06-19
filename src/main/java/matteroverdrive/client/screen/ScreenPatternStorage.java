package matteroverdrive.client.screen;

import matteroverdrive.common.inventory.InventoryPatternStorage;
import matteroverdrive.core.screen.GenericScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ScreenPatternStorage extends GenericScreen<InventoryPatternStorage> {

	public ScreenPatternStorage(InventoryPatternStorage menu, Inventory playerinventory, Component title) {
		super(menu, playerinventory, title);
	}

	@Override
	public int getScreenNumber() {
		return 0;
	}

}
