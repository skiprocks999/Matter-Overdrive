package matteroverdrive.core.tile.types.cable;

import matteroverdrive.core.network.AbstractCableNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractEmittingCable<NETWORK extends AbstractCableNetwork> extends AbstractCableTile<NETWORK> {

	protected AbstractEmittingCable(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public abstract double getMaxTransfer();

}
