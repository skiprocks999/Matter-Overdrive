package matteroverdrive.client.screen;

import matteroverdrive.common.inventory.InventoryMatterAnalyzer;
import matteroverdrive.core.screen.GenericScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ScreenMatterAnalyzer extends GenericScreen<InventoryMatterAnalyzer> {

	public ScreenMatterAnalyzer(InventoryMatterAnalyzer menu, Inventory playerinventory, Component title) {
		super(menu, playerinventory, title);
	}

	@Override
	public int getScreenNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

}
