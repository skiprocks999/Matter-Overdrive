package matteroverdrive.core.tile;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.redstone.IRedstoneReader;
import com.hrznstudio.titanium.api.redstone.IRedstoneState;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.redstone.RedstoneAction;
import com.hrznstudio.titanium.block.redstone.RedstoneManager;
import com.hrznstudio.titanium.block.redstone.RedstoneState;
import com.hrznstudio.titanium.block.tile.MachineTile;
import matteroverdrive.core.sound.tile.ITickingSoundTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public abstract class GenericRedstoneSoundTile<T extends MachineTile<T>> extends GenericTile<T> implements IRedstoneReader, ITickingSoundTile {

  @Save
  private final RedstoneManager<RedstoneAction> redstoneManager;

  @Save
  private boolean shouldPlaySound = false;

  public GenericRedstoneSoundTile(BasicTileBlock<T> base, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
    super(base, blockEntityType, pos, state);
    this.redstoneManager = new RedstoneManager<>(RedstoneAction.IGNORE, false);
  }

  @Override
  public void onNeighborChanged(@Nonnull Block blockIn, @Nonnull BlockPos fromPos) {
    super.onNeighborChanged(blockIn, fromPos);
    if (this.redstoneManager != null) {
      this.redstoneManager.setLastRedstoneState(this.getEnvironmentValue(false, null).isReceivingRedstone());
    }
  }

  @Nonnull
  @Override
  public IRedstoneState getEnvironmentValue(boolean strongPower, @Nullable Direction direction) {
    if (strongPower && this.level != null) {
      if (direction == null) {
        return this.level.hasNeighborSignal(this.worldPosition) ? RedstoneState.ON : RedstoneState.OFF;
      }
      return this.level.hasSignal(this.worldPosition, direction) ? RedstoneState.ON : RedstoneState.OFF;
    } else {
      return Objects.requireNonNull(this.level).getBestNeighborSignal(this.worldPosition) > 0 ? RedstoneState.ON : RedstoneState.OFF;
    }
  }

  @Override
  public boolean shouldPlaySound() {
    return this.shouldPlaySound;
  }

  @Override
  public void setNotPlaying() {
    this.shouldPlaySound = false;
    this.markForUpdate();
  }


}
