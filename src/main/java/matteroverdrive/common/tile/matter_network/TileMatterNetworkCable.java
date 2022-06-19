package matteroverdrive.common.tile.matter_network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.block.type.TypeMatterNetworkCable;
import matteroverdrive.common.network.NetworkMatter;
import matteroverdrive.common.tile.cable.AbstractCableTile;
import matteroverdrive.core.network.AbstractCableNetwork;
import matteroverdrive.core.network.utils.IMatterNetworkMember;
import matteroverdrive.core.utils.misc.Scheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TileMatterNetworkCable extends AbstractCableTile<NetworkMatter> {
	
	public TileMatterNetworkCable(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_MATTER_NETWORK_CABLE.get(), pos, state);
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		cableType = TypeMatterNetworkCable.values()[compound.getInt("ord")];
	}

	@Override
	public boolean isCable(BlockEntity entity) {
		return entity instanceof TileMatterNetworkCable;
	}

	@Override
	public boolean isValidConnection(BlockEntity entity, Direction dir) {
		return entity instanceof IMatterNetworkMember;
	}
	
	@Override
	public AbstractCableNetwork getNetwork(boolean createIfNull) {
		if (network == null && createIfNull) {
			HashSet<AbstractCableTile<NetworkMatter>> adjacentCables = getConnectedConductors();
			HashSet<NetworkMatter> connectedNets = new HashSet<>();
			for (AbstractCableTile<NetworkMatter> wire : adjacentCables) {
				NetworkMatter network = (NetworkMatter) wire.getNetwork(false);
				if (network != null) {
					connectedNets.add(network);
				}
			}
			if (connectedNets.isEmpty()) {
				network = new NetworkMatter(Arrays.asList(this), level.isClientSide);
			} else {
				if (connectedNets.size() == 1) {
					network = (NetworkMatter) connectedNets.toArray()[0];
				} else {
					network = new NetworkMatter(connectedNets, level.isClientSide);
				}
				network.cables.add(this);
			}
		}
		return network;
	}
	
	@Override
	public void handleUpdateTag(CompoundTag tag) {
		super.handleUpdateTag(tag);
		Scheduler.schedule(1, this::refreshNetwork, true);
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag superTag =  super.getUpdateTag();
		superTag.putBoolean("temp", false);
		return superTag;
	}
	
	public void refreshNetwork() {
		updateAdjacent();
		ArrayList<NetworkMatter> foundNetworks = new ArrayList<>();
		for (Direction dir : Direction.values()) {
			BlockEntity facing = level.getBlockEntity(new BlockPos(worldPosition).relative(dir));
			if (isCable(facing)) {
				foundNetworks.add((NetworkMatter) ((TileMatterNetworkCable) facing).getNetwork());
			}
		}
		if (!foundNetworks.isEmpty()) {
			foundNetworks.get(0).cables.add(this);
			network = foundNetworks.get(0);
			if (foundNetworks.size() > 1) {
				foundNetworks.remove(0);
				for (NetworkMatter network : foundNetworks) {
					getNetwork().merge(network);
				}
			}
		}
		getNetwork().refresh();
		
	}

	@Override
	public void setRemoved() {
		if (network != null) {
			network.split(this);
		}
		super.setRemoved();
	}

	@Override
	public void onChunkUnloaded() {
		if (network != null) {
			network.split(this);
		}
	}

}
