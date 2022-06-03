package matteroverdrive.common.block.charger;

import java.util.List;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.block.BlockOverdrive;
import matteroverdrive.common.block.charger.BlockAndroidChargerParent.Position;
import matteroverdrive.common.block.states.OverdriveBlockStates;
import matteroverdrive.common.block.type.TypeMachine;
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
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockAndroidChargerChild extends BlockOverdrive {

	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	
	public BlockAndroidChargerChild() {
		super(BlockBehaviour.Properties.of(Material.GLASS).strength(3.5F).sound(SoundType.METAL)
				.isRedstoneConductor((a, b, c) -> false).noOcclusion(), false);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OverdriveBlockStates.CHARGER_POS, Position.BOTTOM));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING);
		builder.add(OverdriveBlockStates.CHARGER_POS);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context)
				.setValue(FACING, context.getHorizontalDirection().getOpposite())
				.setValue(OverdriveBlockStates.CHARGER_POS, Position.BOTTOM);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		Direction facing = state.getValue(FACING);
		BlockAndroidChargerParent.Position loc = state.getValue(OverdriveBlockStates.CHARGER_POS);
		switch(loc) {
		case MIDDLE:
			switch (facing) {
			case EAST, WEST:
				return Shapes.box(0.0D, 0.0D, 0.09375D, 1.0D, 1.0D, 0.90625D);
			case NORTH, SOUTH:
				return Shapes.box(0.09375D, 0.0D, 0.0D, 0.90625D, 1.0D, 1.0D);
			default:
				return super.getShape(state, worldIn, pos, context);
			}
		case TOP:
			switch (facing) {
			case EAST, WEST:
				return Shapes.box(0.0D, 0.0D, 0.09375D, 1.0D, 0.328125D, 0.90625D);
			case NORTH, SOUTH:
				return Shapes.box(0.09375D, 0.0D, 0.0D, 0.90625D, 0.328125D, 1.0D);
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
		BlockAndroidChargerParent.Position loc = state.getValue(OverdriveBlockStates.CHARGER_POS);
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
	public boolean isSignalSource(BlockState state) {
		return TypeMachine.CHARGER.isRedstoneConnected;
	}

	@Override
	public int getDirectSignal(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
		BlockAndroidChargerParent.Position loc = state.getValue(OverdriveBlockStates.CHARGER_POS);
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
		BlockAndroidChargerParent.Position loc = state.getValue(OverdriveBlockStates.CHARGER_POS);
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
			BlockAndroidChargerParent.Position loc = state.getValue(OverdriveBlockStates.CHARGER_POS);
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
		if (state.hasProperty(FACING)) {
			BlockAndroidChargerParent.Position loc = state.getValue(OverdriveBlockStates.CHARGER_POS);
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
				level.setBlock(first, firstState.setValue(BlockAndroidChargerParent.FACING, rot.rotate(firstState.getValue(BlockAndroidChargerParent.FACING))), 3);
				level.setBlock(second, secondState.setValue(FACING, rot.rotate(secondState.getValue(FACING))), 3);
				break;
			case TOP:
				first = pos.offset(0, -2, 0);
				second = pos.offset(0, -1, 0);
				firstState = level.getBlockState(first);
				secondState = level.getBlockState(second);
				level.setBlock(first, firstState.setValue(BlockAndroidChargerParent.FACING, rot.rotate(firstState.getValue(BlockAndroidChargerParent.FACING))), 3);
				level.setBlock(second, secondState.setValue(FACING, rot.rotate(secondState.getValue(FACING))), 3);
				break;
			}
			return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
		}
		return super.rotate(state, level, pos, rot);
	}

	@Override
	public void fillItemCategory(CreativeModeTab pTab, NonNullList<ItemStack> pItems) {
	}

}
