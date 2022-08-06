package matteroverdrive.common.tile.matter_network;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.math.Vector3f;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.MatterOverdrive;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryPatternStorage;
import matteroverdrive.common.item.ItemPatternDrive;
import matteroverdrive.common.item.tools.electric.ItemMatterScanner;
import matteroverdrive.common.network.NetworkMatter;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.capability.types.item_pattern.CapabilityItemPatternStorage;
import matteroverdrive.core.capability.types.item_pattern.ICapabilityItemPatternStorage;
import matteroverdrive.core.capability.types.item_pattern.ItemPatternWrapper;
import matteroverdrive.core.network.utils.IMatterNetworkMember;
import matteroverdrive.core.tile.types.old.GenericRedstoneTile;
import matteroverdrive.core.utils.UtilsCapability;
import matteroverdrive.core.utils.UtilsDirection;
import matteroverdrive.core.utils.UtilsItem;
import matteroverdrive.core.utils.UtilsMath;
import matteroverdrive.core.utils.UtilsNbt;
import matteroverdrive.core.utils.UtilsParticle;
import matteroverdrive.core.utils.UtilsTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.TriPredicate;

public class TilePatternStorage extends GenericRedstoneTile implements IMatterNetworkMember {

	private static final List<ItemStack> EMPTY_DRIVES = new ArrayList<>();

	static {
		EMPTY_DRIVES.add(ItemStack.EMPTY);
		EMPTY_DRIVES.add(ItemStack.EMPTY);
		EMPTY_DRIVES.add(ItemStack.EMPTY);
		EMPTY_DRIVES.add(ItemStack.EMPTY);
		EMPTY_DRIVES.add(ItemStack.EMPTY);
		EMPTY_DRIVES.add(ItemStack.EMPTY);
	}

	private boolean isPowered = false;

	public static final int SLOT_COUNT = 9;
	private static final int ENERGY_STORAGE = 64000;
	public static final int BASE_USAGE = 50;
	public static final int USAGE_PER_DRIVE = 100;

	public boolean clientTilePowered;

	public CapabilityInventory clientInventory;
	public CapabilityEnergyStorage clientEnergy;

	public TilePatternStorage(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_PATTERN_STORAGE.get(), pos, state);
		addCapability(new CapabilityInventory(SLOT_COUNT, true, true).setInputs(7).setOutputs(1).setEnergySlots(1)
				.setOwner(this).setValidator(getValidator()));
		addCapability(new CapabilityEnergyStorage(ENERGY_STORAGE, true, false).setOwner(this));
		setMenuProvider(new SimpleMenuProvider(
				(id, inv, play) -> new InventoryPatternStorage(id, play.getInventory(),
						exposeCapability(CapabilityType.Item), getCoordsData()),
				getContainerName(TypeMachine.PATTERN_STORAGE.id())));
		setTickable();
		setHasMenuData();
		setHasRenderData();
	}

	@Override
	public void tickServer() {
		if (canRun()) {
			UtilsTile.drainElectricSlot(this);
			CapabilityInventory inv = exposeCapability(CapabilityType.Item);
			CapabilityEnergyStorage energy = exposeCapability(CapabilityType.Energy);
			int drives = 0;
			for (ItemStack stack : inv.getInputs()) {
				if (stack.getItem() instanceof ItemPatternDrive) {
					drives++;
				}
			}
			int usage = BASE_USAGE + drives * USAGE_PER_DRIVE;
			if (energy.getEnergyStored() >= usage) {
				isPowered = true;
				energy.removeEnergy(usage);
			} else {
				isPowered = false;
			}
			ItemStack scanner = inv.getStackInSlot(6);
			if (!scanner.isEmpty() && scanner.getItem() instanceof ItemMatterScanner
					&& inv.getStackInSlot(7).isEmpty()) {
				scanner.getOrCreateTag().put(UtilsNbt.BLOCK_POS, NbtUtils.writeBlockPos(getBlockPos()));
				inv.setStackInSlot(7, scanner.copy());
				scanner.shrink(1);
			}
		} else {
			isPowered = false;
		}
	}

	@Override
	public void tickClient() {
		if (clientTilePowered && MatterOverdrive.RANDOM.nextFloat() < 0.2F) {
			Vector3f pos = UtilsMath.blockPosToVector(worldPosition);
			pos.add(0.5F, 0.5F, 0.5F);
			UtilsParticle.spawnVentParticles(pos, 0.03F, getFacing(), 1);
		}
	}

	@Override
	public void getMenuData(CompoundTag tag) {
		CapabilityEnergyStorage energy = exposeCapability(CapabilityType.Energy);
		tag.put(energy.getSaveKey(), energy.serializeNBT());
		CapabilityInventory inv = exposeCapability(CapabilityType.Item);
		tag.put(inv.getSaveKey(), inv.serializeNBT());

	}

	@Override
	public void readMenuData(CompoundTag tag) {
		clientEnergy = new CapabilityEnergyStorage(0, false, false);
		clientEnergy.deserializeNBT(tag.getCompound(clientEnergy.getSaveKey()));
		clientInventory = new CapabilityInventory();
		clientInventory.deserializeNBT(tag.getCompound(clientInventory.getSaveKey()));
	}

	@Override
	public void getRenderData(CompoundTag tag) {
		tag.putBoolean("isPowered", isPowered);
	}

	@Override
	public void readRenderData(CompoundTag tag) {
		clientTilePowered = tag.getBoolean("isPowered");
	}

	@Override
	public boolean canConnectToFace(Direction face) {
		Direction relative = UtilsDirection.getRelativeSide(Direction.NORTH, handleEastWest(getFacing()));
		return relative == face;
	}

	@Override
	@Nullable
	public NetworkMatter getConnectedNetwork() {
		Direction back = UtilsDirection.getRelativeSide(Direction.NORTH, handleEastWest(getFacing()));
		BlockEntity entity = getLevel().getBlockEntity(getBlockPos().relative(back));
		if (entity != null && entity instanceof TileMatterNetworkCable cable) {
			return (NetworkMatter) cable.getNetwork(false);
		}
		return null;
	}

	@Override
	public boolean isPowered(boolean client) {
		return client ? clientTilePowered : isPowered;
	}

	public CompoundTag getNetworkData() {
		CompoundTag data = new CompoundTag();

		List<ItemPatternWrapper> wrappers = getWrapperList();
		int size = wrappers.size();
		data.putInt("wrapcount", size);
		for (int i = 0; i < wrappers.size(); i++) {
			wrappers.get(i).writeToNbt(data, "pattern" + i);
		}

		data.putBoolean("ispowered", isPowered);

		return data;
	}

	public PatternStorageDataWrapper handleNetworkData(CompoundTag tag) {
		List<ItemPatternWrapper> wrappers = new ArrayList<>();
		for (int i = 0; i < tag.getInt("wrapcount"); i++) {
			wrappers.add(ItemPatternWrapper.readFromNbt(tag.getCompound("pattern" + i)));
		}
		return new PatternStorageDataWrapper(wrappers, tag.getBoolean("ispowered"));
	}

	@Override
	public int getMaxMode() {
		return 2;
	}

	public boolean containsItem(Item item) {
		for (ItemStack stack : getDrives()) {
			if (stack.getItem() instanceof ItemPatternDrive drive) {
				CapabilityItemPatternStorage cap = UtilsItem.getPatternStorageCap(stack);
				if (cap != null) {
					for (ItemPatternWrapper wrapper : cap.getStoredPatterns()) {
						if (wrapper.isItem(item)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Attempts to store an item
	 * 
	 * SERVERSIDE ONLY
	 * 
	 * @param item  : the item to store
	 * @param amt   : the percentage amount to add
	 * @param index : the index to store at
	 * @return whether or not the item was actually stored
	 */
	public boolean storeItem(Item item, int amt, int[] index) {
		if (index[0] > -1 && index[2] < ItemPatternWrapper.MAX) {
			ItemPatternWrapper wrapper = getWrapperFromIndex(index);
			if (wrapper == null) {
				return false;
			}
			wrapper.increasePercentage(amt);
			return true;
		}
		return false;
	}

	/**
	 * Will attempt to store an item in the first empty slot it finds
	 * 
	 * SEVERSIDE ONLY
	 * 
	 * @param item : the item to store
	 * @param amt  : the percentage amount
	 * @return if the item was stored
	 */
	public boolean storeItemFirstChance(Item item, int amt) {
		for (ItemStack stack : getDrives()) {
			if (stack.getItem() instanceof ItemPatternDrive drive) {
				CapabilityItemPatternStorage cap = UtilsItem.getPatternStorageCap(stack);
				if (cap != null) {
					for (ItemPatternWrapper wrapper : cap.getStoredPatterns()) {
						if (wrapper.isAir()) {
							wrapper.setItem(item);
							wrapper.increasePercentage(amt);
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Checks whether or not a pattern can be stored to this drive
	 * 
	 * @param client  : whether or not the request is on the client
	 * @param network : whether or not the request is for the client network
	 *                inventory
	 * @return true if no slots available, false if a slot is available
	 */
	public boolean isFull() {
		for (ItemStack stack : getDrives()) {
			if (stack.getItem() instanceof ItemPatternDrive drive) {
				CapabilityItemPatternStorage cap = UtilsItem.getPatternStorageCap(stack);
				if (cap != null) {
					for (ItemPatternWrapper wrapper : cap.getStoredPatterns()) {
						if (wrapper.isAir()) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * Will return the drive slot, wrapper slot, and percentage stored for the most
	 * complete pattern of the specified item
	 * 
	 * SERVERSIDE ONLY
	 * 
	 * @param item : the item to search for
	 * @return the relevant indexes
	 */
	public int[] getHighestStorageLocForItem(Item item) {
		ItemPatternWrapper[][] array = getWrappers();
		ItemPatternWrapper[] holder;
		int drive = -1;
		int patSlot = -1;
		int highestPerc = -1;
		for (int i = 0; i < 6; i++) {
			holder = array[i];
			int j = 0;
			for (ItemPatternWrapper wrapper : holder) {
				if (wrapper.isItem(item) && wrapper.getPercentage() > highestPerc) {
					highestPerc = wrapper.getPercentage();
					drive = i;
					patSlot = j;
				}
				j++;
			}
		}
		return new int[] { drive, patSlot, highestPerc };
	}

	public List<ItemStack> getDrives() {
		return this.<CapabilityInventory>exposeCapability(CapabilityType.Item).getItems().subList(0, 6);
	}

	/**
	 * Returns an array of the stored patterns for each drive in a slot
	 * 
	 * Note a member can never be null
	 * 
	 * @param client  : whether or not the request is on the client
	 * @param network : whether or not the request is for the client network
	 *                inventory
	 * @return an array of the stored patterns for each drive in a slot
	 */
	public ItemPatternWrapper[][] getWrappers() {
		ItemPatternWrapper[][] array = new ItemPatternWrapper[6][];
		List<ItemStack> drives = getDrives();
		for (int i = 0; i < 6; i++) {
			CapabilityItemPatternStorage cap = UtilsItem.getPatternStorageCap(drives.get(i));
			if (cap == null) {
				array[i] = new ItemPatternWrapper[] {};
			} else {
				array[i] = cap.getStoredPatterns();
			}
		}
		return array;
	}

	/**
	 * Returns a pattern wrapper from the specified index
	 * 
	 * @param index : the index to look at
	 * @return the pattern wrapper from the location
	 */
	@Nullable
	public ItemPatternWrapper getWrapperFromIndex(int[] index) {
		if (index[0] > -1) {
			ItemStack stack = this.<CapabilityInventory>exposeCapability(CapabilityType.Item).getItems().get(index[0]);
			ICapabilityItemPatternStorage cap = UtilsItem.getPatternCap(stack);
			if (cap != null) {
				return cap.getStoredPatterns()[index[1]];
			}
		}
		return null;
	}

	private List<ItemPatternWrapper> getWrapperList() {
		List<ItemPatternWrapper> wrappers = new ArrayList<>();
		for (ItemPatternWrapper[] arr : getWrappers()) {
			for (ItemPatternWrapper wrapper : arr) {
				if (wrapper != null && wrapper.isNotAir()) {
					wrappers.add(wrapper);
				}
			}
		}
		return wrappers;
	}

	private static TriPredicate<Integer, ItemStack, CapabilityInventory> getValidator() {
		return (index, stack,
				cap) -> index < 6 && stack.getItem() instanceof ItemPatternDrive drive && !drive.isFused(stack)
						|| index == 6 && stack.getItem() instanceof ItemMatterScanner
						|| index == 8 && UtilsCapability.hasEnergyCap(stack);
	}

	public static class PatternStorageDataWrapper {

		private List<ItemPatternWrapper> wrappers;
		private boolean isPowered;

		public PatternStorageDataWrapper(List<ItemPatternWrapper> wrappers, boolean isPowered) {
			this.wrappers = wrappers;
			this.isPowered = isPowered;
		}

		public List<ItemPatternWrapper> getPatterns() {
			return wrappers;
		}

		public boolean isPowered() {
			return isPowered;
		}

	}

}
