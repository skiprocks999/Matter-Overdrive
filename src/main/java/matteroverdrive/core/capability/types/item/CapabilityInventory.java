package matteroverdrive.core.capability.types.item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import matteroverdrive.core.capability.IOverdriveCapability;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.tile.GenericTile;
import matteroverdrive.core.utils.DirectionUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class CapabilityInventory extends ItemStackHandler implements IOverdriveCapability {

	private HashSet<Direction> relativeInputDirs;
	private HashSet<Direction> relativeOutputDirs;
	
	private boolean isSided = false;
	
	private int inputs = 0;
	private int outputs = 0;
	private int byproducts = 0;
	//not included in child cap
	private int upgrades = 0;
	
	@Nullable
	private GenericTile owner;
	
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
	
	public CapabilityInventory setIsSided() {
		isSided = true;
		return this;
	}
	
	public CapabilityInventory setOwner(GenericTile tile) {
		owner = tile;
		return this;
	}
	
	public CapabilityInventory setDefaultInputs(@Nonnull Direction...dirs) {
		if(relativeInputDirs == null) {
			relativeInputDirs = new HashSet<>();
			for(Direction dir : dirs) {
				relativeInputDirs.add(dir);
			}
			setInputCaps();
		}
		return this;
	}
	
	public CapabilityInventory setDefaultOutputs(@Nonnull Direction...dirs) {
		if(relativeOutputDirs == null) {
			relativeOutputDirs = new HashSet<>();
			for(Direction dir : dirs) {
				relativeOutputDirs.add(dir);
			}
			setOutputCaps();
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
	
	public int upgrades() {
		return upgrades;
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
	
	public int upgradeIndex() {
		return byproductIndex() + byproducts;
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (matchesCapability(cap)) {
			if (isSided) {
				if (side == null || owner == null) {
					return LazyOptional.empty();
				} 
				Direction dir = DirectionUtils.getRelativeSide(owner.getFacing(), side);
				if(relativeInputDirs.contains(dir)) {
					return sideCaps[dir.ordinal()].cast();
				} else if (relativeOutputDirs.contains(dir)) {
					return sideCaps[dir.ordinal()].cast();
				} else {
					return LazyOptional.empty();
				}
			} 
			return castHolder();
		}
		return LazyOptional.empty();
	}
	
	@Override
	public <T> boolean matchesCapability(Capability<T> capability) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
	}

	@Override
	public <T> LazyOptional<T> castHolder() {
		return holder.cast();
	}

	@Override
	public CapabilityType getCapabilityType() {
		return CapabilityType.Item;
	}
	
	@Override
	public void onLoad(BlockEntity tile) {
		
	}
	
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = super.serializeNBT();
		
		int inDirSize = relativeInputDirs.size();
		tag.putInt("inDirSize", inDirSize);
		ListTag inList = new ListTag();
		int index = 0;
		Iterator<Direction> it = relativeInputDirs.iterator();
		while (it.hasNext()) {
			CompoundTag dirTag = new CompoundTag();
			dirTag.putString("inDir" + index , it.next().toString());
			inList.add(dirTag);
			index ++;
		}
		tag.put("inDirs", inList);
		
		int outDirSize = relativeOutputDirs.size();
		tag.putInt("outDirSize", outDirSize);
		ListTag outList = new ListTag();
		index = 0;
		it = relativeOutputDirs.iterator();
		while (it.hasNext()) {
			CompoundTag dirTag = new CompoundTag();
			dirTag.putString("outDir" + index , it.next().toString());
			outList.add(dirTag);
			index ++;
		}
		tag.put("outDirs", inList);
		
		return tag;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		super.deserializeNBT(nbt);
		
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
		invalidateCaps();
	}
	
	private void invalidateCaps() {
		if(holder != null) {
			holder.invalidate();
		}
		if(childInput != null) {
			childInput.invalidate();
		}
		if(childOutput != null) {
			childOutput.invalidate();
		}
		if (isSided) {
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
		childInput = LazyOptional.of(() -> {
			int[] slots = new int[inputs()];
			for (int i = 0; i < inputs(); i++) {
				slots[i] = inputIndex() + i;
			}
			NonNullList<ItemStack> stacks = NonNullList.create();
			stacks.addAll(getInputs());
			return new ChildInventoryHandler(stacks, this, slots);
		});
		for(Direction dir : relativeInputDirs) {
			sideCaps[dir.ordinal()] = childInput;
		}
	}
	
	private void setOutputCaps() {
		childOutput = LazyOptional.of(() -> {
			int[] slots = new int[outputs() + byproducts()];
			for (int i = 0; i < outputs(); i++) {
				slots[i] = outputIndex() + i;
			}
			for (int i = outputs(); i < byproducts(); i++) {
				slots[i] = byproductIndex() + i;
			}
			NonNullList<ItemStack> stacks = NonNullList.create();
			stacks.addAll(getOutputs());
			stacks.addAll(getByproducts());
			return new ChildInventoryHandler(stacks, this, slots);
		});
		for(Direction dir : relativeOutputDirs) {
			sideCaps[dir.ordinal()] = childOutput;
		}
	}
	
	public List<ItemStack> getInputs(){
		List<ItemStack> inputs = new ArrayList<>();
		for (int i = 0; i < inputs(); i++) {
			inputs.add(getStackInSlot(inputIndex() + i));
		}
		return inputs;
	}
	
	public List<ItemStack> getOutputs(){
		List<ItemStack> outputs = new ArrayList<>();
		for (int i = 0; i < outputs(); i++) {
			outputs.add(getStackInSlot(outputIndex() + i));
		}
		return outputs;
	}
	
	public List<ItemStack> getByproducts(){
		List<ItemStack> byprouducts = new ArrayList<>();
		for (int i = 0; i < byproducts(); i++) {
			byprouducts.add(getStackInSlot(byproductIndex() + i));
		}
		return byprouducts;
	}
	
	public List<ItemStack> getUpgrades(){
		List<ItemStack> upgrades = new ArrayList<>();
		for (int i = 0; i < upgrades(); i++) {
			upgrades.add(getStackInSlot(upgradeIndex() + i));
		}
		return upgrades;
	}
	
	private class ChildInventoryHandler extends CapabilityInventory {

		private int[] indexes;
		private CapabilityInventory parent;
		
		public ChildInventoryHandler(NonNullList<ItemStack> stacks, CapabilityInventory parent, int...indexes) {
			super(stacks);
			this.parent = parent;
			this.indexes = indexes;
		}
		
		@Override
		protected void onContentsChanged(int slot) {
			parent.setStackInSlot(indexes[slot], getStackInSlot(slot).copy());
		}
		
	}

}
