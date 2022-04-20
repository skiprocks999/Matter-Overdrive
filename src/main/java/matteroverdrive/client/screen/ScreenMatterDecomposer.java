package matteroverdrive.client.screen;

import matteroverdrive.common.inventory.InventoryMatterDecomposer;
import matteroverdrive.core.screen.GenericScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ScreenMatterDecomposer extends GenericScreen<InventoryMatterDecomposer> {

	public ScreenMatterDecomposer(InventoryMatterDecomposer pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getScreenNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

}
