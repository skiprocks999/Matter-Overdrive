package matteroverdrive.common.tile.cable;

import matteroverdrive.core.network.BaseNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractEmittingCable<NETWORK extends BaseNetwork> extends AbstractCableTile<NETWORK> {

	protected AbstractEmittingCable(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
	
	public abstract double getMaxTransfer();

}
