package matteroverdrive.common.tile;

import java.util.ArrayList;
import java.util.HashSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Sets;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.block.cable.BlockMatterConduit;
import matteroverdrive.common.block.type.TypeMatterConduit;
import matteroverdrive.common.cable_network.MatterConduitNetwork;
import matteroverdrive.core.cable.AbstractNetwork;
import matteroverdrive.core.cable.types.matter_pipe.IMatterConduit;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.matter.ICapabilityMatterStorage;
import matteroverdrive.core.tile.GenericTile;
import matteroverdrive.core.utils.UtilsMatter;
import matteroverdrive.core.utils.misc.Scheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class TileMatterConduit extends GenericTile implements IMatterConduit {

	public MatterConduitNetwork conduitNetwork;
	private ArrayList<ICapabilityMatterStorage> handler = new ArrayList<>();
	public TypeMatterConduit pipe = null;

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
					return conduitNetwork.emit(maxReceive, ignored, false);
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
	public AbstractNetwork<?, ?, ?, ?> getAbstractNetwork() {
		return conduitNetwork;
	}

	private HashSet<IMatterConduit> getConnectedConductors() {
		HashSet<IMatterConduit> set = new HashSet<>();
		for (Direction dir : Direction.values()) {
			BlockEntity facing = level.getBlockEntity(new BlockPos(worldPosition).relative(dir));
			if (facing instanceof IMatterConduit p) {
				set.add(p);
			}
		}
		return set;
	}

	@Override
	public MatterConduitNetwork getNetwork() {
		return getNetwork(true);
	}

	@Override
	public MatterConduitNetwork getNetwork(boolean createIfNull) {
		if (conduitNetwork == null && createIfNull) {
			HashSet<IMatterConduit> adjacentCables = getConnectedConductors();
			HashSet<MatterConduitNetwork> connectedNets = new HashSet<>();
			for (IMatterConduit wire : adjacentCables) {
				if (wire.getNetwork(false) != null && wire.getNetwork() instanceof MatterConduitNetwork f) {
					connectedNets.add(f);
				}
			}
			if (connectedNets.isEmpty()) {
				conduitNetwork = new MatterConduitNetwork(Sets.newHashSet(this));
			} else {
				if (connectedNets.size() == 1) {
					conduitNetwork = (MatterConduitNetwork) connectedNets.toArray()[0];
				} else {
					conduitNetwork = new MatterConduitNetwork(connectedNets, false);
				}
				conduitNetwork.conductorSet.add(this);
			}
		}
		return conduitNetwork;
	}

	@Override
	public void setNetwork(AbstractNetwork<?, ?, ?, ?> network) {
		if (conduitNetwork != network && network instanceof MatterConduitNetwork f) {
			removeFromNetwork();
			conduitNetwork = f;
		}
	}

	@Override
	public void refreshNetwork() {
		if (!level.isClientSide) {
			updateAdjacent();
			ArrayList<MatterConduitNetwork> foundNetworks = new ArrayList<>();
			for (Direction dir : Direction.values()) {
				BlockEntity facing = level.getBlockEntity(new BlockPos(worldPosition).relative(dir));
				if (facing instanceof IMatterConduit p && p.getNetwork() instanceof MatterConduitNetwork n) {
					foundNetworks.add(n);
				}
			}
			if (!foundNetworks.isEmpty()) {
				foundNetworks.get(0).conductorSet.add(this);
				conduitNetwork = foundNetworks.get(0);
				if (foundNetworks.size() > 1) {
					foundNetworks.remove(0);
					for (MatterConduitNetwork network : foundNetworks) {
						getNetwork().merge(network);
					}
				}
			}
			getNetwork().refresh();
		}
	}

	@Override
	public void removeFromNetwork() {
		if (conduitNetwork != null) {
			conduitNetwork.removeFromNetwork(this);
		}
	}

	private boolean[] connections = new boolean[6];
	private BlockEntity[] tileConnections = new BlockEntity[6];

	public boolean updateAdjacent() {
		boolean flag = false;
		for (Direction dir : Direction.values()) {
			BlockEntity tile = level.getBlockEntity(worldPosition.relative(dir));
			boolean is = UtilsMatter.isMatterReceiver(tile, dir.getOpposite());
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
	public void setRemoved() {
		if (!level.isClientSide && conduitNetwork != null) {
			getNetwork().split(this);
		}
		super.setRemoved();
	}

	@Override
	public void onChunkUnloaded() {
		if (!level.isClientSide && conduitNetwork != null) {
			getNetwork().split(this);
		}
	}

	@Override
	public void onLoad() {
		super.onLoad();
		Scheduler.schedule(1, this::refreshNetwork);
	}

	@Override
	public TypeMatterConduit getMatterConduitType() {
		if (pipe == null) {
			pipe = ((BlockMatterConduit) getBlockState().getBlock()).type;
		}
		return pipe;
	}

	@Override
	public void saveAdditional(CompoundTag compound) {
		compound.putInt("ord", getMatterConduitType().ordinal());
		super.saveAdditional(compound);
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		pipe = TypeMatterConduit.values()[compound.getInt("ord")];
	}

}
