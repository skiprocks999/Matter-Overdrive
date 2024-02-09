package matteroverdrive.common.block.cable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import matteroverdrive.common.block.OverdriveBlockStates;
import matteroverdrive.common.block.OverdriveBlockStates.CableConnectionType;
import matteroverdrive.core.block.GenericEntityBlock;
import matteroverdrive.core.block.OverdriveBlockProperties;
import matteroverdrive.core.tile.types.cable.AbstractCableTile;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class AbstractCableBlock extends GenericEntityBlock {

	public static final Map<Direction, EnumProperty<CableConnectionType>> DIRECTION_TO_PROPERTY_MAP = Util
			.make(Maps.newEnumMap(Direction.class), map -> {
				map.put(Direction.NORTH, OverdriveBlockStates.CABLE_NORTH);
				map.put(Direction.EAST, OverdriveBlockStates.CABLE_EAST);
				map.put(Direction.SOUTH, OverdriveBlockStates.CABLE_SOUTH);
				map.put(Direction.WEST, OverdriveBlockStates.CABLE_WEST);
				map.put(Direction.UP, OverdriveBlockStates.CABLE_UP);
				map.put(Direction.DOWN, OverdriveBlockStates.CABLE_DOWN);
			});

	public static final Map<EnumProperty<CableConnectionType>, Direction> PROPERTY_TO_DIRECTION_MAP = Util
			.make(Maps.newHashMap(), map -> {
				map.put(OverdriveBlockStates.CABLE_NORTH, Direction.NORTH);
				map.put(OverdriveBlockStates.CABLE_EAST, Direction.EAST);
				map.put(OverdriveBlockStates.CABLE_SOUTH, Direction.SOUTH);
				map.put(OverdriveBlockStates.CABLE_WEST, Direction.WEST);
				map.put(OverdriveBlockStates.CABLE_UP, Direction.UP);
				map.put(OverdriveBlockStates.CABLE_DOWN, Direction.DOWN);
			});

	public static final Properties DEFAULT_CABLE_PROPERTIES = Properties.of(Material.METAL).sound(SoundType.METAL)
			.strength(0.15f).dynamicShape();

	protected final VoxelShape center;

	protected final Map<Direction, VoxelShape> DIRECTION_TO_SHAPE_MAP;

	protected HashMap<HashSet<Direction>, VoxelShape> shapestates = new HashMap<>();

	protected final ICableType type;

	public AbstractCableBlock(OverdriveBlockProperties properties, ICableType type) {
		super(properties);

		this.type = type;

		double bottom = 8 - type.getWidth();
		double top = 8 + type.getWidth();

		DIRECTION_TO_SHAPE_MAP = Util.make(Maps.newEnumMap(Direction.class), map -> {
			map.put(Direction.NORTH, Block.box(bottom, bottom, 0, top, top, top));
			map.put(Direction.EAST, Block.box(bottom, bottom, bottom, 16, top, top));
			map.put(Direction.SOUTH, Block.box(bottom, bottom, bottom, top, top, 16));
			map.put(Direction.WEST, Block.box(0, bottom, bottom, top, top, top));
			map.put(Direction.UP, Block.box(bottom, bottom, bottom, top, 16, top));
			map.put(Direction.DOWN, Block.box(bottom, 0, bottom, top, top, top));
		});

		center = Block.box(bottom, bottom, bottom, top, top, top);

		registerDefaultState(
				getStateDefinition().any().setValue(OverdriveBlockStates.CABLE_UP, CableConnectionType.IGNORED)
						.setValue(OverdriveBlockStates.CABLE_DOWN, CableConnectionType.IGNORED)
						.setValue(OverdriveBlockStates.CABLE_NORTH, CableConnectionType.IGNORED)
						.setValue(OverdriveBlockStates.CABLE_SOUTH, CableConnectionType.IGNORED)
						.setValue(OverdriveBlockStates.CABLE_EAST, CableConnectionType.IGNORED)
						.setValue(OverdriveBlockStates.CABLE_WEST, CableConnectionType.IGNORED));

	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(OverdriveBlockStates.CABLE_UP);
		builder.add(OverdriveBlockStates.CABLE_DOWN);
		builder.add(OverdriveBlockStates.CABLE_NORTH);
		builder.add(OverdriveBlockStates.CABLE_EAST);
		builder.add(OverdriveBlockStates.CABLE_SOUTH);
		builder.add(OverdriveBlockStates.CABLE_WEST);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return true;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {

		VoxelShape shape = center;

		HashSet<Direction> checkedDirs = new HashSet<>();
		for (EnumProperty<CableConnectionType> checkState : OverdriveBlockStates.CABLE_DIRECTIONS) {
			CableConnectionType type = state.getValue(checkState);
			if (type == CableConnectionType.CABLE || type == CableConnectionType.INVENTORY) {
				checkedDirs.add(PROPERTY_TO_DIRECTION_MAP.get(checkState));
			}
		}

		if (shapestates.containsKey(checkedDirs)) {
			return shapestates.get(checkedDirs);
		}

		for (Direction dir : checkedDirs) {
			shape = Shapes.or(shape, DIRECTION_TO_SHAPE_MAP.get(dir));
		}

		shapestates.put(checkedDirs, shape);

		if (shape == null) {
			return Shapes.empty();
		}

		return shape;
	}

	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState stateIn, @Nullable LivingEntity placer,
			ItemStack stack) {
		worldIn.setBlockAndUpdate(pos, handleConnectionUpdate(stateIn, pos, worldIn));
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor world,
			BlockPos currentPos, BlockPos facingPos) {

		if (stateIn.getValue(BlockStateProperties.WATERLOGGED) == Boolean.TRUE) {
			world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}

		if (shouldntChange(stateIn, facingState, facing, (AbstractCableTile<?>) world.getBlockEntity(currentPos), world.getBlockEntity(facingPos))) {
			return stateIn;
		}

		return handleConnectionUpdate(stateIn, currentPos, world);
	}

	protected boolean shouldntChange(BlockState thisState, BlockState changedState, Direction facing,
			AbstractCableTile<?> thisTile, BlockEntity facingTile) {

		EnumProperty<CableConnectionType> thisProperty = DIRECTION_TO_PROPERTY_MAP.get(facing);
		EnumProperty<CableConnectionType> facingProperty = DIRECTION_TO_PROPERTY_MAP.get(facing.getOpposite());

		CableConnectionType thisType = thisState.getValue(thisProperty);

		// do not combine!
		if (changedState.hasProperty(facingProperty)) {
			return thisType == changedState.getValue(facingProperty);
		} else {
			if (thisType == CableConnectionType.IGNORED || thisType == CableConnectionType.NONE
					|| thisType == CableConnectionType.NONE_SEAMLESS) {
				return !thisTile.isValidConnection(facingTile, facing.getOpposite());
			}
			return false;
		}
	}

	public BlockState handleConnectionUpdate(BlockState startingState, BlockPos pos, LevelAccessor world) {

		HashSet<Direction> dirsUsed = new HashSet<>();
		HashSet<Direction> inventory = new HashSet<>();
		HashSet<Direction> cable = new HashSet<>();

		for (EnumProperty<CableConnectionType> checkState : OverdriveBlockStates.CABLE_DIRECTIONS) {
			startingState = startingState.setValue(checkState, CableConnectionType.IGNORED);
		}

		sortDirections(dirsUsed, inventory, cable, world, pos);

		boolean shouldntSkip = true;

		if (dirsUsed.size() > 2 || dirsUsed.size() < 2) {
			for (EnumProperty<CableConnectionType> checkState : OverdriveBlockStates.CABLE_DIRECTIONS) {
				startingState = startingState.setValue(checkState, CableConnectionType.NONE);
			}
			shouldntSkip = false;
		}

		if (shouldntSkip) {
			boolean notFound = true;
			// each pair has it's seamless stored on a unique pair for OR operator
			if (dirsUsed.contains(Direction.NORTH) && dirsUsed.contains(Direction.SOUTH)) {
				startingState = startingState.setValue(OverdriveBlockStates.CABLE_EAST,
						CableConnectionType.NONE_SEAMLESS);
				startingState = startingState.setValue(OverdriveBlockStates.CABLE_WEST,
						CableConnectionType.NONE_SEAMLESS);
				notFound = false;
			}

			if (dirsUsed.contains(Direction.EAST) && dirsUsed.contains(Direction.WEST)) {
				startingState = startingState.setValue(OverdriveBlockStates.CABLE_UP,
						CableConnectionType.NONE_SEAMLESS);
				startingState = startingState.setValue(OverdriveBlockStates.CABLE_DOWN,
						CableConnectionType.NONE_SEAMLESS);
				notFound = false;
			}

			if (dirsUsed.contains(Direction.UP) && dirsUsed.contains(Direction.DOWN)) {
				startingState = startingState.setValue(OverdriveBlockStates.CABLE_NORTH,
						CableConnectionType.NONE_SEAMLESS);
				startingState = startingState.setValue(OverdriveBlockStates.CABLE_SOUTH,
						CableConnectionType.NONE_SEAMLESS);
				notFound = false;
			}

			if (notFound) {
				for (EnumProperty<CableConnectionType> checkState : OverdriveBlockStates.CABLE_DIRECTIONS) {
					startingState = startingState.setValue(checkState, CableConnectionType.NONE);
				}
			}
		}

		for (Direction dir : inventory) {
			startingState = startingState.setValue(DIRECTION_TO_PROPERTY_MAP.get(dir), CableConnectionType.INVENTORY);
		}

		for (Direction dir : cable) {
			startingState = startingState.setValue(DIRECTION_TO_PROPERTY_MAP.get(dir), CableConnectionType.CABLE);
		}

		return startingState;
	}

	public ICableType getCableType() {
		return type;
	}

	protected abstract void sortDirections(HashSet<Direction> usedDirs, HashSet<Direction> inventory,
			HashSet<Direction> cable, LevelAccessor world, BlockPos pos);

}
