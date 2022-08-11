package matteroverdrive.common.block.charger;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.block.BlockOverdrive;
import matteroverdrive.common.block.OverdriveBlockStates;
import matteroverdrive.common.block.OverdriveBlockStates.ChargerBlockPos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockAndroidChargerChild extends BlockOverdrive {
	
	private static VoxelShape MIDDLE_E;
	private static VoxelShape MIDDLE_W;
	private static VoxelShape MIDDLE_N;
	private static VoxelShape MIDDLE_S;
	
	private static final VoxelShape TOP_EW = Shapes.box(0.0D, 0.0D, 0.09375D, 1.0D, 0.3125D, 0.90625D);
	private static final VoxelShape TOP_NS = Shapes.box(0.09375D, 0.0D, 0.0D, 0.90625D, 0.3125D, 1.0D);
	
	static {
		
		MIDDLE_E = Shapes.box(0.0D, 0.9D, 0.09375D, 1.0D, 1.0D, 0.90625D);
		
		MIDDLE_E = Shapes.or(MIDDLE_E, Shapes.box(0.09375D, 0D, 0.29375D, 0.5D, 0.75D, 0.70625D));
		MIDDLE_E = Shapes.or(MIDDLE_E, Shapes.box(0.65625, 0D, 0.40625D, 0.84375, 0.875D, 0.59375D));
		
		MIDDLE_E = Shapes.or(MIDDLE_E, Shapes.box(0.0D, 0.5D, 0.09375D, 0.0625D, 0.9D, 0.90625D));
		MIDDLE_E = Shapes.or(MIDDLE_E, Shapes.box(0.0625D, 0.525D, 0.09375D, 0.125D, 0.9D, 0.90625D));
		MIDDLE_E = Shapes.or(MIDDLE_E, Shapes.box(0.125D, 0.55D, 0.09375D, 0.1875D, 0.9D, 0.90625D));
		MIDDLE_E = Shapes.or(MIDDLE_E, Shapes.box(0.1875D, 0.575D, 0.09375D, 0.25D, 0.9D, 0.90625D));
		MIDDLE_E = Shapes.or(MIDDLE_E, Shapes.box(0.25D, 0.6D, 0.09375D, 0.3125D, 0.9D, 0.90625D));
		MIDDLE_E = Shapes.or(MIDDLE_E, Shapes.box(0.3125D, 0.625D, 0.09375D, 0.375D, 0.9D, 0.90625D));
		MIDDLE_E = Shapes.or(MIDDLE_E, Shapes.box(0.375D, 0.65D, 0.09375D, 0.4375D, 0.9D, 0.90625D));
		MIDDLE_E = Shapes.or(MIDDLE_E, Shapes.box(0.4375D, 0.675D, 0.09375D, 0.5D, 0.9D, 0.90625D));
		MIDDLE_E = Shapes.or(MIDDLE_E, Shapes.box(0.5D, 0.7D, 0.09375D, 0.5625D, 0.9D, 0.90625D));
		MIDDLE_E = Shapes.or(MIDDLE_E, Shapes.box(0.5625D, 0.725D, 0.09375D, 0.625D, 0.9D, 0.90625D));
		MIDDLE_E = Shapes.or(MIDDLE_E, Shapes.box(0.625D, 0.75D, 0.09375D, 0.6875D, 0.9D, 0.90625D));
		MIDDLE_E = Shapes.or(MIDDLE_E, Shapes.box(0.6875D, 0.775D, 0.09375D, 0.75D, 0.9D, 0.90625D));
		MIDDLE_E = Shapes.or(MIDDLE_E, Shapes.box(0.75D, 0.8D, 0.09375D, 0.8125D, 0.9D, 0.90625D));
		MIDDLE_E = Shapes.or(MIDDLE_E, Shapes.box(0.8125D, 0.825D, 0.09375D, 0.875D, 0.9D, 0.90625D));
		MIDDLE_E = Shapes.or(MIDDLE_E, Shapes.box(0.875D, 0.85D, 0.09375D, 0.9375D, 0.9D, 0.90625D));
		MIDDLE_E = Shapes.or(MIDDLE_E, Shapes.box(0.9375D, 0.875D, 0.09375D, 1.0D, 0.9D, 0.90625D));
		
		
		MIDDLE_W = Shapes.box(0.0D, 0.9D, 0.09375D, 1.0D, 1.0D, 0.90625D);
		
		MIDDLE_W = Shapes.or(MIDDLE_W, Shapes.box(0.5D, 0.0D, 0.29375D, 0.90625D, 0.75D, 0.70625D));
		MIDDLE_W = Shapes.or(MIDDLE_W, Shapes.box(0.15625D, 0.0D, 0.40625D, 0.34375D, 1.0D, 0.59375D));
		
		MIDDLE_W = Shapes.or(MIDDLE_W, Shapes.box(0.0D, 0.875D, 0.09375D, 0.0625D, 0.9D, 0.90625D));
		MIDDLE_W = Shapes.or(MIDDLE_W, Shapes.box(0.0625D, 0.85D, 0.09375D, 0.125D, 0.9D, 0.90625D));
		MIDDLE_W = Shapes.or(MIDDLE_W, Shapes.box(0.125D, 0.825D, 0.09375D, 0.1875D, 0.9D, 0.90625D));
		MIDDLE_W = Shapes.or(MIDDLE_W, Shapes.box(0.1875D, 0.8D, 0.09375D, 0.25D, 0.9D, 0.90625D));
		MIDDLE_W = Shapes.or(MIDDLE_W, Shapes.box(0.25D, 0.775D, 0.09375D, 0.3125D, 0.9D, 0.90625D));
		MIDDLE_W = Shapes.or(MIDDLE_W, Shapes.box(0.3125D, 0.75D, 0.09375D, 0.375D, 0.9D, 0.90625D));
		MIDDLE_W = Shapes.or(MIDDLE_W, Shapes.box(0.375D, 0.725D, 0.09375D, 0.4375D, 0.9D, 0.90625D));
		MIDDLE_W = Shapes.or(MIDDLE_W, Shapes.box(0.4375D, 0.7D, 0.09375D, 0.5D, 0.9D, 0.90625D));
		MIDDLE_W = Shapes.or(MIDDLE_W, Shapes.box(0.5D, 0.675D, 0.09375D, 0.5625D, 0.9D, 0.90625D));
		MIDDLE_W = Shapes.or(MIDDLE_W, Shapes.box(0.5625D, 0.65D, 0.09375D, 0.625D, 0.9D, 0.90625D));
		MIDDLE_W = Shapes.or(MIDDLE_W, Shapes.box(0.625D, 0.625D, 0.09375D, 0.6875D, 0.9D, 0.90625D));
		MIDDLE_W = Shapes.or(MIDDLE_W, Shapes.box(0.6875D, 0.6D, 0.09375D, 0.75D, 0.9D, 0.90625D));
		MIDDLE_W = Shapes.or(MIDDLE_W, Shapes.box(0.75D, 0.575D, 0.09375D, 0.8125D, 0.9D, 0.90625D));
		MIDDLE_W = Shapes.or(MIDDLE_W, Shapes.box(0.8125D, 0.55D, 0.09375D, 0.875D, 0.9D, 0.90625D));
		MIDDLE_W = Shapes.or(MIDDLE_W, Shapes.box(0.875D, 0.525D, 0.09375D, 0.9375D, 0.9D, 0.90625D));
		MIDDLE_W = Shapes.or(MIDDLE_W, Shapes.box(0.9375D, 0.5D, 0.09375D, 1.0D, 0.9D, 0.90625D));
		
		
		MIDDLE_N = Shapes.box(0.09375D, 0.9D, 0.0D, 0.90625D, 1.0D, 1.0D);
		
		MIDDLE_N = Shapes.or(MIDDLE_N, Shapes.box(0.29375D, 0.0D, 0.5D, 0.70625D, 0.75D, 0.90625D));
		MIDDLE_N = Shapes.or(MIDDLE_N, Shapes.box(0.40625D, 0.0D, 0.15625D, 0.59375D, 1.0D, 0.34375D));
		
		MIDDLE_N = Shapes.or(MIDDLE_N, Shapes.box(0.09375D, 0.875D, 0.0D, 0.90625D, 0.9D, 0.0625D));
		MIDDLE_N = Shapes.or(MIDDLE_N, Shapes.box(0.09375D, 0.85D,0.0625D, 0.90625D, 0.9D, 0.125D));
		MIDDLE_N = Shapes.or(MIDDLE_N, Shapes.box(0.09375D, 0.825D, 0.125D, 0.90625D, 0.9D, 0.1875D));
		MIDDLE_N = Shapes.or(MIDDLE_N, Shapes.box(0.09375D, 0.8D, 0.1875D, 0.90625D, 0.9D, 0.25D));
		MIDDLE_N = Shapes.or(MIDDLE_N, Shapes.box(0.09375D, 0.775D, 0.25D, 0.90625D, 0.9D, 0.3125D));
		MIDDLE_N = Shapes.or(MIDDLE_N, Shapes.box(0.09375D, 0.75D, 0.3125D, 0.90625D, 0.9D, 0.375D));
		MIDDLE_N = Shapes.or(MIDDLE_N, Shapes.box(0.09375D, 0.725D, 0.375D, 0.90625D, 0.9D, 0.4375D));
		MIDDLE_N = Shapes.or(MIDDLE_N, Shapes.box(0.09375D, 0.7D, 0.4375D, 0.90625D, 0.9D, 0.5D));
		MIDDLE_N = Shapes.or(MIDDLE_N, Shapes.box(0.09375D, 0.675D, 0.5D, 0.90625D, 0.9D, 0.5625D));
		MIDDLE_N = Shapes.or(MIDDLE_N, Shapes.box(0.09375D, 0.65D, 0.5625D, 0.90625D, 0.9D, 0.625D));
		MIDDLE_N = Shapes.or(MIDDLE_N, Shapes.box(0.09375D, 0.625D, 0.625D, 0.90625D, 0.9D, 0.6875D));
		MIDDLE_N = Shapes.or(MIDDLE_N, Shapes.box(0.09375D, 0.6D, 0.6875D, 0.90625D, 0.9D, 0.75D));
		MIDDLE_N = Shapes.or(MIDDLE_N, Shapes.box(0.09375D, 0.575D, 0.75D, 0.90625D, 0.9D, 0.8125D));
		MIDDLE_N = Shapes.or(MIDDLE_N, Shapes.box(0.09375D, 0.55D, 0.8125D, 0.90625D, 0.9D, 0.875D));
		MIDDLE_N = Shapes.or(MIDDLE_N, Shapes.box(0.09375D, 0.525D, 0.875D, 0.90625D, 0.9D, 0.9375D));
		MIDDLE_N = Shapes.or(MIDDLE_N, Shapes.box(0.09375D, 0.5D, 0.9375D, 0.90625D, 0.9D, 1.0D));
		
		
		MIDDLE_S = Shapes.box(0.09375D, 0.9D, 0.0D, 0.90625D, 1.0D, 1.0D);
		
		MIDDLE_S = Shapes.or(MIDDLE_S, Shapes.box(0.29375D, 0D, 0.09375D, 0.70625D, 0.75D, 0.5D));
		MIDDLE_S = Shapes.or(MIDDLE_S, Shapes.box(0.40625D, 0D, 0.65625, 0.59375D, 0.875D, 0.84375));
		
		MIDDLE_S = Shapes.or(MIDDLE_S, Shapes.box(0.09375D, 0.5D, 0.0D, 0.90625D, 0.9D, 0.0625D));
		MIDDLE_S = Shapes.or(MIDDLE_S, Shapes.box(0.09375D, 0.525D,0.0625D, 0.90625D, 0.9D, 0.125D));
		MIDDLE_S = Shapes.or(MIDDLE_S, Shapes.box(0.09375D, 0.55D, 0.125D, 0.90625D, 0.9D, 0.1875D));
		MIDDLE_S = Shapes.or(MIDDLE_S, Shapes.box(0.09375D, 0.575D, 0.1875D, 0.90625D, 0.9D, 0.25D));
		MIDDLE_S = Shapes.or(MIDDLE_S, Shapes.box(0.09375D, 0.6D, 0.25D, 0.90625D, 0.9D, 0.3125D));
		MIDDLE_S = Shapes.or(MIDDLE_S, Shapes.box(0.09375D, 0.625D, 0.3125D, 0.90625D, 0.9D, 0.375D));
		MIDDLE_S = Shapes.or(MIDDLE_S, Shapes.box(0.09375D, 0.65D, 0.375D, 0.90625D, 0.9D, 0.4375D));
		MIDDLE_S = Shapes.or(MIDDLE_S, Shapes.box(0.09375D, 0.675D, 0.4375D, 0.90625D, 0.9D, 0.5D));
		MIDDLE_S = Shapes.or(MIDDLE_S, Shapes.box(0.09375D, 0.7D, 0.5D, 0.90625D, 0.9D, 0.5625D));
		MIDDLE_S = Shapes.or(MIDDLE_S, Shapes.box(0.09375D, 0.725D, 0.5625D, 0.90625D, 0.9D, 0.625D));
		MIDDLE_S = Shapes.or(MIDDLE_S, Shapes.box(0.09375D, 0.75D, 0.625D, 0.90625D, 0.9D, 0.6875D));
		MIDDLE_S = Shapes.or(MIDDLE_S, Shapes.box(0.09375D, 0.775D, 0.6875D, 0.90625D, 0.9D, 0.75D));
		MIDDLE_S = Shapes.or(MIDDLE_S, Shapes.box(0.09375D, 0.8D, 0.75D, 0.90625D, 0.9D, 0.8125D));
		MIDDLE_S = Shapes.or(MIDDLE_S, Shapes.box(0.09375D, 0.825D, 0.8125D, 0.90625D, 0.9D, 0.875D));
		MIDDLE_S = Shapes.or(MIDDLE_S, Shapes.box(0.09375D, 0.85D, 0.875D, 0.90625D, 0.9D, 0.9375D));
		MIDDLE_S = Shapes.or(MIDDLE_S, Shapes.box(0.09375D, 0.875D, 0.9375D, 0.90625D, 0.9D, 1.0D));
	}
	
	public BlockAndroidChargerChild() {
		super(BlockBehaviour.Properties.of(Material.GLASS).strength(3.5F).sound(SoundType.METAL)
				.isRedstoneConductor((a, b, c) -> false).noOcclusion(), false);
		registerDefaultState(stateDefinition.any().setValue(BlockStateProperties.FACING, Direction.NORTH).setValue(OverdriveBlockStates.CHARGER_POS, ChargerBlockPos.BOTTOM)
				.setValue(BlockStateProperties.WATERLOGGED, false));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(BlockStateProperties.FACING);
		builder.add(OverdriveBlockStates.CHARGER_POS);
		builder.add(BlockStateProperties.WATERLOGGED);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
		return super.getStateForPlacement(context)
				.setValue(BlockStateProperties.FACING, context.getHorizontalDirection().getOpposite())
				.setValue(OverdriveBlockStates.CHARGER_POS, ChargerBlockPos.BOTTOM)
				.setValue(BlockStateProperties.WATERLOGGED, fluidState.getType() == Fluids.WATER);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		Direction facing = state.getValue(BlockStateProperties.FACING);
		ChargerBlockPos loc = state.getValue(OverdriveBlockStates.CHARGER_POS);
		switch(loc) {
		case MIDDLE:
			switch (facing) {
			case EAST:
				return MIDDLE_E;
			case WEST:
				return MIDDLE_W;
			case NORTH:
				return MIDDLE_N;
			case SOUTH:
				return MIDDLE_S;
			default:
				return super.getShape(state, worldIn, pos, context);
			}
		case TOP:
			switch (facing) {
			case EAST, WEST:
				return TOP_EW;
			case NORTH, SOUTH:
				return TOP_NS;
			default:
				return super.getShape(state, worldIn, pos, context);
			}
		default:
			return super.getShape(state, worldIn, pos, context);
		}
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		return DeferredRegisters.BLOCK_CHARGER.get().defaultBlockState().getDrops(builder);
	}

	@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
		return true;
	}

	@Override
	public float getShadeBrightness(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 1.0f;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return true;
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
			BlockHitResult hit) {
		ChargerBlockPos loc = state.getValue(OverdriveBlockStates.CHARGER_POS);
		BlockPos newPos;
		BlockState parent;
		switch(loc) {
		case TOP:
			newPos = pos.offset(0, -2, 0);
			parent = world.getBlockState(newPos);
			return parent.getBlock().use(state, world, newPos, player, hand, hit);
		case MIDDLE:
			newPos = pos.offset(0, -1, 0);
			parent = world.getBlockState(newPos);
			return parent.getBlock().use(state, world, newPos, player, hand, hit);
		default:
			return super.use(state, world, pos, player, hand, hit);
		}
	}

	@Override
	public int getDirectSignal(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
		ChargerBlockPos loc = state.getValue(OverdriveBlockStates.CHARGER_POS);
		BlockPos newPos;
		BlockState parent;
		switch(loc) {
		case TOP:
			newPos = pos.offset(0, -2, 0);
			parent = world.getBlockState(newPos);
			return parent.getBlock().getDirectSignal(state, world, newPos, side);
		case MIDDLE:
			newPos = pos.offset(0, -1, 0);
			parent = world.getBlockState(newPos);
			return parent.getBlock().getDirectSignal(state, world, newPos, side);
		default:
			return super.getDirectSignal(state, world, pos, side);
		}
	}

	@Override
	public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
		ChargerBlockPos loc = state.getValue(OverdriveBlockStates.CHARGER_POS);
		BlockPos newPos;
		BlockState parent;
		switch(loc) {
		case TOP:
			newPos = pos.offset(0, -2, 0);
			parent = world.getBlockState(newPos);
			return parent.getBlock().getSignal(state, world, newPos, side);
		case MIDDLE:
			newPos = pos.offset(0, -1, 0);
			parent = world.getBlockState(newPos);
			return parent.getBlock().getSignal(state, world, newPos, side);
		default:
			return super.getSignal(state, world, pos, side);
		}
	}

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
		if(!newState.hasProperty(OverdriveBlockStates.CHARGER_POS)) {
			ChargerBlockPos loc = state.getValue(OverdriveBlockStates.CHARGER_POS);
			switch(loc) {
			case MIDDLE:
				world.setBlockAndUpdate(pos.offset(0, -1, 0), Blocks.AIR.defaultBlockState());
				world.setBlockAndUpdate(pos.offset(0, 1, 0), Blocks.AIR.defaultBlockState());
				break;
			case TOP:
				world.setBlockAndUpdate(pos.offset(0, -1, 0), Blocks.AIR.defaultBlockState());
				world.setBlockAndUpdate(pos.offset(0, -2, 0), Blocks.AIR.defaultBlockState());
				break;
			}
		}
		super.onRemove(state, world, pos, newState, isMoving);
	}
	
	@Override
	public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation rot) {
		if (state.hasProperty(BlockStateProperties.FACING)) {
			ChargerBlockPos loc = state.getValue(OverdriveBlockStates.CHARGER_POS);
			BlockPos first;
			BlockPos second;
			BlockState firstState;
			BlockState secondState;
			switch(loc) {
			case MIDDLE:
				first = pos.offset(0, -1, 0);
				second = pos.offset(0, 1, 0);
				firstState = level.getBlockState(first);
				secondState = level.getBlockState(second);
				level.setBlock(first, firstState.setValue(BlockStateProperties.FACING, rot.rotate(firstState.getValue(BlockStateProperties.FACING))), 3);
				level.setBlock(second, secondState.setValue(BlockStateProperties.FACING, rot.rotate(secondState.getValue(BlockStateProperties.FACING))), 3);
				break;
			case TOP:
				first = pos.offset(0, -2, 0);
				second = pos.offset(0, -1, 0);
				firstState = level.getBlockState(first);
				secondState = level.getBlockState(second);
				level.setBlock(first, firstState.setValue(BlockStateProperties.FACING, rot.rotate(firstState.getValue(BlockStateProperties.FACING))), 3);
				level.setBlock(second, secondState.setValue(BlockStateProperties.FACING, rot.rotate(secondState.getValue(BlockStateProperties.FACING))), 3);
				break;
			}
			return state.setValue(BlockStateProperties.FACING, rot.rotate(state.getValue(BlockStateProperties.FACING)));
		}
		return super.rotate(state, level, pos, rot);
	}

	@Override
	public void fillItemCategory(CreativeModeTab pTab, NonNullList<ItemStack> pItems) {
	}
	
	@Override
	public BlockState updateShape(BlockState state, @NotNull Direction direction,
			@NotNull BlockState neighborState, @NotNull LevelAccessor level, @NotNull BlockPos currentPos,
			@NotNull BlockPos neighborPos) {
		if (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) {
			level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
	}
	
	@Override
	public FluidState getFluidState(BlockState state) {
		if (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) {
			return Fluids.WATER.getSource(false);
		}
		return super.getFluidState(state);
	}

}
