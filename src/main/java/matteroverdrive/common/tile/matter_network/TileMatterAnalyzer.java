package matteroverdrive.common.tile.matter_network;

import matteroverdrive.core.cable.types.matter_network.IMatterNetworkMember;
import matteroverdrive.core.tile.types.GenericSoundTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TileMatterAnalyzer extends GenericSoundTile implements IMatterNetworkMember {

	public TileMatterAnalyzer(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean shouldPlaySound() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setNotPlaying() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getMaxMode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean canConnectToFace(Direction face) {
		// TODO Auto-generated method stub
		return false;
	}

}
