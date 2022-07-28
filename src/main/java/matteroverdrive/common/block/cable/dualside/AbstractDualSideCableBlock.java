package matteroverdrive.common.block.cable.dualside;

import matteroverdrive.common.block.cable.AbstractCableBlock;
import matteroverdrive.common.block.cable.ICableType;
import matteroverdrive.common.tile.cable.AbstractCableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public abstract class AbstractDualSideCableBlock extends AbstractCableBlock {

	public AbstractDualSideCableBlock(Properties properties, ICableType type) {
		super(properties, type);
	}
	
	@Override
	public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		super.onPlace(state, worldIn, pos, oldState, isMoving);
		BlockEntity tile = worldIn.getBlockEntity(pos);
		if (checkCableClass(tile)) {
			((AbstractCableTile<?>)tile).refreshNetwork();
		}
	}

	@Override
	public void onNeighborChange(BlockState state, LevelReader world, BlockPos pos, BlockPos neighbor) {
		super.onNeighborChange(state, world, pos, neighbor);
		BlockEntity tile = world.getBlockEntity(pos);
		if (checkCableClass(tile)) {
			((AbstractCableTile<?>)tile).refreshNetwork();
		}
	}

}
