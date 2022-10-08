package matteroverdrive.core.block;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import matteroverdrive.common.block.OverdriveBlockStates;
import matteroverdrive.common.block.OverdriveBlockStates.VerticalFacing;
import matteroverdrive.core.tile.GenericTile;
import matteroverdrive.core.tile.utils.ITickableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public abstract class GenericEntityBlock extends BaseEntityBlock {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	protected GenericEntityBlock(OverdriveBlockProperties properties) {
		super(properties);

		BlockState defaultState = getStateDefinition().any();
		OverdriveBlockProperties stateProperties = (OverdriveBlockProperties) this.properties;
		if (stateProperties.canBeWaterlogged()) {
			defaultState.setValue(BlockStateProperties.WATERLOGGED, false);
		}
		if (stateProperties.canBeLit()) {
			defaultState.setValue(BlockStateProperties.LIT, false);
		}
		if (stateProperties.hasFacing()) {
			defaultState.setValue(FACING, Direction.NORTH);
		}
		if (stateProperties.isOmniDirectional()) {
			defaultState.setValue(OverdriveBlockStates.VERTICAL_FACING, VerticalFacing.NONE);
		}
		registerDefaultState(defaultState);
	}

	@Override
	protected void createBlockStateDefinition(@NonNull Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		OverdriveBlockProperties stateProperties = (OverdriveBlockProperties) this.properties;
		if (stateProperties.canBeWaterlogged()) {
			builder.add(BlockStateProperties.WATERLOGGED);
		}
		if (stateProperties.canBeLit()) {
			builder.add(BlockStateProperties.LIT);
		}
		if (stateProperties.hasFacing()) {
			builder.add(FACING);
		}
		if (stateProperties.isOmniDirectional()) {
			builder.add(OverdriveBlockStates.VERTICAL_FACING);
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState superState = super.getStateForPlacement(context);
		OverdriveBlockProperties stateProperties = (OverdriveBlockProperties) this.properties;
		if (stateProperties.canBeWaterlogged()) {
			FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
			superState = superState.setValue(BlockStateProperties.WATERLOGGED, fluidState.getType() == Fluids.WATER);
		}
		if (stateProperties.canBeLit()) {
			superState = superState.setValue(BlockStateProperties.LIT,
					stateProperties.isAlwaysLit() || stateProperties.isLitOnPlacement());
		}
		if (stateProperties.hasFacing()) {
			if (stateProperties.isOmniDirectional()) {
				float viewRot = context.getPlayer().getViewXRot(1.0F);

				Direction vertical = null;

				if (viewRot < -50.0F) {
					vertical = Direction.DOWN;
				} else if (viewRot > 50.0F) {
					vertical = Direction.UP;
				}
				superState = superState.setValue(OverdriveBlockStates.VERTICAL_FACING,
						VerticalFacing.fromDirection(vertical));
			}
			superState = superState.setValue(FACING, context.getHorizontalDirection().getOpposite());
		}
		return superState;
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
			BlockEntityType<T> type) {
		return (world, pos, blockstate, tile) -> {
			if (tile instanceof ITickableTile tickable && tickable.canTick()) {
				tickable.tick(world);
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
	public BlockState updateShape(BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState,
			@NotNull LevelAccessor level, @NotNull BlockPos currentPos, @NotNull BlockPos neighborPos) {
		if (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) {
			level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
	}

	@Override
	public void onRemove(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean moving) {
		BlockEntity entity = level.getBlockEntity(pos);
		if (entity != null && entity instanceof GenericTile tile) {
			tile.onBlockStateChange(oldState, newState, moving);
		}
		super.onRemove(oldState, level, pos, newState, moving);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		if (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) {
			return Fluids.WATER.getSource(false);
		}
		return super.getFluidState(state);
	}

	@Override
	public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
		OverdriveBlockProperties stateProperties = (OverdriveBlockProperties) this.properties;
		if ((state.hasProperty(BlockStateProperties.LIT) && state.getValue(BlockStateProperties.LIT))
				|| stateProperties.isAlwaysLit()) {
			return 15;
		}
		return super.getLightEmission(state, level, pos);
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return ((OverdriveBlockProperties) this.properties).canConnectToRedstone();
	}
	
	@Override
	public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
		BlockEntity entity = level.getBlockEntity(pos);
		if(entity != null && entity instanceof GenericTile generic) {
			generic.onNeighborChange(state, neighbor);
		}
		super.onNeighborChange(state, level, pos, neighbor);
	}
	
	@Override
	public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
		super.stepOn(level, pos, state, entity);
		BlockEntity block = level.getBlockEntity(pos);
		if(block != null && block instanceof GenericTile tile) {
			tile.onEntityContact(state, entity, false);
		}
	}
	
	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		super.entityInside(state, level, pos, entity);
		BlockEntity block = level.getBlockEntity(pos);
		if(block != null && block instanceof GenericTile tile) {
			tile.onEntityContact(state, entity, true);
		}
	}
	
	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		super.onPlace(state, level, pos, oldState, isMoving);
		BlockEntity block = level.getBlockEntity(pos);
		if(block != null && block instanceof GenericTile tile) {
			tile.onTilePlaced(state, oldState, isMoving);
		}

	}

}
