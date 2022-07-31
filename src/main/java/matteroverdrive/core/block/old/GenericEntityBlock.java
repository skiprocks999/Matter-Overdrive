package matteroverdrive.core.block.old;

import matteroverdrive.core.tile.types.old.GenericTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public abstract class GenericEntityBlock extends BaseEntityBlock {

	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	protected GenericEntityBlock(Properties properties) {
		super(properties);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
			BlockEntityType<T> type) {
		return (world, pos, blockstate, tile) -> {
			if (tile instanceof GenericTile generic && generic.isTickable) {
				generic.tick(world, generic);
			}
		};
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		if (state.hasProperty(FACING)) {
			return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
		}
		return super.rotate(state, rot);
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		if (state.hasProperty(FACING)) {
			return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
		}
		return super.mirror(state, mirrorIn);
	}

	@Override
	public void onRemove(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean moving) {
		super.onRemove(oldState, level, pos, newState, moving);
		if (!level.isClientSide && newState.hasProperty(FACING)
				&& oldState.getValue(FACING) != newState.getValue(FACING)) {
			BlockEntity entity = level.getBlockEntity(pos);
			if (entity != null && entity instanceof GenericTile tile) {
				tile.refreshCapabilities();
			}
		}
	}

}
