package matteroverdrive.common.tile;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.capability.types.matter.CapabilityMatterStorage;
import matteroverdrive.core.tile.GenericTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileMatterDecomposer extends GenericTile {

	public TileMatterDecomposer(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_MATTER_DECOMPOSER.get(), pos, state);
		addCapability(new CapabilityInventory(8).setOwner(this));
		addCapability(new CapabilityEnergyStorage(512000, true, false).setOwner(this));
		addCapability(new CapabilityMatterStorage(1024, false, true).setOwner(this));
		
	}

}
