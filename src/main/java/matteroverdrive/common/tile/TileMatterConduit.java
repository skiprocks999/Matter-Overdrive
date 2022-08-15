package matteroverdrive.common.tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import matteroverdrive.common.block.type.TypeMatterConduit;
import matteroverdrive.common.network.NetworkMatterConduit;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.matter.ICapabilityMatterStorage;
import matteroverdrive.core.network.AbstractCableNetwork;
import matteroverdrive.core.tile.types.cable.AbstractCableTile;
import matteroverdrive.core.tile.types.cable.AbstractEmittingCable;
import matteroverdrive.core.utils.UtilsMatter;
import matteroverdrive.registry.TileRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class TileMatterConduit extends AbstractEmittingCable<NetworkMatterConduit> {

	private ArrayList<ICapabilityMatterStorage> handler = new ArrayList<>();

	public TileMatterConduit(BlockPos pos, BlockState state) {
		super(TileRegistry.TILE_MATTER_CONDUIT.get(), pos, state);
		for (Direction dir : Direction.values()) {
			handler.add(new ICapabilityMatterStorage() {

				@Override
				public double receiveMatter(double maxReceive, boolean simulate) {
					if (simulate || network == null) {
						return 0;
					}
					ArrayList<BlockEntity> ignored = new ArrayList<>();
					ignored.add(level.getBlockEntity(worldPosition.relative(dir)));
					return network.emitToConnected(maxReceive, ignored, false);
				}

				@Override
				public double extractMatter(double maxExtract, boolean simulate) {
					if (network != null) {
						return network.extractFromConnected(maxExtract, simulate);
					}
					return 0;
				}

				@Override
				public double getMatterStored() {
					if (network != null) {
						return network.getCurrentMemberStorage();
					}
					return 0;
				}

				@Override
				public double getMaxMatterStored() {
					if (network != null) {
						return network.getTotalMemberStorage();
					}
					return 0;
				}

				@Override
				public boolean canExtract() {
					return true;
				}

				@Override
				public boolean canReceive() {
					return true;
				}

			});
		}
	}

	@Override
	@Nonnull
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
		if (capability == MatterOverdriveCapabilities.MATTER_STORAGE) {
			return LazyOptional.of(() -> handler.get((facing == null ? Direction.UP : facing).ordinal())).cast();
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		cableType = TypeMatterConduit.values()[compound.getInt("ord")];
	}

	@Override
	public double getMaxTransfer() {
		return ((TypeMatterConduit) getConductorType()).capacity;
	}

	@Override
	public boolean isCable(BlockEntity entity) {
		return entity instanceof TileMatterConduit;
	}

	@Override
	public boolean isValidConnection(BlockEntity entity, Direction dir) {
		return UtilsMatter.isMatterReceiver(entity, dir.getOpposite());
	}

	@Override
	public AbstractCableNetwork getNetwork(boolean createIfNull) {
		if (network == null && createIfNull) {
			HashSet<AbstractCableTile<NetworkMatterConduit>> adjacentCables = getConnectedConductors();
			HashSet<NetworkMatterConduit> connectedNets = new HashSet<>();
			for (AbstractCableTile<NetworkMatterConduit> wire : adjacentCables) {
				NetworkMatterConduit network = (NetworkMatterConduit) wire.getNetwork(false);
				if (network != null) {
					connectedNets.add(network);
				}
			}
			if (connectedNets.isEmpty()) {
				network = new NetworkMatterConduit(Arrays.asList(this));
			} else {
				if (connectedNets.size() == 1) {
					network = (NetworkMatterConduit) connectedNets.toArray()[0];
				} else {
					network = new NetworkMatterConduit(connectedNets);
				}
				network.cables.add(this);
			}
		}
		return network;
	}

}
