package matteroverdrive.common.tile;

import java.util.ArrayList;
import java.util.HashSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Sets;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.block.type.TypeMatterConduit;
import matteroverdrive.common.cable_network.MatterConduitNetwork;
import matteroverdrive.common.tile.cable.AbstractCableTile;
import matteroverdrive.common.tile.cable.AbstractEmittingCable;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.matter.ICapabilityMatterStorage;
import matteroverdrive.core.network.BaseNetwork;
import matteroverdrive.core.utils.UtilsMatter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class TileMatterConduit extends AbstractEmittingCable<MatterConduitNetwork> {

	private ArrayList<ICapabilityMatterStorage> handler = new ArrayList<>();

	public TileMatterConduit(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_MATTER_CONDUIT.get(), pos, state);
		for (Direction dir : Direction.values()) {
			handler.add(new ICapabilityMatterStorage() {

				@Override
				public double receiveMatter(double maxReceive, boolean simulate) {
					if (simulate || getNetwork() == null) {
						return 0;
					}
					ArrayList<BlockEntity> ignored = new ArrayList<>();
					ignored.add(level.getBlockEntity(new BlockPos(worldPosition).relative(dir)));
					return network.emit(maxReceive, ignored, false);
				}

				@Override
				public double extractMatter(double maxExtract, boolean simulate) {
					return 0;
				}

				@Override
				public double getMatterStored() {
					return 0;
				}

				@Override
				public double getMaxMatterStored() {
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
	public BaseNetwork getNetwork(boolean createIfNull) {
		if (network == null && createIfNull) {
			HashSet<AbstractCableTile<MatterConduitNetwork>> adjacentCables = getConnectedConductors();
			HashSet<MatterConduitNetwork> connectedNets = new HashSet<>();
			for (AbstractCableTile<MatterConduitNetwork> wire : adjacentCables) {
				MatterConduitNetwork network = (MatterConduitNetwork) wire.getNetwork(false);
				if (network != null) {
					connectedNets.add(network);
				}
			}
			if (connectedNets.isEmpty()) {
				network = new MatterConduitNetwork(Sets.newHashSet(this));
			} else {
				if (connectedNets.size() == 1) {
					network = (MatterConduitNetwork) connectedNets.toArray()[0];
				} else {
					network = new MatterConduitNetwork(connectedNets, false);
				}
				network.cables.add(this);
			}
		}
		return network;
	}

}
