package matteroverdrive.core.tile.types;

import matteroverdrive.core.sound.tile.ITickingSoundTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class GenericSoundTile extends GenericUpgradableTile implements ITickingSoundTile {

	// Client-side only value
	protected boolean clientSoundPlaying = false;

	protected GenericSoundTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void setNotPlaying() {
		clientSoundPlaying = false;
	}

	@Override
	public boolean shouldPlaySound() {
		return isRunning() && !isMuffled();
	}

	public abstract boolean isRunning();

}
