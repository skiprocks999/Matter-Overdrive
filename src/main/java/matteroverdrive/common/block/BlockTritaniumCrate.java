package matteroverdrive.common.block;

import matteroverdrive.common.tile.TileTritaniumCrate;
import matteroverdrive.core.block.WaterloggableEntityBlock;
import matteroverdrive.core.tile.GenericTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockTritaniumCrate extends WaterloggableEntityBlock {
	
	private static final VoxelShape NS = Block.box(0.0D , 0.0D, 2.0D, 16.0D, 12.0D, 14.0D);
	private static final VoxelShape EW = Block.box(2.0D , 0.0D, 0.0D, 14.0D, 12.0D, 16.0D);
	
	public BlockTritaniumCrate(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		switch(state.getValue(FACING)) {
		case NORTH, SOUTH:
			return NS;
		case EAST, WEST:
			return EW;
		default:
			return super.getShape(state, level, pos, context);
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new TileTritaniumCrate(pPos, pState);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
			BlockHitResult hit) {
		if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		}
		BlockEntity tile = level.getBlockEntity(pos);
		if (tile instanceof GenericTile generic && generic != null) {
			if (generic.hasMenu) {
				player.openMenu(generic.getMenuProvider());
			}
			player.awardStat(Stats.INTERACT_WITH_FURNACE);
			return InteractionResult.CONSUME;
		}
		return InteractionResult.FAIL;
	}

}
