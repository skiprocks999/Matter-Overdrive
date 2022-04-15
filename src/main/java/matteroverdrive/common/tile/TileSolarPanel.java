package matteroverdrive.common.tile;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.inventory.InventorySolarPanel;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.tile.GenericTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.block.state.BlockState;

public class TileSolarPanel extends GenericTile {

	public TileSolarPanel(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_SOLARPANEL.get(), pos, state);
		addCapability(new CapabilityInventory(2).setUpgrades(2).setOwner(this));
		addCapability(new CapabilityEnergyStorage(64000, false, true).setOwner(this).setDefaultDirections(state,
				new Direction[] {}, new Direction[] { Direction.DOWN }));
		setMenuProvider(
				new SimpleMenuProvider(
						(id, inv, play) -> new InventorySolarPanel(id, play.getInventory(),
								exposeCapability(CapabilityType.Item), getCoordsData()),
						getContainerName("solar_panel")));
	}

}
