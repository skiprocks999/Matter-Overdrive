package matteroverdrive.common.tile.matter_network;

import javax.annotation.Nullable;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.network.NetworkMatter;
import matteroverdrive.core.network.utils.IMatterNetworkMember;
import matteroverdrive.core.tile.types.GenericSoundTile;
import matteroverdrive.core.utils.UtilsDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TileMatterReplicator extends GenericSoundTile implements IMatterNetworkMember {

	public TileMatterReplicator(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_MATTER_REPLICATOR.get(), pos, state);
		
		setTickable();
		setHasMenuData();
		setHasRenderData();
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
		Direction facing = getFacing();
		Direction back = Direction.NORTH;
		Direction relative = UtilsDirection.getRelativeSide(back, facing);
		return relative == face;
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
	
	public CompoundTag getNetworkData() {
		CompoundTag data = new CompoundTag();
		
		return data;
	}
	
	public void handleNetworkData(CompoundTag tag) {
		
	}

}
