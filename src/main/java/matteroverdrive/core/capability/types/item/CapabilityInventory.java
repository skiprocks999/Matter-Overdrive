package matteroverdrive.core.capability.types.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import matteroverdrive.common.item.ItemUpgrade.UpgradeType;
import matteroverdrive.core.block.GenericEntityBlock;
import matteroverdrive.core.capability.IOverdriveCapability;
import matteroverdrive.core.property.Property;
import matteroverdrive.core.tile.GenericTile;
import matteroverdrive.core.utils.UtilsDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.TriPredicate;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class CapabilityInventory extends ItemStackHandler implements IOverdriveCapability {

	public static final CapabilityInventory EMPTY = new CapabilityInventory(0, false, false);

	private TriPredicate<Integer, ItemStack, CapabilityInventory> valid = (slot, item, inv) -> true;

	private HashSet<Direction> relativeInputDirs;
	private HashSet<Direction> relativeOutputDirs;

	private boolean isSided = false;
	public boolean hasInput = false;
	public boolean hasOutput = false;

	private int inputs = 0;
	private int outputs = 0;
	private int byproducts = 0;
	private int energyInputSlot = 0;
	private int matterInputSlot = 0;
	private int energyOutputSlot = 0;
	private int matterOutputSlot = 0;
	// not included in child cap
	private int upgrades = 0;

	@Nullable
	private GenericTile owner;
	private boolean hasOwner = false;
	private Direction initialFacing = null;

	private LazyOptional<IItemHandlerModifiable> holder = LazyOptional.of(() -> this);

	private LazyOptional<IItemHandlerModifiable> childInput;
	private LazyOptional<IItemHandlerModifiable> childOutput;
	// Down Up North South West East
	private LazyOptional<IItemHandlerModifiable>[] sideCaps = new LazyOptional[6];

	private UpgradeType[] validUpgrades;

	private Property<CompoundTag> propertyHandler = null;

	public CapabilityInventory() {
		super();
	}

	public CapabilityInventory(int size, boolean hasInput, boolean hasOutput) {
		super(size);
		this.hasInput = hasInput;
		this.hasOutput = hasOutput;
	}

	public CapabilityInventory setOwner(GenericTile tile) {
		owner = tile;
		hasOwner = true;
		return this;
	}

	public CapabilityInventory setDefaultDirections(@Nonnull BlockState initialState, @Nullable Direction[] inputs,
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

	public CapabilityInventory setInputs(int count) {
		inputs = count;
		return this;
	}

	public CapabilityInventory setOutputs(int count) {
		outputs = count;
		return this;
	}

	public CapabilityInventory setByproducts(int count) {
		byproducts = count;
		return this;
	}

	public CapabilityInventory setEnergyInputSlots(int count) {
		energyInputSlot = count;
		return this;
	}

	public CapabilityInventory setMatterInputSlots(int count) {
		matterInputSlot = count;
		return this;
	}
	
	public CapabilityInventory setEnergyOutputSlots(int count) {
		energyOutputSlot = count;
		return this;
	}

	public CapabilityInventory setMatterOutputSlots(int count) {
		matterOutputSlot = count;
		return this;
	}

	public CapabilityInventory setUpgrades(int count) {
		upgrades = count;
		return this;
	}

	public CapabilityInventory setValidUpgrades(UpgradeType[] upgrades) {
		this.validUpgrades = upgrades;
		return this;
	}

	public CapabilityInventory setValidator(TriPredicate<Integer, ItemStack, CapabilityInventory> valid) {
		this.valid = valid;
		return this;
	}

	public CapabilityInventory setPropertyManager(Property<CompoundTag> property) {
		propertyHandler = property;
		return this;
	}

	public int inputs() {
		return inputs;
	}

	public int outputs() {
		return outputs;
	}

	public int byproducts() {
		return byproducts;
	}

	public int energyInputSlots() {
		return energyInputSlot;
	}

	public int matterInputSlots() {
		return matterInputSlot;
	}
	
	public int energyOutputSlots() {
		return energyOutputSlot;
	}

	public int matterOutputSlots() {
		return matterOutputSlot;
	}

	public int upgrades() {
		return upgrades;
	}

	public int externalCount() {
		return inputs() + outputs() + byproducts() + energyInputSlots() + matterInputSlots() + energyOutputSlots() + matterOutputSlots();
	}

	public int inputIndex() {
		return 0;
	}

	public int outputIndex() {
		return inputIndex() + inputs;
	}

	public int byproductIndex() {
		return outputIndex() + outputs;
	}

	public int energyInputSlotsIndex() {
		return byproductIndex() + byproducts;
	}

	public int matterInputSlotsIndex() {
		return energyInputSlotsIndex() + energyInputSlot;
	}
	
	public int energyOutputSlotsIndex() {
		return matterInputSlotsIndex() + matterInputSlot;
	}

	public int matterOutputSlotsIndex() {
		return energyOutputSlotsIndex() + energyOutputSlot;
	}

	public int upgradeIndex() {
		return matterOutputSlotsIndex() + matterOutputSlot;
	}

	@Override
	public int getSlotLimit(int slot) {
		if (slot >= upgradeIndex()) {
			return 1;
		} else {
			return super.getSlotLimit(slot);
		}
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
	public <T> boolean matchesCapability(Capability<T> cap) {
		return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
	}

	@Override
	public void onLoad(BlockEntity tile) {
		refreshCapability();
	}

	@Override
	public String getSaveKey() {
		return "inventory";
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = super.serializeNBT();

		if (isSided) {
			int inDirSize = relativeInputDirs == null ? -1 : relativeInputDirs.size();
			if (inDirSize >= 0) {
				tag.putInt("inDirSize", inDirSize);
				List<Direction> inDirs = new ArrayList<>(relativeInputDirs);
				inDirSize = inDirs.size();
				for (int i = 0; i < inDirSize; i++) {
					tag.putString("inDir" + i, inDirs.get(i).getName());
				}
			}

			int outDirSize = relativeOutputDirs == null ? -1 : relativeOutputDirs.size();
			if (outDirSize >= 0) {
				tag.putInt("outDirSize", outDirSize);
				List<Direction> outDirs = new ArrayList<>(relativeOutputDirs);
				outDirSize = outDirs.size();
				for (int i = 0; i < outDirSize; i++) {
					tag.putString("outDir" + i, outDirs.get(i).getName());
				}
			}
		}
		tag.putBoolean("hasInput", hasInput);
		tag.putBoolean("hasOutput", hasOutput);

		int[] vals = new int[] { inputs, outputs, byproducts, energyInputSlot, matterInputSlot, energyOutputSlot, matterOutputSlot, upgrades };

		tag.putIntArray("sizes", vals);

		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		super.deserializeNBT(nbt);

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
		hasInput = nbt.getBoolean("hasInput");
		hasOutput = nbt.getBoolean("hasOutput");
		int[] vals = nbt.getIntArray("sizes");
		inputs = vals[0];
		outputs = vals[1];
		byproducts = vals[2];
		energyInputSlot = vals[3];
		matterInputSlot = vals[4];
		energyOutputSlot = vals[5];
		matterOutputSlot = vals[6];
		upgrades = vals[7];
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
			if (relativeInputDirs.size() > 0) {
				setInputCaps();
			}
			if (relativeOutputDirs.size() > 0) {
				setOutputCaps();
			}
			initialFacing = null;
		} else {
			holder = LazyOptional.of(() -> {
				int[] slots = new int[externalCount()];
				for (int i = 0; i < slots.length; i++) {
					slots[i] = i;
				}
				return new ChildInventoryHandler(this, slots);
			});
		}
	}

	private void setInputCaps() {
		childInput = LazyOptional.of(() -> {
			int[] slots = new int[inputs() + energyInputSlots() + matterInputSlots()];
			int index = 0;
			for (int i = 0; i < inputs(); i++) {
				slots[index] = inputIndex() + i;
				index++;
			}
			for (int i = 0; i < energyInputSlots(); i++) {
				slots[index] = energyInputSlotsIndex() + i;
				index++;
			}
			for (int i = 0; i < matterInputSlots(); i++) {
				slots[index] = matterInputSlotsIndex() + i;
				index++;
			}
			return new ChildInventoryHandler(this, slots);
		});
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
		childOutput = LazyOptional.of(() -> {
			int[] slots = new int[outputs() + byproducts() + energyOutputSlots() + matterOutputSlots()];
			int index = 0;
			for (int i = 0; i < outputs(); i++) {
				slots[index] = outputIndex() + i;
				index++;
			}
			for (int i = 0; i < byproducts(); i++) {
				slots[index] = byproductIndex() + i;
				index++;
			}
			for (int i = 0; i < energyOutputSlots(); i++) {
				slots[index] = energyOutputSlotsIndex() + i;
				index++;
			}
			for (int i = 0; i < matterOutputSlots(); i++) {
				slots[index] = matterOutputSlotsIndex() + i;
				index++;
			}
			return new ChildInventoryHandler(this, slots);
		});
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

	public List<ItemStack> getInputs() {
		List<ItemStack> inputs = new ArrayList<>();
		for (int i = 0; i < inputs(); i++) {
			inputs.add(getStackInSlot(inputIndex() + i));
		}
		return inputs;
	}

	public List<ItemStack> getOutputs() {
		List<ItemStack> outputs = new ArrayList<>();
		for (int i = 0; i < outputs(); i++) {
			outputs.add(getStackInSlot(outputIndex() + i));
		}
		return outputs;
	}

	public List<ItemStack> getByproducts() {
		List<ItemStack> byprouducts = new ArrayList<>();
		for (int i = 0; i < byproducts(); i++) {
			byprouducts.add(getStackInSlot(byproductIndex() + i));
		}
		return byprouducts;
	}

	public List<ItemStack> getEnergyInputItems() {
		List<ItemStack> energy = new ArrayList<>();
		for (int i = 0; i < energyInputSlots(); i++) {
			energy.add(getStackInSlot(energyInputSlotsIndex() + i));
		}
		return energy;
	}

	public List<ItemStack> getMatterInputItems() {
		List<ItemStack> matter = new ArrayList<>();
		for (int i = 0; i < matterInputSlots(); i++) {
			matter.add(getStackInSlot(matterInputSlotsIndex() + i));
		}
		return matter;
	}
	
	public List<ItemStack> getEnergyOutputItems() {
		List<ItemStack> energy = new ArrayList<>();
		for (int i = 0; i < energyOutputSlots(); i++) {
			energy.add(getStackInSlot(energyOutputSlotsIndex() + i));
		}
		return energy;
	}

	public List<ItemStack> getMatterOutputItems() {
		List<ItemStack> matter = new ArrayList<>();
		for (int i = 0; i < matterOutputSlots(); i++) {
			matter.add(getStackInSlot(matterOutputSlotsIndex() + i));
		}
		return matter;
	}

	public List<ItemStack> getUpgrades() {
		List<ItemStack> upgrades = new ArrayList<>();
		for (int i = 0; i < upgrades(); i++) {
			upgrades.add(getStackInSlot(upgradeIndex() + i));
		}
		return upgrades;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return valid.test(slot, stack, this);
	}

	public boolean isInRange(Player player) {
		if (!hasOwner) {
			return true;
		}
		BlockPos pos = owner.getBlockPos();
		return owner.getLevel().getBlockEntity(pos) == owner
				&& player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64;
	}

	public NonNullList<ItemStack> getItems() {
		return stacks;
	}

	public ItemStack[] getItemsArray() {
		return getItems().toArray(new ItemStack[getItems().size()]);
	}

	@Override
	protected void onContentsChanged(int slot) {
		if (hasOwner && !owner.getLevel().isClientSide()) {
			owner.onInventoryChange(slot, this);
			if (propertyHandler != null) {
				propertyHandler.set(serializeNBT());
			}
			owner.setShouldSaveData(true);
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
		onContentsChanged(-1);
	}

	public void setOutputDirs(@Nonnull List<Direction> dirs) {
		relativeOutputDirs = new HashSet<>();
		for (Direction dir : dirs) {
			relativeOutputDirs.add(dir);
		}
		onContentsChanged(-1);
	}

	public boolean isUpgradeValid(UpgradeType type) {
		if (validUpgrades != null) {
			for (UpgradeType upgrade : validUpgrades) {
				if (upgrade == type)
					return true;
			}
		}
		return false;
	}

	private class ChildInventoryHandler extends CapabilityInventory {

		private int[] indexes;
		private CapabilityInventory parent;

		public ChildInventoryHandler(CapabilityInventory parent, @Nonnull int[] indexes) {
			super();
			this.parent = parent;
			this.indexes = indexes;
		}

		@Override
		public ItemStack getStackInSlot(int slot) {
			return parent.getStackInSlot(indexes[slot]);
		}

		@Override
		public void setStackInSlot(int slot, ItemStack stack) {
			parent.setStackInSlot(indexes[slot], stack);
		}

		@Override
		public int getSlots() {
			return indexes.length;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			return parent.insertItem(indexes[slot], stack, simulate);
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			return parent.extractItem(indexes[slot], amount, simulate);
		}

		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			return parent.isItemValid(indexes[slot], stack);
		}

		@Override
		public int getSlotLimit(int slot) {
			return parent.getSlotLimit(indexes[slot]);
		}

	}

}
