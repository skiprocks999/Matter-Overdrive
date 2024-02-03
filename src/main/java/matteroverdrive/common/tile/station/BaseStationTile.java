package matteroverdrive.common.tile.station;

import matteroverdrive.core.tile.types.GenericMachineTile;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseStationTile extends GenericMachineTile {

	protected BaseStationTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public boolean isUsableByPlayer(Player player) {
		return true;
	}

}
