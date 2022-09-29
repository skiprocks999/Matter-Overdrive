package matteroverdrive.common.inventory;

import matteroverdrive.common.tile.TileTritaniumCrate;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.capability.types.item.PlayerSlotDataWrapper;
import matteroverdrive.core.inventory.GenericInventoryTile;
import matteroverdrive.core.inventory.slot.SlotGeneric;
import matteroverdrive.core.screen.component.ScreenComponentIcon.IconType;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import matteroverdrive.core.tile.GenericTile;
import matteroverdrive.registry.MenuRegistry;
import matteroverdrive.registry.SoundRegistry;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;;

public class InventoryTritaniumCrate extends GenericInventoryTile<TileTritaniumCrate> {

	public static final int OFFSET = 56;

	public InventoryTritaniumCrate(int id, Inventory playerinv) {
		this(id, playerinv, new CapabilityInventory(TileTritaniumCrate.SIZE, true, true), new SimpleContainerData(3));
	}

	public InventoryTritaniumCrate(int id, Inventory playerinv, CapabilityInventory invcap, ContainerData coords) {
		super(MenuRegistry.MENU_TRITANIUM_CRATE.get(), id, playerinv, invcap, coords);
	}

	@Override
	public void addInvSlots(CapabilityInventory invcap, Inventory playerinv) {
		for (int j = 0; j < 6; ++j) {
			for (int k = 0; k < 9; ++k) {
				this.addSlot(new SlotGeneric(invcap, nextIndex(), 8 + k * 18, 18 + j * 18, new int[] { 0, 1, 2 },
						SlotType.VANILLA, IconType.NONE));
			}
		}
	}

	@Override
	public void removed(Player pPlayer) {
		super.removed(pPlayer);
		GenericTile tile = getTile();
		if (tile != null) {
			tile.getLevel().playSound(null, tile.getBlockPos(), SoundRegistry.SOUND_CRATE_CLOSE.get(),
					SoundSource.BLOCKS, 0.5F, 1.0F);
		}
	}

	@Override
	public PlayerSlotDataWrapper getDataWrapper(Player player) {
		return new PlayerSlotDataWrapper(8, 84 + OFFSET, 18, 18, 8, 142 + OFFSET, 18, 18, SlotType.VANILLA,
				SlotType.VANILLA, new int[] { 0, 1, 2 }, new int[] { 0, 1, 2 });
	}

}
