package matteroverdrive.common.tile.matter_network;

import matteroverdrive.core.network.utils.IMatterNetworkMember;
import matteroverdrive.core.tile.GenericTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TilePatternDrive extends GenericTile implements IMatterNetworkMember {

	public TilePatternDrive(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean canConnectToFace(Direction face) {
		// TODO Auto-generated method stub
		return false;
	}

}
