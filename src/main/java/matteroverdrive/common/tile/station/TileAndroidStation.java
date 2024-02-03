package matteroverdrive.common.tile.station;

import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryAndroidStation;
import matteroverdrive.core.android.api.ICapabilityAndroid;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.registry.TileRegistry;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class TileAndroidStation extends BaseStationTile {

	public static final AABB RENDER_AABB = new net.minecraft.world.phys.AABB(-1, 0, -1, 2, 3, 2);
	
	public TileAndroidStation(BlockPos pos, BlockState state) {
		super(TileRegistry.TILE_ANDROID_STATION.get(), pos, state);
		addCapability(ForgeCapabilities.ITEM_HANDLER, CapabilityInventory.EMPTY);
		setMenuProvider(new SimpleMenuProvider((id, inv, play) -> new InventoryAndroidStation(id, play.getInventory(),
				getInventoryCap(), getCoordsData()), getContainerName(TypeMachine.ANDROID_STATION.id())));
	}

	@Override
	public boolean isUsableByPlayer(Player player) {
		return player.getCapability(MatterOverdriveCapabilities.ANDROID_DATA).map(ICapabilityAndroid::isAndroid)
				.orElse(false);
	}
	
	@Override
	public InteractionResult useClient(Player player, InteractionHand hand, BlockHitResult hit) {
		if(player.getCapability(MatterOverdriveCapabilities.ANDROID_DATA).map(ICapabilityAndroid::isAndroid)
				.orElse(false)) {
			return super.useClient(player, hand, hit);
		}
		return InteractionResult.PASS;
	}
	
	@Override
	public InteractionResult useServer(Player player, InteractionHand hand, BlockHitResult hit) {
		if(player.getCapability(MatterOverdriveCapabilities.ANDROID_DATA).map(ICapabilityAndroid::isAndroid)
				.orElse(false)) {
			return super.useClient(player, hand, hit);
		}
		return InteractionResult.PASS;
	}

	@Override
	public AABB getRenderBoundingBox() {
		return RENDER_AABB.move(this.worldPosition);
	}

}
