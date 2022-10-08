package matteroverdrive.core.tile.types.cable;

import java.util.ArrayList;
import java.util.HashSet;

import javax.annotation.Nullable;

import matteroverdrive.common.block.cable.AbstractCableBlock;
import matteroverdrive.common.block.cable.ICableType;
import matteroverdrive.common.event.ServerEventHandler;
import matteroverdrive.core.network.AbstractCableNetwork;
import matteroverdrive.core.tile.GenericTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractCableTile<NETWORK extends AbstractCableNetwork> extends GenericTile {

	protected boolean[] connections = new boolean[6];
	protected BlockEntity[] tileConnections = new BlockEntity[6];

	protected NETWORK network;
	protected ICableType cableType;

	protected AbstractCableTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public void removeFromNetwork() {
		if (network != null) {
			network.removeFromNetwork(this);
		}
	}

	public void setNetwork(AbstractCableNetwork network) {
		if (this.network != network) {
			removeFromNetwork();
			this.network = (NETWORK) network;
		}
	}

	public BlockEntity[] getAdjacentConnections() {
		return tileConnections;
	}

	public ICableType getConductorType() {
		if (cableType == null) {
			cableType = ((AbstractCableBlock) getBlockState().getBlock()).getCableType();
		}
		return cableType;
	}

	public void refreshNetworkIfChange() {
		if (updateAdjacent()) {
			refreshNetwork();
		}
	}

	public AbstractCableNetwork getNetwork() {
		return getNetwork(true);
	}

	public abstract AbstractCableNetwork getNetwork(boolean createIfNull);

	public void refreshNetwork() {
		if (!level.isClientSide) {
			updateAdjacent();
			ArrayList<NETWORK> foundNetworks = new ArrayList<>();
			for (Direction dir : Direction.values()) {
				BlockEntity facing = level.getBlockEntity(new BlockPos(worldPosition).relative(dir));
				if (isCable(facing)) {
					foundNetworks.add((NETWORK) ((AbstractCableTile<NETWORK>) facing).getNetwork());
				}
			}
			if (!foundNetworks.isEmpty()) {
				foundNetworks.get(0).cables.add(this);
				network = foundNetworks.get(0);
				if (foundNetworks.size() > 1) {
					foundNetworks.remove(0);
					for (NETWORK network : foundNetworks) {
						getNetwork().merge(network);
					}
				}
			}
			getNetwork().refresh();
		}
	}

	@Override
	public void setRemoved() {
		if (!level.isClientSide && network != null) {
			network.split(this);
		}
		super.setRemoved();
	}

	@Override
	public void onChunkUnloaded() {
		if (!level.isClientSide && network != null) {
			network.split(this);
		}
	}

	@Override
	public void onLoad() {
		super.onLoad();
		ServerEventHandler.TASK_HANDLER.queueTask(this::refreshNetwork);
	}

	@Override
	public void saveAdditional(CompoundTag compound) {
		compound.putInt("ord", getConductorType().getOrdinal());
		super.saveAdditional(compound);
	}

	public boolean updateAdjacent() {
		boolean flag = false;
		for (Direction dir : Direction.values()) {
			BlockEntity tile = level.getBlockEntity(worldPosition.relative(dir));
			boolean is = isValidConnection(tile, dir);
			if (connections[dir.ordinal()] != is) {
				connections[dir.ordinal()] = is;
				tileConnections[dir.ordinal()] = tile;
				flag = true;
			}

		}
		if (flag) {
			setChanged();
		}
		return flag;
	}

	protected HashSet<AbstractCableTile<NETWORK>> getConnectedConductors() {
		HashSet<AbstractCableTile<NETWORK>> set = new HashSet<>();
		for (Direction dir : Direction.values()) {
			BlockEntity facing = level.getBlockEntity(new BlockPos(worldPosition).relative(dir));
			if (isCable(facing)) {
				set.add((AbstractCableTile<NETWORK>) facing);
			}
		}
		return set;
	}
	
	@Override
	public void onNeighborChange(BlockState state, BlockPos neighbor) {
		if(level.isClientSide) {
			return;
		}
		BlockEntity tile = level.getBlockEntity(neighbor);
		if (isCable(tile)) {
			((AbstractCableTile<?>) tile).refreshNetwork();
		}
	}
	
	@Override
	public void onTilePlaced(BlockState state, BlockState oldState, boolean isMoving) {
		if(level.isClientSide) {
			return;
		}
		refreshNetwork();
	}
	
	@Override
	public void onBlockStateChange(BlockState oldState, BlockState newState, boolean moving) {
		
	}

	public abstract boolean isCable(BlockEntity entity);

	public abstract boolean isValidConnection(BlockEntity entity, @Nullable Direction direction);

}
