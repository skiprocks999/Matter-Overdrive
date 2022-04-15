package matteroverdrive.common.tile;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.inventory.InventoryTritaniumCrate;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.registers.IBulkRegistryObject;
import matteroverdrive.core.tile.GenericTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.block.state.BlockState;

public class TileTritaniumCrate extends GenericTile {

	public static final int SIZE = 54;

	public TileTritaniumCrate(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_TRITANIUMCRATE.get(), pos, state);

		addCapability(new CapabilityInventory(SIZE).setOwner(this).setInputs(SIZE));

		setMenuProvider(
				new SimpleMenuProvider(
						(id, inv, play) -> new InventoryTritaniumCrate(id, play.getInventory(),
								exposeCapability(CapabilityType.Item), getCoordsData()),
						getContainerName("tritanium_crate")));
	}

	public static enum CrateColors implements IBulkRegistryObject {

		BLACK, BLUE, BROWN, CYAN, DARK_GREY, GREEN, LIGHT_BLUE, LIGHT_GREY, LIME_GREEN, MAGENTA, ORANGE, PINK, PURPLE,
		RED, REG, WHITE, YELLOW;
	}

}
