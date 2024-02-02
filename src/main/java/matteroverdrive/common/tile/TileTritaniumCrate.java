package matteroverdrive.common.tile;

import matteroverdrive.common.inventory.InventoryTritaniumCrate;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.registers.IBulkRegistryObject;
import matteroverdrive.core.tile.GenericTile;
import matteroverdrive.registry.TileRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class TileTritaniumCrate extends GenericTile {

	public static final int SIZE = 54;

	public TileTritaniumCrate(BlockPos pos, BlockState state) {
		super(TileRegistry.TILE_TRITANIUM_CRATE.get(), pos, state);

		addCapability(ForgeCapabilities.ITEM_HANDLER,
				new CapabilityInventory(SIZE, true, true).setOwner(this).setInputs(SIZE));
		setMenuProvider(new SimpleMenuProvider(
				(id, inv, play) -> new InventoryTritaniumCrate(id, play.getInventory(),
						exposeCapability(ForgeCapabilities.ITEM_HANDLER), getCoordsData()),
				getContainerName("tritanium_crate")));
	}
	
	@Override
	public void onInventoryChange(int slot, CapabilityInventory inv) {
		setChanged();
	}

	public static enum CrateColors implements IBulkRegistryObject {

		BLACK, BLUE, BROWN, CYAN, DARK_GRAY, GREEN, LIGHT_BLUE, LIGHT_GRAY, LIME_GREEN, MAGENTA, ORANGE, PINK, PURPLE,
		RED, REG, WHITE, YELLOW;

		@Override
		public String id() {
			return "tritanium_crate_" + this.toString().toLowerCase();
		}
	}

}
