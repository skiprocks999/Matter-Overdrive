package matteroverdrive.core.screen.types;

import matteroverdrive.core.inventory.GenericInventory;
import matteroverdrive.core.screen.GenericScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public abstract class GenericSwappableScreen<T extends GenericInventory> extends GenericScreen<T> {

	protected int screenNumber = 0;

	public GenericSwappableScreen(T menu, Inventory playerinventory, Component title, GuiTextures background, int guiWidth, int guiHeight) {
		super(menu, playerinventory, title, background, guiWidth, guiHeight);
	}

	protected void updateScreen(int screenNumber) {
		this.screenNumber = screenNumber;
		updateComponentActivity(screenNumber);
	}

	@Override
	public int getScreenNumber() {
		return screenNumber;
	}

}
