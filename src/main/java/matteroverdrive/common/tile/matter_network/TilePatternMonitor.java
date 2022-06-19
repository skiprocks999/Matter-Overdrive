package matteroverdrive.common.tile.matter_network;

import javax.annotation.Nullable;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.block.states.OverdriveBlockStates;
import matteroverdrive.common.block.states.OverdriveBlockStates.VerticalFacing;
import matteroverdrive.common.network.NetworkMatter;
import matteroverdrive.core.network.utils.IMatterNetworkMember;
import matteroverdrive.core.tile.GenericTile;
import matteroverdrive.core.utils.UtilsDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TilePatternMonitor extends GenericTile implements IMatterNetworkMember {

	public TilePatternMonitor(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_PATTERN_MONITOR.get(), pos, state);
	}

	@Override
	public boolean canConnectToFace(Direction face) {
		VerticalFacing vertical = getBlockState().getValue(OverdriveBlockStates.VERTICAL_FACING);
		if(vertical == null || vertical == VerticalFacing.NONE) {
			Direction facing = getFacing();
			Direction relative = UtilsDirection.getRelativeSide(Direction.NORTH, facing);
			return relative == face;
		} else {
			return face == vertical.mapped.getOpposite();
		}
		
	}

	@Override
	@Nullable
	public NetworkMatter getConnectedNetwork() {
		Direction back = UtilsDirection.getRelativeSide(Direction.NORTH, getFacing());
		BlockEntity entity = getLevel().getBlockEntity(getBlockPos().relative(back));
		if(entity != null && entity instanceof TileMatterNetworkCable cable) {
			return (NetworkMatter) cable.getNetwork(false);
		}
		return null;
	}

}
