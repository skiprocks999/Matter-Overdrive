package matteroverdrive.core.tile.types;

import matteroverdrive.core.tile.utils.IUpgradableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class GenericUpgradableTile extends GenericRedstoneTile implements IUpgradableTile {

	public GenericUpgradableTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

}
