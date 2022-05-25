package matteroverdrive.common.tile;

import matteroverdrive.core.cable.AbstractNetwork;
import matteroverdrive.core.cable.api.ICableNetwork;
import matteroverdrive.core.cable.types.matter_network.IMatterNetworkCable;
import matteroverdrive.core.tile.GenericTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TileMatterNetworkCable extends GenericTile implements IMatterNetworkCable {

	protected TileMatterNetworkCable(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void removeFromNetwork() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AbstractNetwork<?, ?, ?, ?> getAbstractNetwork() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BlockEntity[] getAdjacentConnections() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getConductorType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICableNetwork getNetwork() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICableNetwork getNetwork(boolean createIfNull) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void refreshNetwork() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refreshNetworkIfChange() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNetwork(AbstractNetwork<?, ?, ?, ?> aValueNetwork) {
		// TODO Auto-generated method stub
		
	}

}
