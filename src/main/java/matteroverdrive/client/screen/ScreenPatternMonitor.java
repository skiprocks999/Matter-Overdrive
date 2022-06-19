package matteroverdrive.client.screen;

import matteroverdrive.common.inventory.InventoryPatternMonitor;
import matteroverdrive.core.screen.GenericScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ScreenPatternMonitor extends GenericScreen<InventoryPatternMonitor> {

	public ScreenPatternMonitor(InventoryPatternMonitor menu, Inventory playerinventory, Component title) {
		super(menu, playerinventory, title);
	}

	@Override
	public int getScreenNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

}
