package matteroverdrive.common.block;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class BlockRedstoneToggle extends BlockOverdrive {

	public BlockRedstoneToggle(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(BlockStateProperties.LIT, false));
	}

	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(BlockStateProperties.LIT,
				context.getLevel().hasNeighborSignal(context.getClickedPos()));
	}

	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block changedBlock, BlockPos changedPos,
			boolean bool) {
		if (!world.isClientSide()) {
			boolean flag = state.getValue(BlockStateProperties.LIT);
			if (flag != world.hasNeighborSignal(pos)) {
				if (flag) {
					world.scheduleTick(pos, this, 4);
				} else {
					world.setBlock(pos, state.cycle(BlockStateProperties.LIT), 2);
				}
			}

		}
	}

	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
		if (state.getValue(BlockStateProperties.LIT) && !level.hasNeighborSignal(pos)) {
			level.setBlock(pos, state.cycle(BlockStateProperties.LIT), 2);
		}

	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
		stateBuilder.add(BlockStateProperties.LIT);
	}

}
