package matteroverdrive.common.tile.station;

import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryAndroidStation;
import matteroverdrive.core.android.api.ICapabilityAndroid;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.registry.TileRegistry;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileAndroidStation extends BaseStationTile {

	public static final AABB RENDER_AABB = new net.minecraft.world.phys.AABB(-1, 0, -1, 2, 3, 2);
	
	public TileAndroidStation(BlockPos pos, BlockState state) {
		super(TileRegistry.TILE_ANDROID_STATION.get(), pos, state);
		addCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, CapabilityInventory.EMPTY);
		setMenuProvider(new SimpleMenuProvider((id, inv, play) -> new InventoryAndroidStation(id, play.getInventory(),
				getInventoryCap(), getCoordsData()), getContainerName(TypeMachine.ANDROID_STATION.id())));
	}

	@Override
	public boolean isUsableByPlayer(LocalPlayer player) {
		return player.getCapability(MatterOverdriveCapabilities.ANDROID_DATA).map(ICapabilityAndroid::isAndroid)
				.orElse(false);
	}

	@Override
	public AABB getRenderBoundingBox() {
		return RENDER_AABB.move(this.worldPosition);
	}

}
