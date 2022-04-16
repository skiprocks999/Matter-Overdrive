package matteroverdrive.common.inventory;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.SoundRegister;
import matteroverdrive.common.tile.TileTritaniumCrate;
import matteroverdrive.core.inventory.GenericVanillaInventoryTile;
import matteroverdrive.core.inventory.slot.SlotGeneric;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import matteroverdrive.core.tile.GenericTile;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class InventoryTritaniumCrate extends GenericVanillaInventoryTile<TileTritaniumCrate> {

	public static final int OFFSET = 56;

	public InventoryTritaniumCrate(int id, Inventory playerinv) {
		this(id, playerinv, new ItemStackHandler(TileTritaniumCrate.SIZE), new SimpleContainerData(3));
	}

	public InventoryTritaniumCrate(int id, Inventory playerinv, IItemHandler invcap, ContainerData coords) {
		super(DeferredRegisters.MENU_TRITANIUMCRATE.get(), id, playerinv, invcap, coords);
	}

	@Override
	public void addInvSlots(IItemHandler invcap, Inventory playerinv) {
		playerInvOffset = OFFSET;
		if (getHandler() != null) {
			for (int j = 0; j < 6; ++j) {
				for (int k = 0; k < 9; ++k) {
					SlotGeneric slot = new SlotGeneric(invcap, nextIndex(), 8 + k * 18, 18 + j * 18, SlotType.VANILLA);
					slot.setScreenNumber(new int[] {0, 1, 2});
					this.addSlot(slot);
				}
			}
		}

	}

	@Override
	public void removed(Player pPlayer) {
		super.removed(pPlayer);
		GenericTile tile = getTile();
		if (tile != null) {
			tile.getLevel().playSound(null, tile.getBlockPos(), SoundRegister.SOUND_CRATECLOSE.get(),
					SoundSource.BLOCKS, 0.5F, 1.0F);
		}
	}

	@Override
	public int[] getHotbarNumbers() {
		return new int[]{0,1,2};
	}

	@Override
	public int[] getPlayerInvNumbers() {
		return new int[]{0,1,2};
	}

}
