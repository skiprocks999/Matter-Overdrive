package matteroverdrive.common.tile.matter_network;

import java.util.HashSet;

import com.google.common.collect.Sets;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.block.type.TypeMatterNetworkCable;
import matteroverdrive.common.cable_network.MatterNetwork;
import matteroverdrive.common.tile.cable.AbstractCableTile;
import matteroverdrive.core.network.BaseNetwork;
import matteroverdrive.core.network.utils.IMatterNetworkMember;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TileMatterNetworkCable extends AbstractCableTile<MatterNetwork> {
	
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
	public BaseNetwork getNetwork(boolean createIfNull) {
		if (network == null && createIfNull) {
			HashSet<AbstractCableTile<MatterNetwork>> adjacentCables = getConnectedConductors();
			HashSet<MatterNetwork> connectedNets = new HashSet<>();
			for (AbstractCableTile<MatterNetwork> wire : adjacentCables) {
				MatterNetwork network = (MatterNetwork) wire.getNetwork(false);
				if (network != null) {
					connectedNets.add(network);
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

}
