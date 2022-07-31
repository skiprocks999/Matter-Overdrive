package matteroverdrive.core.block.state;

import com.hrznstudio.titanium.block.RotatableBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import javax.annotation.Nonnull;

public class StateVariables {

  /**
   * The {@link Boolean} value which states if the block can be waterlogged or not.
   */
  private final boolean canBeWaterlogged;


  /**
   * The {@link RotatableBlock.RotationType} of the Block.
   */
  @Nonnull
  private final RotatableBlock.RotationType rotationType;

  /**
   * The {@link Boolean} value which states if the block can be lit or not.
   */
  private final boolean canBeLit;

  /**
   * Empty Private Constructor to make it so people don't instantiate new objects directly.
   */
  private StateVariables(final boolean isWaterloggable, @Nonnull final RotatableBlock.RotationType rotationType, final boolean canBeLit) {
    this.canBeWaterlogged = isWaterloggable;
    this.rotationType = rotationType;
    this.canBeLit = canBeLit;
  }

  /**
   * @return Returns a new {@link StateVariables.Builder} object.
   */
  public static Builder getBuilder() {
    return new Builder();
  }

  /**
   * Copies another {@link StateVariables} object's properties into a new {@link Builder} object.
   *
   * @param variables The existing {@link StateVariables} object.
   * @return Returns a mutable {@link Builder} that inherits the values of the immutable {@link StateVariables} object passed in.
   */
  public static Builder copy(StateVariables variables) {
    Builder copy = getBuilder();
    copy.canBeWaterlogged = variables.canBeWaterlogged;
    copy.rotationType = variables.rotationType;
    return copy;
  }

  /**
   * @return Returns the value of {@link #canBeWaterlogged}.
   */
  public boolean canBeWaterlogged() {
    return canBeWaterlogged;
  }

  /**
   * @return Returns the value of {@link #rotationType}.
   */
  @Nonnull
  public RotatableBlock.RotationType getRotationType() {
    return rotationType;
  }

  /**
   * @return Returns the value of {@link #canBeLit}
   */
  public boolean canBeLit() {
    return canBeLit;
  }

  /**
   * The mutable {@link Builder} object for the {@link StateVariables}.
   */
  public static class Builder {

    /**
     * The value stating if the block should be waterlogged or not.
     */
    boolean canBeWaterlogged = false;

    /**
     * The {@link RotatableBlock.RotationType} of the Block.
     */
    @Nonnull
    RotatableBlock.RotationType rotationType = RotatableBlock.RotationType.NONE;

    /**
     * The value stating if the block can be lit or not.
     */
    boolean canBeLit = false;

    /**
     * Default Constructor
     */
    public Builder() {}

    /**
     * Used to specify if the {@link Block} can be waterlogged, and thus should implement the {@link BlockStateProperties#WATERLOGGED} property.
     *
     * @return Returns the builder.
     */
    public Builder canBeWaterlogged() {
      this.canBeWaterlogged = true;
      return this;
    }

    /**
     * Used to specify the
     *
     * @param rotationType The {@link RotatableBlock.RotationType} enum to use for rotation handling for the block.
     * @return Returns the builder.
     */
    public Builder withRotationType(@Nonnull RotatableBlock.RotationType rotationType) {
      this.rotationType = rotationType;
      return this;
    }

    public Builder canBeLit() {
      this.canBeLit = true;
      return this;
    }

    /**
     * @return Returns the immutable {@link StateVariables} object.
     */
    public StateVariables build() {
      return new StateVariables(canBeWaterlogged, rotationType, canBeLit);
    }
  }

  public static class Defaults {
    // No Rotation
    public static final StateVariables defaultMachine = StateVariables.getBuilder().build();
    public static final StateVariables litMachine = StateVariables.copy(defaultMachine)
            .canBeLit().build();
    public static final StateVariables waterloggableMachine = StateVariables.copy(defaultMachine)
            .canBeWaterlogged().build();
    public static final StateVariables waterloggableLit = StateVariables.copy(waterloggableMachine)
            .canBeLit().build();

    // With Rotations;
    public static final StateVariables defaultFourWay = StateVariables.copy(defaultMachine)
            .withRotationType(RotatableBlock.RotationType.FOUR_WAY).build();
    public static final StateVariables defaultSixWay = StateVariables.copy(defaultMachine)
            .withRotationType(RotatableBlock.RotationType.SIX_WAY).build();
    public static final StateVariables litFourWay = StateVariables.copy(litMachine)
            .withRotationType(RotatableBlock.RotationType.FOUR_WAY).build();
    public static final StateVariables litSixWay = StateVariables.copy(litMachine)
            .withRotationType(RotatableBlock.RotationType.SIX_WAY).build();
    public static final StateVariables waterloggableFourway = StateVariables.copy(waterloggableMachine)
            .withRotationType(RotatableBlock.RotationType.FOUR_WAY).build();
    public static final StateVariables waterloggableSixway = StateVariables.copy(waterloggableMachine)
            .withRotationType(RotatableBlock.RotationType.SIX_WAY).build();
    public static final StateVariables waterloggableLitFourway = StateVariables.copy(waterloggableLit)
            .withRotationType(RotatableBlock.RotationType.FOUR_WAY).build();
    public static final StateVariables waterloggableLitSixway = StateVariables.copy(waterloggableLit)
            .withRotationType(RotatableBlock.RotationType.SIX_WAY).build();
  }
}
