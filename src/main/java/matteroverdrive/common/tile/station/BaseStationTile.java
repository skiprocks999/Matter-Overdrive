package matteroverdrive.common.tile.station;

import matteroverdrive.core.tile.types.GenericMachineTile;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseStationTile extends GenericMachineTile {

  protected final Component title;

  protected BaseStationTile(Component title, BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
    this.title = title;
  }

  public boolean isUsableByPlayer(LocalPlayer player) {
    return true;
  }

  @Override
  public Component getName() {
    return this.title;
  }

  @Override
  public Component getDisplayName() {
    return this.title;
  }
}
