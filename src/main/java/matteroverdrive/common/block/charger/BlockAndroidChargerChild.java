package matteroverdrive.common.block.charger;

import java.util.List;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.block.BlockOverdrive;
import matteroverdrive.common.block.machine.BlockMachine;
import matteroverdrive.common.block.states.OverdriveBlockStates;
import matteroverdrive.common.block.states.OverdriveBlockStates.ChargerBlockPos;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.tile.TileCharger;
import matteroverdrive.core.block.GenericStateVariableBlock;
import matteroverdrive.core.block.state.StateVariables;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class BlockAndroidChargerChild extends BlockMachine<TileCharger> {
	
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
		super(Properties.of(Material.GLASS).strength(3.5F).sound(SoundType.METAL).isRedstoneConductor((state, level, pos) -> false).noOcclusion(),
						StateVariables.Defaults.waterloggableFourway,
						"charger_child", TileCharger.class, TileCharger::new, TypeMachine.CHARGER);
		registerDefaultState(getStateDefinition().any()
						.setValue(BlockStateProperties.WATERLOGGED, false)
						.setValue(getRotationProperty(), Direction.NORTH)
						.setValue(OverdriveBlockStates.CHARGER_POS, ChargerBlockPos.BOTTOM));
	}

	/**
	 * Just don't create a BlockEntity for this since it's a dummy block.
	 *
	 * @param pos   The position of the block where the BlockEntity is created.
	 * @param state The state of the block which is creating the BlockEntity.
	 * @return Returns in this case a Null BlockEntity.
	 */
	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return null;
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(getRotationProperty());
		builder.add(OverdriveBlockStates.CHARGER_POS);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState state = super.getStateForPlacement(context);
		if (state != null) {
			state.setValue(getRotationProperty(), context.getHorizontalDirection().getOpposite()).setValue(OverdriveBlockStates.CHARGER_POS, ChargerBlockPos.BOTTOM);
			return state;
		}
		return state;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		Direction facing = state.getValue(getRotationProperty());
		ChargerBlockPos loc = state.getValue(OverdriveBlockStates.CHARGER_POS);
		return switch (loc) {
			case MIDDLE -> switch (facing) {
				case EAST -> MIDDLE_E;
				case WEST -> MIDDLE_W;
				case NORTH -> MIDDLE_N;
				case SOUTH -> MIDDLE_S;
				default -> super.getShape(state, worldIn, pos, context);
			};
			case TOP -> switch (facing) {
				case EAST, WEST -> TOP_EW;
				case NORTH, SOUTH -> TOP_NS;
				default -> super.getShape(state, worldIn, pos, context);
			};
			default -> super.getShape(state, worldIn, pos, context);
		};
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		return DeferredRegisters.BLOCK_CHARGER.get().defaultBlockState().getDrops(builder);
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public float getShadeBrightness(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 1.0f;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return true;
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
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

	@SuppressWarnings("deprecation")
	@Override
	public boolean isSignalSource(BlockState state) {
		return TypeMachine.CHARGER.isRedstoneConnected;
	}

	@SuppressWarnings("deprecation")
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

	@SuppressWarnings("deprecation")
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
			switch (loc) {
				case MIDDLE -> {
					world.setBlockAndUpdate(pos.offset(0, -1, 0), Blocks.AIR.defaultBlockState());
					world.setBlockAndUpdate(pos.offset(0, 1, 0), Blocks.AIR.defaultBlockState());
				}
				case TOP -> {
					world.setBlockAndUpdate(pos.offset(0, -1, 0), Blocks.AIR.defaultBlockState());
					world.setBlockAndUpdate(pos.offset(0, -2, 0), Blocks.AIR.defaultBlockState());
				}
			}
		}
		super.onRemove(state, world, pos, newState, isMoving);
	}
	
	@Override
	public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation rot) {
		if (state.hasProperty(getRotationProperty())) {
			ChargerBlockPos loc = state.getValue(OverdriveBlockStates.CHARGER_POS);
			BlockPos first;
			BlockPos second;
			BlockState firstState;
			BlockState secondState;
			switch (loc) {
				case MIDDLE -> {
					first = pos.offset(0, -1, 0);
					second = pos.offset(0, 1, 0);
					firstState = level.getBlockState(first);
					secondState = level.getBlockState(second);
					if (firstState.getBlock() instanceof GenericStateVariableBlock<?> variable)
						level.setBlock(first, firstState.setValue(variable.getRotationProperty(), rot.rotate(firstState.getValue(variable.getRotationProperty()))), 3);
					level.setBlock(second, secondState.setValue(getRotationProperty(), rot.rotate(secondState.getValue(getRotationProperty()))), 3);
				}
				case TOP -> {
					first = pos.offset(0, -2, 0);
					second = pos.offset(0, -1, 0);
					firstState = level.getBlockState(first);
					secondState = level.getBlockState(second);
					if (firstState.getBlock() instanceof GenericStateVariableBlock<?> variable)
						level.setBlock(first, firstState.setValue(variable.getRotationProperty(), rot.rotate(firstState.getValue(variable.getRotationProperty()))), 3);
					level.setBlock(second, secondState.setValue(getRotationProperty(), rot.rotate(secondState.getValue(getRotationProperty()))), 3);
				}
			}
			return state.setValue(getRotationProperty(), rot.rotate(state.getValue(getRotationProperty())));
		}
		return super.rotate(state, level, pos, rot);
	}

	@Override
	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {}
	
	

}
