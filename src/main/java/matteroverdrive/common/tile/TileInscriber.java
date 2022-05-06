package matteroverdrive.common.tile;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.core.tile.types.GenericSoundTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileInscriber extends GenericSoundTile {

	public TileInscriber(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_INSCRIBER.get(), pos, state);
	}

	@Override
	public boolean shouldPlaySound() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setNotPlaying() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getMaxMode() {
		return 2;
	}

}
