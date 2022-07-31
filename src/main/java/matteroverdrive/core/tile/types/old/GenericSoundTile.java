package matteroverdrive.core.tile.types.old;

import matteroverdrive.core.sound.tile.ITickingSoundTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class GenericSoundTile extends GenericUpgradableTile implements ITickingSoundTile {

	protected GenericSoundTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

}
