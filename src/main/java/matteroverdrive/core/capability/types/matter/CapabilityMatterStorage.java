package matteroverdrive.core.capability.types.matter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import matteroverdrive.core.block.GenericEntityBlock;
import matteroverdrive.core.capability.IOverdriveCapability;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.property.Property;
import matteroverdrive.core.tile.GenericTile;
import matteroverdrive.core.utils.UtilsDirection;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class CapabilityMatterStorage implements IOverdriveCapability, ICapabilityMatterStorage {

	private HashSet<Direction> relativeInputDirs;
	private HashSet<Direction> relativeOutputDirs;

	private boolean isSided = false;

	private GenericTile owner;
	private boolean hasOwner;
	private Direction initialFacing = null;

	private boolean hasInput = false;
	private boolean hasOutput = false;

	private double maxStorage = 0;
	private double currStorage = 0;

	private LazyOptional<ICapabilityMatterStorage> holder = LazyOptional.of(() -> this);

	private LazyOptional<ICapabilityMatterStorage> childInput;
	private LazyOptional<ICapabilityMatterStorage> childOutput;
	// Down Up North South West East
	private LazyOptional<ICapabilityMatterStorage>[] sideCaps = new LazyOptional[6];

	private Property<CompoundTag> propertyHandler = null;

	public CapabilityMatterStorage(double maxStorage, boolean hasInput, boolean hasOutput) {
		// will be overwritten by nbt load!
		this.maxStorage = maxStorage;
		this.hasInput = hasInput;
		this.hasOutput = hasOutput;
	}

	public CapabilityMatterStorage setOwner(GenericTile tile) {
		owner = tile;
		hasOwner = true;
		return this;
	}

	public CapabilityMatterStorage setDefaultDirections(@Nonnull BlockState initialState, @Nullable Direction[] inputs,
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
			initialFacing = initialState.getValue(GenericEntityBlock.FACING);
			refreshCapability();
		}
		return this;
	}

	public CapabilityMatterStorage setPropertyManager(Property<CompoundTag> property) {
		propertyHandler = property;
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
		tag.putDouble("stored", currStorage);

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

		tag.putDouble("maxStorage", maxStorage);
		tag.putBoolean("hasInput", hasInput);
		tag.putBoolean("hasOutput", hasOutput);
		tag.putBoolean("hasOwner", hasOwner);

		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		currStorage = nbt.getDouble("stored");

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

		maxStorage = nbt.getDouble("maxStorage");
		hasInput = nbt.getBoolean("hasInput");
		hasOutput = nbt.getBoolean("hasOutput");
		hasOwner = nbt.getBoolean("hasOwner");
	}

	@Override
	public double receiveMatter(double maxReceive, boolean simulate) {
		if (canReceive()) {
			double room = maxStorage - currStorage;
			double accepted = room <= maxReceive ? room : maxReceive;
			if (!simulate) {
				currStorage += accepted;
				onChange();
			}
			return accepted;
		}
		return 0;
	}

	@Override
	public double extractMatter(double maxExtract, boolean simulate) {
		if (canExtract()) {
			double taken = currStorage <= maxExtract ? currStorage : maxExtract;
			if (!simulate) {
				currStorage -= taken;
				onChange();
			}
			return taken;
		}
		return 0;
	}

	@Override
	public double getMatterStored() {
		return currStorage;
	}

	@Override
	public double getMaxMatterStored() {
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
		if(hasOwner && tile instanceof GenericTile generic) {
			owner = generic;
		}
		refreshCapability();
	}

	@Override
	public <T> boolean matchesCapability(Capability<T> cap) {
		return cap == MatterOverdriveCapabilities.MATTER_STORAGE;
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

	@Override
	public String getSaveKey() {
		return "matter";
	}

	public void updateMaxMatterStorage(double maxStorage) {
		this.maxStorage = maxStorage;
		onChange();
	}

	public void updateInput(boolean input) {
		this.hasInput = input;
		onChange();
	}

	public void updateOutput(boolean output) {
		this.hasOutput = output;
		onChange();
	}

	// method for us to allow for matter removal on items/blocks that aren't
	// meant to provide matter
	public double removeMatter(double amt) {
		double taken = currStorage <= amt ? currStorage : amt;
		currStorage -= taken;
		onChange();
		return taken;
	}

	// method for us to allow matter addition on items/blocks that aren't
	// meant to receive matter
	public double giveMatter(double amt) {
		double room = maxStorage - currStorage;
		double accepted = room <= amt ? room : amt;
		currStorage += accepted;
		onChange();
		return accepted;
	}

	private void setInputCaps() {
		childInput = LazyOptional.of(() -> new ChildCapabilityMatterStorage(true, false, this));
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
		childOutput = LazyOptional.of(() -> new ChildCapabilityMatterStorage(false, true, this));
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

	private void onChange() {
		if(hasOwner && !owner.getLevel().isClientSide()) {
			owner.onMatterStorageChange(this);
			if (propertyHandler != null) {
				propertyHandler.set(serializeNBT());
			}
		}
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
		onChange();
	}

	public void setOutputDirs(@Nonnull List<Direction> dirs) {
		relativeOutputDirs = new HashSet<>();
		for (Direction dir : dirs) {
			relativeOutputDirs.add(dir);
		}
		onChange();
	}

	public boolean isSided() {
		return isSided;
	}

	private class ChildCapabilityMatterStorage extends CapabilityMatterStorage {

		private CapabilityMatterStorage parent;

		public ChildCapabilityMatterStorage(boolean isInput, boolean isOutput, CapabilityMatterStorage parent) {
			super(parent.maxStorage, isInput, isOutput);
			this.parent = parent;
			currStorage = parent.currStorage;
		}

		@Override
		public double extractMatter(double maxExtract, boolean simulate) {
			if (canExtract()) {
				return parent.extractMatter(maxExtract, simulate);
			}
			return 0;
		}

		@Override
		public double receiveMatter(double maxReceive, boolean simulate) {
			if (canReceive()) {
				return parent.receiveMatter(maxReceive, simulate);
			}
			return 0;
		}

	}

}
