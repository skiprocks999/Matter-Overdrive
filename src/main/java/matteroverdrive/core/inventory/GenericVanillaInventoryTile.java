package matteroverdrive.core.inventory;

import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.inventory.slot.SlotContainer;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import matteroverdrive.core.tile.GenericTile;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;

public abstract class GenericVanillaInventoryTile<T extends GenericTile> extends GenericInventoryTile<T> {

	protected GenericVanillaInventoryTile(MenuType<?> menu, int id, Inventory playerinv, CapabilityInventory invcap,
			ContainerData tilecoords) {
		super(menu, id, playerinv, invcap, tilecoords);
	}

	@Override
	protected void addPlayerInventory(Inventory playerinv, SlotType type1, SlotType type2) {
		if (hasInventorySlots) {
			for (int i = 0; i < 3; ++i) {
				for (int j = 0; j < 9; ++j) {
					addSlot(new SlotContainer(playerinv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + playerInvOffset,
							getPlayerInvNumbers(), SlotType.VANILLA));
				}
			}
		}
		if (hasHotbarSlots) {
			for (int k = 0; k < 9; ++k) {
				addSlot(new SlotContainer(playerinv, k, 8 + k * 18, 142 + playerInvOffset, getHotbarNumbers(),
						SlotType.VANILLA));
			}
		}
	}

}
