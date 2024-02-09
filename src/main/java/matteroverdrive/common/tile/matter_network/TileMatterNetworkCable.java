package matteroverdrive.common.tile.matter_network;

import matteroverdrive.common.block.type.TypeMatterNetworkCable;
import matteroverdrive.common.network.NetworkMatter;
import matteroverdrive.core.network.AbstractCableNetwork;
import matteroverdrive.core.network.utils.IMatterNetworkMember;
import matteroverdrive.core.tile.types.cable.AbstractCableTile;
import matteroverdrive.registry.TileRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.HashSet;

public class TileMatterNetworkCable extends AbstractCableTile<NetworkMatter> {
	public TileMatterNetworkCable(BlockPos pos, BlockState state) {
		super(TileRegistry.TILE_MATTER_NETWORK_CABLE.get(), pos, state);
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
				network = new NetworkMatter(Arrays.asList(this));
			} else {
				if (connectedNets.size() == 1) {
					network = (NetworkMatter) connectedNets.toArray()[0];
				} else {
					network = new NetworkMatter(connectedNets);
				}
				network.cables.add(this);
			}
		}
		return network;
	}

}
