package matteroverdrive.core.capability.types.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import matteroverdrive.core.block.GenericMachineBlock;
import matteroverdrive.core.capability.IOverdriveCapability;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.tile.GenericTile;
import matteroverdrive.core.utils.UtilsDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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

	private TriPredicate<Integer, ItemStack, CapabilityInventory> valid = (slot, item, inv) -> true;

	private HashSet<Direction> relativeInputDirs;
	private HashSet<Direction> relativeOutputDirs;

	private boolean isSided = false;

	private int inputs = 0;
	private int outputs = 0;
	private int byproducts = 0;
	private int energySlot = 0;
	private int matterSlot = 0;
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

	public CapabilityInventory() {
		super();
	}

	public CapabilityInventory(int size) {
		super(size);
	}

	public CapabilityInventory(NonNullList<ItemStack> stacks) {
		super(stacks);
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
			initialFacing = initialState.getValue(GenericMachineBlock.FACING);
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
	
	public CapabilityInventory setEnergySlots(int count) {
		energySlot = count;
		return this;
	}
	
	public CapabilityInventory setMatterSlots(int count) {
		matterSlot = count;
		return this;
	}

	public CapabilityInventory setUpgrades(int count) {
		upgrades = count;
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
	
	public int energySlots() {
		return energySlot;
	}
	
	public int matterSlots() {
		return matterSlot;
	}

	public int upgrades() {
		return upgrades;
	}
	
	public int externalCount() {
		return inputs() + outputs() + byproducts() + energySlots() + matterSlots();
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
	
	public int energySlotsIndex() {
		return byproductIndex() + byproducts;
	}
	
	public int matterSlotsIndex() {
		return energySlotsIndex() + energySlot;
	}

	public int upgradeIndex() {
		return matterSlotsIndex() + matterSlot;
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
	public CapabilityType getCapabilityType() {
		return CapabilityType.Item;
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

		ListTag inList = new ListTag();
		ListTag outList = new ListTag();
		int inDirSize = 0;
		int outDirSize = 0;

		if (isSided) {
			if(relativeInputDirs != null) {
				List<Direction> inDirs = new ArrayList<>(relativeInputDirs);
				inDirSize = inDirs.size();
				for(int i = 0; i < inDirSize; i++) {
					CompoundTag dirTag = new CompoundTag();
					dirTag.putString("inDir" + i, inDirs.get(i).toString());
					inList.add(dirTag);
				}
			}
			if(relativeOutputDirs != null) {
				List<Direction> outDirs = new ArrayList<>(relativeOutputDirs);
				outDirSize = outDirs.size();
				for(int i = 0; i < outDirSize; i++) {
					CompoundTag dirTag = new CompoundTag();
					dirTag.putString("outDir" + i, outDirs.get(i).toString());
					outList.add(dirTag);
				}
			}
		}

		if(inDirSize > 0) {
			tag.putInt("inDirSize", inDirSize);
			tag.put("inDirs", inList);
		}
		if(outDirSize > 0) {
			tag.putInt("outDirSize", outDirSize);
			tag.put("outDirs", inList);
		}

		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		super.deserializeNBT(nbt);
		if(nbt.contains("inDirSize")) {
			relativeInputDirs = new HashSet<>();
			ListTag inList = nbt.getList("inDirs", 10);
			for (int i = 0; i < nbt.getInt("inDirSize"); i++) {
				relativeInputDirs.add(Direction.byName(inList.getCompound(i).getString("inDir" + i)));
			}

		}
		if(nbt.contains("outDirSize")) {
			relativeOutputDirs = new HashSet<>();
			ListTag outList = nbt.getList("outDirs", 10);
			for (int i = 0; i < nbt.getInt("outDirSize"); i++) {
				relativeOutputDirs.add(Direction.byName(outList.getCompound(i).getString("outDir" + i)));
			}
		}
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
		} else {
			holder = LazyOptional.of(() -> {
				int[] slots = new int[externalCount()];
				for(int i = 0; i < slots.length; i++) {
					slots[i] = i;
				}
				return new ChildInventoryHandler(this, slots);
			});
		}
	}

	private void setInputCaps() {
		childInput = LazyOptional.of(() -> {
			int[] slots = new int[inputs() + energySlots() + matterSlots()];
			int index = 0;
			for (int i = 0; i < inputs(); i++) {
				slots[index] = inputIndex() + i;
				index++;
			}
			for(int i = 0; i < energySlots(); i++) {
				slots[index] = energySlotsIndex() + i;
				index++;
			}
			for(int i = 0; i < matterSlots(); i++) {
				slots[index] = matterSlotsIndex() + i;
				index++;
			}
			return new ChildInventoryHandler(this, slots);
		});
		Direction facing;
		if (initialFacing == null) {
			facing = owner.getFacing();
		} else {
			facing = initialFacing;
			initialFacing = null;
		}
		for (Direction dir : relativeInputDirs) {
			sideCaps[UtilsDirection.getRelativeSide(facing, dir).ordinal()] = childInput;
		}
	}

	private void setOutputCaps() {
		childOutput = LazyOptional.of(() -> {
			int[] slots = new int[outputs() + byproducts() + energySlots() + matterSlots()];
			int index = 0;
			for (int i = 0; i < outputs(); i++) {
				slots[index] = outputIndex() + i;
				index++;
			}
			for (int i = 0; i < byproducts(); i++) {
				slots[index] = byproductIndex() + i;
				index++;
			}
			for (int i = 0; i < energySlots(); i++) {
				slots[index] = energySlotsIndex() + i;
				index++;
			}
			for (int i = 0; i < matterSlots(); i++) {
				slots[index] = matterSlotsIndex() + i;
				index++;
			}
			return new ChildInventoryHandler(this, slots);
		});
		Direction facing;
		if (initialFacing == null) {
			facing = owner.getFacing();
		} else {
			facing = initialFacing;
			initialFacing = null;
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
	
	public List<ItemStack> getEnergyItems() {
		List<ItemStack> energy = new ArrayList<>();
		for (int i = 0; i < energySlots(); i++) {
			energy.add(getStackInSlot(energySlotsIndex() + i));
		}
		return energy;
	}
	
	public List<ItemStack> getMatterItems() {
		List<ItemStack> matter = new ArrayList<>();
		for (int i = 0; i < matterSlots(); i++) {
			matter.add(getStackInSlot(matterSlotsIndex() + i));
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
		if(hasOwner) {
			owner.setChanged();
		}
	}

	private class ChildInventoryHandler extends CapabilityInventory {

		private int[] indexes;
		private CapabilityInventory parent;

		public ChildInventoryHandler(CapabilityInventory parent, @Nonnull int[] indexes) {
			super(0);
			this.parent = parent;
			this.indexes = indexes;
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
