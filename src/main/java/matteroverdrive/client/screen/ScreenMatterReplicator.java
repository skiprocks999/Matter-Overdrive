package matteroverdrive.client.screen;

import matteroverdrive.common.inventory.InventoryMatterReplicator;
import matteroverdrive.core.screen.GenericScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ScreenMatterReplicator extends GenericScreen<InventoryMatterReplicator> {

	public ScreenMatterReplicator(InventoryMatterReplicator menu, Inventory playerinventory, Component title) {
		super(menu, playerinventory, title);
	}

	@Override
	public int getScreenNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

}
