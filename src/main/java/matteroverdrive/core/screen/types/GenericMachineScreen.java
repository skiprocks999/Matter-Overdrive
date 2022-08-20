package matteroverdrive.core.screen.types;

import matteroverdrive.core.inventory.GenericInventoryTile;
import matteroverdrive.core.screen.component.button.ButtonRedstoneMode;
import matteroverdrive.core.tile.types.GenericRedstoneTile;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public abstract class GenericMachineScreen<T extends GenericInventoryTile<?>> extends GenericOverdriveScreen<T> {

	public GenericMachineScreen(T menu, Inventory playerinventory, Component title) {
		super(menu, playerinventory, title);
	}
	
	public ButtonRedstoneMode redstoneButton(int x, int y) {
		return new ButtonRedstoneMode(this, x, y, button -> {
			GenericRedstoneTile charger = (GenericRedstoneTile) getMenu().getTile();
			if (charger != null) {
				charger.getPropertyManager().updateServerBlockEntity(charger.currRedstoneModeProp, charger.getCurrMode() + 1);
			}
		}, () -> {
			GenericRedstoneTile charger = (GenericRedstoneTile) getMenu().getTile();
			if (charger != null) {
				return charger.getCurrMode();
			}
			return 0;
		});
	}

}
