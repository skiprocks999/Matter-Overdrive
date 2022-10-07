package matteroverdrive.common.tile.matter_network;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.math.Vector3f;

import matteroverdrive.MatterOverdrive;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryPatternStorage;
import matteroverdrive.common.item.ItemPatternDrive;
import matteroverdrive.common.item.tools.electric.ItemMatterScanner;
import matteroverdrive.common.network.NetworkMatter;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.capability.types.item_pattern.CapabilityItemPatternStorage;
import matteroverdrive.core.capability.types.item_pattern.ICapabilityItemPatternStorage;
import matteroverdrive.core.capability.types.item_pattern.ItemPatternWrapper;
import matteroverdrive.core.config.MatterOverdriveConfig;
import matteroverdrive.core.network.utils.IMatterNetworkMember;
import matteroverdrive.core.property.Property;
import matteroverdrive.core.property.PropertyTypes;
import matteroverdrive.core.tile.types.GenericMachineTile;
import matteroverdrive.core.utils.UtilsCapability;
import matteroverdrive.core.utils.UtilsDirection;
import matteroverdrive.core.utils.UtilsItem;
import matteroverdrive.core.utils.UtilsMath;
import matteroverdrive.core.utils.UtilsNbt;
import matteroverdrive.core.utils.UtilsParticle;
import matteroverdrive.core.utils.UtilsTile;
import matteroverdrive.registry.TileRegistry;
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

public class TilePatternStorage extends GenericMachineTile implements IMatterNetworkMember {

	private static final List<ItemStack> EMPTY_DRIVES = new ArrayList<>();

	static {
		EMPTY_DRIVES.add(ItemStack.EMPTY);
		EMPTY_DRIVES.add(ItemStack.EMPTY);
		EMPTY_DRIVES.add(ItemStack.EMPTY);
		EMPTY_DRIVES.add(ItemStack.EMPTY);
		EMPTY_DRIVES.add(ItemStack.EMPTY);
		EMPTY_DRIVES.add(ItemStack.EMPTY);
	}

	public static final int SLOT_COUNT = 9;
	private static final int ENERGY_STORAGE = 64000;
	public static final int BASE_USAGE = 50;
	public static final int USAGE_PER_DRIVE = 100;

	public final Property<CompoundTag> capInventoryProp;
	public final Property<CompoundTag> capEnergyStorageProp;

	public TilePatternStorage(BlockPos pos, BlockState state) {
		super(TileRegistry.TILE_PATTERN_STORAGE.get(), pos, state);

		setPowerUsage(BASE_USAGE);

		defaultPowerStorage = ENERGY_STORAGE;
		defaultPowerUsage = BASE_USAGE;
		
		capInventoryProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.NBT
				.create(() -> getInventoryCap().serializeNBT(), tag -> getInventoryCap().deserializeNBT(tag)));
		capEnergyStorageProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.NBT
				.create(() -> getEnergyStorageCap().serializeNBT(), tag -> getEnergyStorageCap().deserializeNBT(tag)));

		addInventoryCap(new CapabilityInventory(SLOT_COUNT, true, true).setInputs(7).setOutputs(1).setEnergyInputSlots(1)
				.setOwner(this).setValidator(getValidator()).setPropertyManager(capInventoryProp));
		addEnergyStorageCap(new CapabilityEnergyStorage(ENERGY_STORAGE, true, false).setOwner(this)
				.setPropertyManager(capEnergyStorageProp));
		setMenuProvider(new SimpleMenuProvider((id, inv, play) -> new InventoryPatternStorage(id, play.getInventory(),
				getInventoryCap(), getCoordsData()), getContainerName(TypeMachine.PATTERN_STORAGE.id())));
		setTickable();
	}

	@Override
	public void tickServer() {
		UtilsTile.drainElectricSlot(this);
		handleOnState();
		
		if (!canRun()) {
			setShouldSaveData(setPowered(false), setPowerUsage(0), setRunning(false), updateTickable(false));
			return;
		}

		CapabilityInventory inv = getInventoryCap();
		CapabilityEnergyStorage energy = getEnergyStorageCap();
		int drives = 0;
		for (ItemStack stack : getDrives()) {
			if (!stack.isEmpty()) {
				drives++;
			}
		}
		setPowerUsage(BASE_USAGE + drives * USAGE_PER_DRIVE);
		setShouldSaveData(true);
		
		if (energy.getEnergyStored() < getCurrentPowerUsage()) {
			setShouldSaveData(setPowered(false), setPowerUsage(0), setRunning(false), updateTickable(false));
			return;
		}
		energy.removeEnergy((int) getCurrentPowerUsage());
		setShouldSaveData(setPowered(true), setRunning(true));

		ItemStack scanner = inv.getStackInSlot(6);
		if (!scanner.isEmpty() && scanner.getItem() instanceof ItemMatterScanner && inv.getStackInSlot(7).isEmpty()) {
			scanner.getOrCreateTag().put(UtilsNbt.BLOCK_POS, NbtUtils.writeBlockPos(getBlockPos()));
			inv.setStackInSlot(7, scanner.copy());
			inv.setStackInSlot(6, ItemStack.EMPTY);
		}
	}

	@Override
	public void tickClient() {
		if (isPowered() && MatterOverdrive.RANDOM.nextFloat() < 0.2F && MatterOverdriveConfig.PATTERN_STORAGE_VENT_PARTICLES.get()) {
			Vector3f pos = UtilsMath.blockPosToVector(worldPosition);
			pos.add(0.5F, 0.5F, 0.5F);
			UtilsParticle.spawnVentParticlesSphere(pos, 0.03F, getFacing(), 1);
		}
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
		return isPowered();
	}

	public CompoundTag getNetworkData() {
		CompoundTag data = new CompoundTag();

		List<ItemPatternWrapper> wrappers = getWrapperList();
		int size = wrappers.size();
		data.putInt("wrapcount", size);
		for (int i = 0; i < wrappers.size(); i++) {
			wrappers.get(i).writeToNbt(data, "pattern" + i);
		}

		data.putBoolean("ispowered", isPowered());

		return data;
	}

	public PatternStorageDataWrapper handleNetworkData(CompoundTag tag) {
		List<ItemPatternWrapper> wrappers = new ArrayList<>();
		for (int i = 0; i < tag.getInt("wrapcount"); i++) {
			wrappers.add(ItemPatternWrapper.readFromNbt(tag.getCompound("pattern" + i)));
		}
		return new PatternStorageDataWrapper(wrappers, tag.getBoolean("ispowered"));
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
		return getInventoryCap().getItems().subList(0, 6);
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
			ItemStack stack = getInventoryCap().getItems().get(index[0]);
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
