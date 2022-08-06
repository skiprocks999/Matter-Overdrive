package matteroverdrive.core.block;

import com.hrznstudio.titanium.block.RotatableBlock.RotationType;
import com.hrznstudio.titanium.block.RotationHandler;
import com.hrznstudio.titanium.block.tile.BasicTile;
import matteroverdrive.core.block.state.OverdriveBlockProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GenericStateVariableBlock<T extends BasicTile<T>> extends GenericEntityBlock<T> {

  /**
   * Default Constructor for GenericStateVariableBlock.
   *
   * @param properties The blocks BlockBehaviour Properties.
   * @param name The "name" of the block (IE. "charger_block")
   * @param tileClass The BlockEntity Class for the block.
   */
  protected GenericStateVariableBlock(OverdriveBlockProperties properties, String name, Class<T> tileClass) {
    super(properties, name, tileClass);
    BlockState defaultState = getStateDefinition().any();
    OverdriveBlockProperties stateProperties = (OverdriveBlockProperties)this.properties;
    if (stateProperties.canBeWaterlogged()) {
      defaultState.setValue(BlockStateProperties.WATERLOGGED, false);
    }
    if (stateProperties.canBeLit()) {
      defaultState.setValue(BlockStateProperties.LIT, false);
    }
    registerDefaultState(defaultState);
  }

  /**
   * Creates our default {@link StateDefinition}.
   * In our case:
   * - If the value of the local {@link OverdriveBlockProperties#canBeWaterlogged()} is true then it adds the {@link BlockStateProperties#WATERLOGGED} property.
   * - If our {@link OverdriveBlockProperties#getRotationType()} )'s "property" list isn't null then we can add the associated properties of our {@link RotationType}.
   *
   * @param builder The passed in {@link StateDefinition.Builder} for adding the properties to.
   */
  @Override
  protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    OverdriveBlockProperties stateProperties = (OverdriveBlockProperties)this.properties;
    if (stateProperties.canBeWaterlogged()) {
      builder.add(BlockStateProperties.WATERLOGGED);
    }
    if (stateProperties.getRotationType().getProperties().length > 0) {
      builder.add(stateProperties.getRotationType().getProperties());
    }
    if (stateProperties.canBeLit()) {
      builder.add(BlockStateProperties.LIT);
    }
  }

  /**
   * Getter for RotationType.
   *
   * @return returns the blocks {@link RotationType}.
   */
  public RotationType getRotationType() {
    return ((OverdriveBlockProperties)this.properties).getRotationType();
  }

  /**
   * Logic goes:
   * 1. Get the Default {@link BlockState} + Modifications by the {@link RotationHandler}.
   * 2. Get the {@link FluidState} -> If the fluidstate is of type {@link Fluids#WATER} then set the value of {@link BlockStateProperties#WATERLOGGED} to true.
   *
   * @param context The context for the block placement.
   * @return Returns the BlockState of the placed block to use.
   */
  @Nullable
  @Override
  public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
    BlockState stateWithRotation = this.getRotationType().getHandler().getStateForPlacement(this, context);
    FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
    return fluidState.getType() == Fluids.WATER ? stateWithRotation.setValue(BlockStateProperties.WATERLOGGED, true) : stateWithRotation;
  }

  /**
   * Used to update the fluid if need be, by scheduling a fluid tick with a delayed value.
   *
   * @param state The {@link BlockState} of the {@link Block} whose shape needs updating.
   * @param direction The direction of the {@link Block}.
   * @param neighborState The neighbouring {@link BlockState}.
   * @param level The {@link Level} of the {@link Block}.
   * @param currentPos The current {@link BlockPos} of the {@link Block}.
   * @param neighborPos The neighbouring {@link Block}'s {@link BlockPos}.
   * @return Returns the blocks state unless modifications occur.
   */
  @SuppressWarnings("deprecation")
  @Override
  public @NotNull BlockState updateShape(BlockState state, @NotNull Direction direction,
                                         @NotNull BlockState neighborState, @NotNull LevelAccessor level,
                                         @NotNull BlockPos currentPos, @NotNull BlockPos neighborPos) {
    if (getProperty(state, BlockStateProperties.WATERLOGGED) != null) {
      level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
    }
    return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
  }

  /**
   * Override the default getFluidState method to be able to return the Water fluid if the block is waterlogged.
   *
   * @param state The {@link BlockState} of the {@link Block}.
   * @return Returns the {@link FluidState} of the {@link Block}.
   */
  @SuppressWarnings("deprecation")
  @Override
  public @NotNull FluidState getFluidState(BlockState state) {
    return getProperty(state, BlockStateProperties.WATERLOGGED) != null ? Fluids.WATER.getSource(false) : super.getFluidState(state);
  }

  /**
   * Rotates our {@link Block} using our blocks provided {@link RotationType} handler.
   *
   * @param state The current {@link BlockState} of the {@link Block}.
   * @param rotation The current {@link Rotation} of the {@link Block}.
   * @return Returns the new state of the block.
   */
  @SuppressWarnings("deprecation")
  @Override
  public @NotNull BlockState rotate(@NotNull BlockState state, @NotNull Rotation rotation) {
    if (getRotationType().getProperties().length > 0){
      return state.setValue(getRotationType().getProperties()[0], rotation.rotate(state.getValue(getRotationType().getProperties()[0])));
    }
    return super.rotate(state, rotation);
  }

  /**
   * Mirrors our {@link Block} using our blocks provided {@link RotationType} handler.
   *
   * @param state The current {@link BlockState} of the {@link Block}.
   * @param mirror The current {@link Mirror} state of the {@link Block}.
   * @return Returns the new state of the block.
   */
  @SuppressWarnings("deprecation")
  @Override
  public @NotNull BlockState mirror(@NotNull BlockState state, @NotNull Mirror mirror) {
    if (getRotationType().getProperties().length > 0){
      return state.rotate(mirror.getRotation(state.getValue(getRotationType().getProperties()[0])));
    }
    return super.mirror(state, mirror);
  }

  /**
   * @param state
   * @param level
   * @param pos
   * @return
   */
  @Override
  public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
    return getProperty(state, BlockStateProperties.LIT) != null ? 15 : super.getLightEmission(state, level, pos);
  }

  public DirectionProperty getRotationProperty() {
    return getRotationType().getProperties()[0];
  }

  @Nullable
  public <U extends Comparable<U>> U getProperty(BlockState state, Property<U> property) {
    if (state.hasProperty(property)) {
      return state.getValue(property);
    }
    return null;
  }
}
