package matteroverdrive.core.inventory;

import matteroverdrive.core.inventory.slot.SlotContainer;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;

public abstract class GenericVanillaInventoryTile<T extends BlockEntity> extends GenericInventoryTile<T> {

	protected GenericVanillaInventoryTile(MenuType<?> menu, int id, Inventory playerinv, IItemHandler invcap,
			ContainerData tilecoords) {
		super(menu, id, playerinv, invcap, tilecoords);
	}
	
	protected void addPlayerInventory(Inventory playerinv) {
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlot(new SlotContainer(playerinv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + playerInvOffset, SlotType.VANILLA));
			}
		}

		for (int k = 0; k < 9; ++k) {
			addSlot(new SlotContainer(playerinv, k, 8 + k * 18, 142 + playerInvOffset, SlotType.VANILLA));
		}
	}

}
