package matteroverdrive.common.tile.matter_network;

import java.util.ArrayList;
import java.util.HashSet;

import com.google.common.collect.Sets;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.block.cable.BlockMatterNetworkCable;
import matteroverdrive.common.block.type.TypeMatterNetworkCable;
import matteroverdrive.common.cable_network.MatterNetwork;
import matteroverdrive.core.cable.AbstractNetwork;
import matteroverdrive.core.cable.types.matter_network.IMatterNetworkCable;
import matteroverdrive.core.cable.types.matter_network.IMatterNetworkMember;
import matteroverdrive.core.cable.types.matter_network.MatterNetworkEMPack;
import matteroverdrive.core.tile.GenericTile;
import matteroverdrive.core.utils.misc.Scheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TileMatterNetworkCable extends GenericTile implements IMatterNetworkCable {

	public MatterNetwork network;
	public TypeMatterNetworkCable cable = null;
	
	public TileMatterNetworkCable(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_MATTER_NETWORK_CABLE.get(), pos, state);
	}
	
	public MatterNetworkEMPack sendToNetwork(int fe, double matter, BlockEntity sender ,boolean debug) {
		ArrayList<BlockEntity> ignored = new ArrayList<>();
		ignored.add(sender);
		return getNetwork(true).emit(new MatterNetworkEMPack(fe, matter), ignored, debug);
	}

	@Override
	public void removeFromNetwork() {
		if (network != null) {
			network.removeFromNetwork(this);
		}
	}

	@Override
	public AbstractNetwork<?, ?, ?, ?> getAbstractNetwork() {
		return network;
	}

	@Override
	public TypeMatterNetworkCable getConductorType() {
		if (cable == null) {
			cable = ((BlockMatterNetworkCable) getBlockState().getBlock()).type;
		}
		return cable;
	}

	@Override
	public MatterNetwork getNetwork() {
		return getNetwork(true);
	}

	@Override
	public MatterNetwork getNetwork(boolean createIfNull) {
		if (network == null && createIfNull) {
			HashSet<IMatterNetworkCable> adjacentCables = getConnectedConductors();
			HashSet<MatterNetwork> connectedNets = new HashSet<>();
			for (IMatterNetworkCable wire : adjacentCables) {
				if (wire.getNetwork(false) != null && wire.getNetwork() instanceof MatterNetwork f) {
					connectedNets.add(f);
				}
			}
			if (connectedNets.isEmpty()) {
				network = new MatterNetwork(Sets.newHashSet(this));
			} else {
				if (connectedNets.size() == 1) {
					network = (MatterNetwork) connectedNets.toArray()[0];
				} else {
					network = new MatterNetwork(connectedNets, false);
				}
				network.conductorSet.add(this);
			}
		}
		return network;
	}
	
	private HashSet<IMatterNetworkCable> getConnectedConductors() {
		HashSet<IMatterNetworkCable> set = new HashSet<>();
		for (Direction dir : Direction.values()) {
			BlockEntity facing = level.getBlockEntity(new BlockPos(worldPosition).relative(dir));
			if (facing instanceof IMatterNetworkCable p) {
				set.add(p);
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
				if (facing instanceof IMatterNetworkCable p && p.getNetwork() instanceof MatterNetwork n) {
					foundNetworks.add(n);
				}
			}
			if (!foundNetworks.isEmpty()) {
				foundNetworks.get(0).conductorSet.add(this);
				network = foundNetworks.get(0);
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
			boolean is = tile instanceof IMatterNetworkCable || tile instanceof IMatterNetworkMember;
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
	public void setNetwork(AbstractNetwork<?, ?, ?, ?> aNetwork) {
		if (network != aNetwork && aNetwork instanceof MatterNetwork f) {
			removeFromNetwork();
			network = f;
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
		compound.putInt("ord", getConductorType().ordinal());
		super.saveAdditional(compound);
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		cable = TypeMatterNetworkCable.values()[compound.getInt("ord")];
	}

}
