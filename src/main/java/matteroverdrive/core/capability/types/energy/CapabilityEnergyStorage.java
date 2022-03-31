package matteroverdrive.core.capability.types.energy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import javax.annotation.Nonnull;

import matteroverdrive.core.capability.IOverdriveCapability;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.tile.GenericTile;
import matteroverdrive.core.utils.UtilsDirection;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class CapabilityEnergyStorage implements IEnergyStorage, IOverdriveCapability {

	private HashSet<Direction> relativeInputDirs;
	private HashSet<Direction> relativeOutputDirs;

	private boolean isSided = false;

	private GenericTile owner;
	private boolean hasTile;

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
		//will be overwritten by nbt load!
		this.maxStorage = maxStorage;
		this.hasInput = hasInput;
		this.hasOutput = hasOutput;
	}

	public CapabilityEnergyStorage setOwner(GenericTile tile) {
		owner = tile;
		hasTile = true;
		return this;
	}

	public CapabilityEnergyStorage setDefaultDirections(@Nonnull Direction[] inputs, @Nonnull Direction[] outputs) {
		isSided = true;
		boolean changed = false;
		if(relativeInputDirs == null) {
			relativeInputDirs = new HashSet<>();
			for(Direction dir : inputs) {
				relativeInputDirs.add(dir);
			}
			changed = true;
		}
		if(relativeOutputDirs == null) {
			relativeOutputDirs = new HashSet<>();
			for(Direction dir : outputs) {
				relativeOutputDirs.add(dir);
			}
			changed = true;
		}
		if(changed) {
			refreshCapability();
		}
		return this;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (matchesCapability(cap)) {
			if (isSided) {
				return side == null ? LazyOptional.empty() : sideCaps[side.ordinal()].cast();
			} else {
				return holder.cast();
			}
		}
		return LazyOptional.empty();
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		tag.putInt("stored", currStorage);

		ListTag inList = new ListTag();
		ListTag outList = new ListTag();
		int inDirSize = 0;
		int outDirSize = 0;

		if(isSided) {
			inDirSize = relativeInputDirs.size();
			int index = 0;
			Iterator<Direction> it = relativeInputDirs.iterator();
			while (it.hasNext()) {
				CompoundTag dirTag = new CompoundTag();
				dirTag.putString("inDir" + index , it.next().toString());
				inList.add(dirTag);
				index ++;
			}

			outDirSize = relativeOutputDirs.size();
			index = 0;
			it = relativeOutputDirs.iterator();
			while (it.hasNext()) {
				CompoundTag dirTag = new CompoundTag();
				dirTag.putString("outDir" + index , it.next().toString());
				outList.add(dirTag);
				index ++;
			}
		}

		tag.putInt("inDirSize", inDirSize);
		tag.put("inDirs", inList);
		tag.putInt("outDirSize", outDirSize);
		tag.put("outDirs", inList);

		tag.putInt("maxStorage", maxStorage);
		tag.putBoolean("hasInput", hasInput);
		tag.putBoolean("hasOutput", hasOutput);

		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		currStorage = nbt.getInt("stored");

		relativeInputDirs = new HashSet<>();
		ListTag inList = nbt.getList("inDirs", Tag.TAG_COMPOUND);
		for(int i = 0; i < nbt.getInt("inDirSize"); i++) {
			relativeInputDirs.add(Direction.byName(inList.getCompound(i).getString("inDir" + i)));
		}

		relativeOutputDirs = new HashSet<>();
		ListTag outList = nbt.getList("outDirs", Tag.TAG_COMPOUND);
		for(int i = 0; i < nbt.getInt("outDirSize"); i++) {
			relativeOutputDirs.add(Direction.byName(outList.getCompound(i).getString("outDir" + i)));
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
		if(canReceive()) {
			int room = maxStorage - currStorage;
			int accepted = room < maxReceive ? room : maxReceive;
			if(!simulate) {
				currStorage += accepted;
			}
			return accepted;
		}
		return 0;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		if(canExtract()) {
			int taken = currStorage < maxExtract ? currStorage : maxExtract;
			if(!simulate) {
				currStorage -= taken;
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
		if(holder != null) {
			holder.invalidate();
		}
		if(childInput != null) {
			childInput.invalidate();
		}
		if(childOutput != null) {
			childOutput.invalidate();
		}

	}

	@Override
	public void refreshCapability() {
		invalidateCapability();
		sideCaps = new LazyOptional[6];
		if (isSided) {
			Arrays.fill(sideCaps, LazyOptional.empty());
			if(relativeInputDirs.size() > 0) {
				setInputCaps();
			}
			if(relativeOutputDirs.size() > 0) {
				setOutputCaps();
			}
		} else {
			holder = LazyOptional.of(() -> this);
		}
	}

	private void setInputCaps() {
		childInput = LazyOptional.of(() -> new ChildCapabilityEnergyStorage(true, false, this));
		Direction facing = owner.getFacing();
		for(Direction dir : relativeInputDirs) {
			sideCaps[UtilsDirection.getRelativeSide(facing, dir).ordinal()] = childInput;
		}
	}

	private void setOutputCaps() {
		childOutput = LazyOptional.of(() -> new ChildCapabilityEnergyStorage(false, true, this));
		Direction facing = owner.getFacing();
		for(Direction dir : relativeOutputDirs) {
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
		return taken;
	}

	// method for us to allow energy addition on items/blocks that aren't
	// meant to recieve power
	public int giveEnergy(int amt) {
		int room = maxStorage - currStorage;
		int accepted = room < amt ? room : amt;
		currStorage += accepted;
		return accepted;
	}

	@Override
	public String getSaveKey() {
		return "energy";
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
			int returned = super.extractEnergy(maxExtract, simulate);
			if (canExtract() && !simulate) {
				parent.currStorage -= returned;
			}
			return returned;
		}

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate) {
			int returned = super.receiveEnergy(maxReceive, simulate);
			if (canReceive() && !simulate) {
				parent.currStorage += returned;
			}
			return returned;
		}

	}

}
