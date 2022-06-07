package matteroverdrive.common.tile.matter_network;

import java.util.ArrayList;
import java.util.HashSet;

import com.google.common.collect.Sets;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.block.cable.BlockMatterNetworkCable;
import matteroverdrive.common.block.type.TypeMatterNetworkCable;
import matteroverdrive.common.cable_network.MatterNetwork;
import matteroverdrive.core.network.AbstractNetwork;
import matteroverdrive.core.network.cable.utils.IMatterNetworkMember;
import matteroverdrive.core.network.cable.utils.INetworkCable;
import matteroverdrive.core.tile.GenericTile;
import matteroverdrive.core.utils.misc.Scheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TileMatterNetworkCable extends GenericTile implements INetworkCable {

	public MatterNetwork matterNetwork;
	public TypeMatterNetworkCable cable = null;
	
	public TileMatterNetworkCable(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_MATTER_NETWORK_CABLE.get(), pos, state);
	}

	@Override
	public void removeFromNetwork() {
		if (matterNetwork != null) {
			matterNetwork.removeFromNetwork(this);
		}
	}

	@Override
	public TypeMatterNetworkCable getConductorType() {
		if (cable == null) {
			cable = ((BlockMatterNetworkCable) getBlockState().getBlock()).type;
		}
		return cable;
	}

	@Override
	public MatterNetwork getNetwork(boolean createIfNull) {
		if (matterNetwork == null && createIfNull) {
			HashSet<INetworkCable> adjacentCables = getConnectedConductors();
			HashSet<MatterNetwork> connectedNets = new HashSet<>();
			for (INetworkCable wire : adjacentCables) {
				MatterNetwork network = wire.getNetwork(false);
				if (network != null) {
					connectedNets.add(network);
				}
			}
			if (connectedNets.isEmpty()) {
				matterNetwork = new MatterNetwork(Sets.newHashSet(this));
			} else {
				if (connectedNets.size() == 1) {
					matterNetwork = (MatterNetwork) connectedNets.toArray()[0];
				} else {
					matterNetwork = new MatterNetwork(connectedNets, false);
				}
				matterNetwork.conductorSet.add(this);
			}
		}
		return matterNetwork;
	}
	
	private HashSet<INetworkCable> getConnectedConductors() {
		HashSet<INetworkCable> set = new HashSet<>();
		for (Direction dir : Direction.values()) {
			BlockEntity facing = level.getBlockEntity(new BlockPos(worldPosition).relative(dir));
			if (facing instanceof INetworkCable cable) {
				set.add(cable);
			}
		}
		return set;
	}

	@Override
	public void refreshNetwork() {
		if (!level.isClientSide) {
			updateAdjacent();
			ArrayList<MatterNetwork> foundNetworks = new ArrayList<>();
			for (Direction dir : Direction.values()) {
				BlockEntity facing = level.getBlockEntity(new BlockPos(worldPosition).relative(dir));
				if (facing instanceof INetworkCable cable) {
					foundNetworks.add(cable.getNetwork());
				}
			}
			if (!foundNetworks.isEmpty()) {
				foundNetworks.get(0).conductorSet.add(this);
				matterNetwork = foundNetworks.get(0);
				if (foundNetworks.size() > 1) {
					foundNetworks.remove(0);
					for (MatterNetwork network : foundNetworks) {
						getNetwork().merge(network);
					}
				}
			}
			getNetwork().refresh();
		}
	}
	
	private boolean[] connections = new boolean[6];
	private BlockEntity[] tileConnections = new BlockEntity[6];

	public boolean updateAdjacent() {
		boolean flag = false;
		for (Direction dir : Direction.values()) {
			BlockEntity tile = level.getBlockEntity(worldPosition.relative(dir));
			boolean is = tile instanceof INetworkCable || tile instanceof IMatterNetworkMember;
			if (connections[dir.ordinal()] != is) {
				connections[dir.ordinal()] = is;
				tileConnections[dir.ordinal()] = tile;
				flag = true;
			}

		}
		return flag;
	}
	
	@Override
	public BlockEntity[] getAdjacentConnections() {
		return tileConnections;
	}

	@Override
	public void refreshNetworkIfChange() {
		if (updateAdjacent()) {
			refreshNetwork();
		}
	}

	@Override
	public void setNetwork(AbstractNetwork<?, ?, ?> network) {
		if (matterNetwork != network) {
			removeFromNetwork();
			matterNetwork = (MatterNetwork) network;
		}
	}
	
	@Override
	public void setRemoved() {
		if (!level.isClientSide && matterNetwork != null) {
			getNetwork().split(this);
		}
		super.setRemoved();
	}

	@Override
	public void onChunkUnloaded() {
		if (!level.isClientSide && matterNetwork != null) {
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
		compound.putInt("ord", getConductorType().ordinal());
		super.saveAdditional(compound);
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		cable = TypeMatterNetworkCable.values()[compound.getInt("ord")];
	}

}
