package matteroverdrive.core.tile;

import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.ActiveTile;
import com.hrznstudio.titanium.block.tile.MachineTile;
import matteroverdrive.core.tile.utils.ITickableTile;
import matteroverdrive.core.tile.utils.IUpdatableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GenericTile<T extends MachineTile<T>> extends MachineTile<T> implements Nameable {

  public GenericTile(BasicTileBlock<T> base, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
    super(base, blockEntityType, pos, state);
  }

  @NotNull
  @Override
  public <U> LazyOptional<U> getCapability(@NotNull Capability<U> cap, @Nullable Direction side) {
    return super.getCapability(cap, side);
  }
}
