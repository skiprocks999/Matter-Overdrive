package matteroverdrive.core.capability.types.energy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import matteroverdrive.core.block.old.GenericMachineBlock;
import matteroverdrive.core.capability.IOverdriveCapability;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.tile.types.old.GenericTile;
import matteroverdrive.core.utils.UtilsDirection;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class CapabilityEnergyStorage implements IEnergyStorage, IOverdriveCapability {

	private HashSet<Direction> relativeInputDirs;
	private HashSet<Direction> relativeOutputDirs;

	private boolean isSided = false;

	private GenericTile owner;
	private boolean hasOwner;
	private Direction initialFacing = null;

	private boolean hasInput = false;
	private boolean hasOutput = false;

	private int maxStorage = 0;
	private int currStorage = 0;

	private LazyOptional<IEnergyStorage> holder = LazyOptional.of(() -> this);

	private LazyOptional<IEnergyStorage> childInput;
	private LazyOptional<IEnergyStorage> childOutput;
	// Down Up North South West East
	private LazyOptional<IEnergyStorage>[] sideCaps = new LazyOptional[6];

	public CapabilityEnergyStorage(int maxStorage, boolean hasInput, boolean hasOutput) {
		// will be overwritten by nbt load!
		this.maxStorage = maxStorage;
		this.hasInput = hasInput;
		this.hasOutput = hasOutput;
	}

	public CapabilityEnergyStorage setOwner(GenericTile tile) {
		owner = tile;
		hasOwner = true;
		return this;
	}

	public CapabilityEnergyStorage setDefaultDirections(@Nonnull BlockState initialState, @Nullable Direction[] inputs,
			@Nullable Direction[] outputs) {
		isSided = true;
		boolean changed = false;
		if (relativeInputDirs == null && inputs != null) {
			relativeInputDirs = new HashSet<>();
			for (Direction dir : inputs) {
				relativeInputDirs.add(dir);
			}
			changed = true;
		}
		if (relativeOutputDirs == null && outputs != null) {
			relativeOutputDirs = new HashSet<>();
			for (Direction dir : outputs) {
				relativeOutputDirs.add(dir);
			}
			changed = true;
		}
		if (changed) {
			initialFacing = initialState.getValue(GenericMachineBlock.FACING);
			refreshCapability();
		}
		return this;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (matchesCapability(cap)) {
			if (isSided) {
				return side == null ? LazyOptional.empty() : sideCaps[side.ordinal()].cast();
			}
			return holder.cast();
		}
		return LazyOptional.empty();
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		tag.putInt("stored", currStorage);

		if (isSided) {
			int inDirSize = relativeInputDirs == null ? 0 : relativeInputDirs.size();
			if (inDirSize > 0) {
				tag.putInt("inDirSize", inDirSize);
				List<Direction> inDirs = new ArrayList<>(relativeInputDirs);
				inDirSize = inDirs.size();
				for (int i = 0; i < inDirSize; i++) {
					tag.putString("inDir" + i, inDirs.get(i).getName());
				}
			}

			int outDirSize = relativeOutputDirs == null ? 0 : relativeOutputDirs.size();
			if (outDirSize > 0) {
				tag.putInt("outDirSize", outDirSize);
				List<Direction> outDirs = new ArrayList<>(relativeOutputDirs);
				outDirSize = outDirs.size();
				for (int i = 0; i < outDirSize; i++) {
					tag.putString("outDir" + i, outDirs.get(i).getName());
				}
			}
		}

		tag.putInt("maxStorage", maxStorage);
		tag.putBoolean("hasInput", hasInput);
		tag.putBoolean("hasOutput", hasOutput);

		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		currStorage = nbt.getInt("stored");

		if (nbt.contains("inDirSize")) {
			relativeInputDirs = new HashSet<>();
			int inDirSize = nbt.getInt("inDirSize");
			for (int i = 0; i < inDirSize; i++) {
				relativeInputDirs.add(Direction.valueOf(nbt.getString("inDir" + i).toUpperCase()));
			}

		}
		if (nbt.contains("outDirSize")) {
			relativeOutputDirs = new HashSet<>();
			int outDirSize = nbt.getInt("outDirSize");
			for (int i = 0; i < outDirSize; i++) {
				relativeOutputDirs.add(Direction.valueOf(nbt.getString("outDir" + i).toUpperCase()));
			}
		}

		maxStorage = nbt.getInt("maxStorage");
		hasInput = nbt.getBoolean("hasInput");
		hasOutput = nbt.getBoolean("hasOutput");
	}

	@Override
	public <T> boolean matchesCapability(Capability<T> capability) {
		return capability == CapabilityEnergy.ENERGY;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		if (canReceive()) {
			int room = maxStorage - currStorage;
			int accepted = room <= maxReceive ? room : maxReceive;
			if (!simulate) {
				currStorage += accepted;
				onChange();
			}
			return accepted;
		}
		return 0;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		if (canExtract()) {
			int taken = currStorage <= maxExtract ? currStorage : maxExtract;
			if (!simulate) {
				currStorage -= taken;
				onChange();
			}
			return taken;
		}
		return 0;
	}

	@Override
	public int getEnergyStored() {
		return currStorage;
	}

	@Override
	public int getMaxEnergyStored() {
		return maxStorage;
	}

	@Override
	public boolean canExtract() {
		return hasOutput;
	}

	@Override
	public boolean canReceive() {
		return hasInput;
	}

	@Override
	public void onLoad(BlockEntity tile) {
		refreshCapability();
	}

	@Override
	public CapabilityType getCapabilityType() {
		return CapabilityType.Energy;
	}

	@Override
	public void invalidateCapability() {
		if (holder != null) {
			holder.invalidate();
		}
		if (childInput != null) {
			childInput.invalidate();
		}
		if (childOutput != null) {
			childOutput.invalidate();
		}

	}

	@Override
	public void refreshCapability() {
		invalidateCapability();
		sideCaps = new LazyOptional[6];
		if (isSided) {
			Arrays.fill(sideCaps, LazyOptional.empty());
			if (relativeInputDirs != null) {
				setInputCaps();
			}
			if (relativeOutputDirs != null) {
				setOutputCaps();
			}
			initialFacing = null;
		} else {
			holder = LazyOptional.of(() -> this);
		}
	}

	private void setInputCaps() {
		childInput = LazyOptional.of(() -> new ChildCapabilityEnergyStorage(true, false, this));
		Direction facing;
		if (initialFacing == null) {
			facing = owner.getFacing();
		} else {
			facing = initialFacing;
		}
		for (Direction dir : relativeInputDirs) {
			sideCaps[UtilsDirection.getRelativeSide(facing, dir).ordinal()] = childInput;
		}
	}

	private void setOutputCaps() {
		childOutput = LazyOptional.of(() -> new ChildCapabilityEnergyStorage(false, true, this));
		Direction facing;
		if (initialFacing == null) {
			facing = owner.getFacing();
		} else {
			facing = initialFacing;
		}
		for (Direction dir : relativeOutputDirs) {
			sideCaps[UtilsDirection.getRelativeSide(facing, dir).ordinal()] = childOutput;
		}
	}

	public void updateMaxEnergyStorage(int maxStorage) {
		this.maxStorage = maxStorage;
	}

	public void updateInput(boolean input) {
		this.hasInput = input;
	}

	public void updateOutput(boolean output) {
		this.hasOutput = output;
	}

	// method for us to allow for energy removal on items/blocks that aren't
	// meant to provide energy
	public int removeEnergy(int amt) {
		int taken = currStorage < amt ? currStorage : amt;
		currStorage -= taken;
		onChange();
		return taken;
	}

	// method for us to allow energy addition on items/blocks that aren't
	// meant to recieve power
	public int giveEnergy(int amt) {
		int room = maxStorage - currStorage;
		int accepted = room < amt ? room : amt;
		currStorage += accepted;
		onChange();
		return accepted;
	}

	@Override
	public String getSaveKey() {
		return "energy";
	}

	private void onChange() {
		if (hasOwner) {
			owner.setChanged();
		}
	}

	public boolean isSided() {
		return isSided;
	}

	public HashSet<Direction> getInputDirections() {
		return relativeInputDirs;
	}

	public HashSet<Direction> getOutputDirections() {
		return relativeOutputDirs;
	}

	public void setInputDirs(@Nonnull List<Direction> dirs) {
		relativeInputDirs = new HashSet<>();
		for (Direction dir : dirs) {
			relativeInputDirs.add(dir);
		}
	}

	public void setOutputDirs(@Nonnull List<Direction> dirs) {
		relativeOutputDirs = new HashSet<>();
		for (Direction dir : dirs) {
			relativeOutputDirs.add(dir);
		}
	}

	private class ChildCapabilityEnergyStorage extends CapabilityEnergyStorage {

		private CapabilityEnergyStorage parent;

		public ChildCapabilityEnergyStorage(boolean isInput, boolean isOutput, CapabilityEnergyStorage parent) {
			super(parent.maxStorage, isInput, isOutput);
			this.parent = parent;
			currStorage = parent.currStorage;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			if (canExtract()) {
				return parent.extractEnergy(maxExtract, simulate);
			}
			return 0;
		}

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate) {
			if (canReceive()) {
				return parent.receiveEnergy(maxReceive, simulate);
			}
			return 0;
		}

	}

}
