package matteroverdrive.common.tile;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.core.tile.GenericTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileMatterDecomposer extends GenericTile {

	public TileMatterDecomposer(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_MATTER_DECOMPOSER.get(), pos, state);
	}

}
