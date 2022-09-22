package matteroverdrive.common.tile.station;

import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryAndroidStation;
import matteroverdrive.core.android.api.ICapabilityAndroid;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.registry.TileRegistry;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class TileAndroidStation extends BaseStationTile {

  public TileAndroidStation(BlockPos pos, BlockState state) {
    super(Component.translatable("gui.matteroverdrive.androidstation"), TileRegistry.TILE_CHARGER.get(), pos, state);
    setMenuProvider(new SimpleMenuProvider(
            (id, inv, play) -> new InventoryAndroidStation(
                    id,
                    play.getInventory(),
                    getInventoryCap(),
                    getCoordsData()),
            getContainerName(TypeMachine.ANDROID_STATION.id()))
    );
  }


  @Override
  public boolean isUsableByPlayer(LocalPlayer player) {
    return player.getCapability(MatterOverdriveCapabilities.ANDROID_DATA)
            .map(ICapabilityAndroid::isAndroid)
            .orElse(false);
  }

  public static final AABB RENDER_AABB = new net.minecraft.world.phys.AABB(-1, 0, -1, 2, 3, 2);
  @Override
  public AABB getRenderBoundingBox() {
    return RENDER_AABB.move(this.worldPosition);
  }


}
