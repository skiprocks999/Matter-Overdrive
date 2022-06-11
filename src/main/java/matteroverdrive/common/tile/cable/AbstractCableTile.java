package matteroverdrive.common.tile.cable;

import java.util.ArrayList;
import java.util.HashSet;

import javax.annotation.Nullable;

import com.google.common.collect.Sets;

import matteroverdrive.common.block.cable.AbstractCableBlock;
import matteroverdrive.common.block.cable.ICableType;
import matteroverdrive.core.network.BaseNetwork;
import matteroverdrive.core.tile.GenericTile;
import matteroverdrive.core.utils.misc.Scheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractCableTile<NETWORK extends BaseNetwork> extends GenericTile {

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

	public void setNetwork(BaseNetwork network) {
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
			cableType = ((AbstractCableBlock) getBlockState().getBlock()).getConductorType();
		}
		return cableType;
	}

	public void refreshNetworkIfChange() {
		if (updateAdjacent()) {
			refreshNetwork();
		}
	}
	
	public BaseNetwork getNetwork() {
		return getNetwork(true);
	}
	
	public abstract BaseNetwork getNetwork(boolean createIfNull);
	
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
			getNetwork().split(this);
		}
		super.setRemoved();
	}

	@Override
	public void onChunkUnloaded() {
		if (!level.isClientSide && network != null) {
			getNetwork().split(this);
		}
	}
	
	@Override
	public void onLoad() {
		super.onLoad();
		Scheduler.schedule(1, this::refreshNetwork);
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

	public abstract boolean isCable(BlockEntity entity);
	
	public abstract boolean isValidConnection(BlockEntity entity, @Nullable Direction direction);
	
}
