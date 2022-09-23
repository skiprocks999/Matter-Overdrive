package matteroverdrive.common.tile.matter_network;

import javax.annotation.Nullable;

import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryMatterAnalyzer;
import matteroverdrive.common.item.ItemUpgrade;
import matteroverdrive.common.network.NetworkMatter;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.matter.MatterRegister;
import matteroverdrive.core.network.utils.IMatterNetworkMember;
import matteroverdrive.core.property.Property;
import matteroverdrive.core.property.PropertyTypes;
import matteroverdrive.core.sound.SoundBarrierMethods;
import matteroverdrive.core.tile.types.GenericMachineTile;
import matteroverdrive.core.utils.UtilsCapability;
import matteroverdrive.core.utils.UtilsDirection;
import matteroverdrive.core.utils.UtilsItem;
import matteroverdrive.core.utils.UtilsTile;
import matteroverdrive.registry.SoundRegistry;
import matteroverdrive.registry.TileRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.util.TriPredicate;

public class TileMatterAnalyzer extends GenericMachineTile implements IMatterNetworkMember {

	public static final int SLOT_COUNT = 6;
	private static final int ENERGY_STORAGE = 512000;
	private static final int USAGE_PER_TICK = 80;
	private static final double PROCESSING_TIME = 800;
	private static final int DEFAULT_SPEED = 1;
	private static final int PERCENTAGE_PER_SCAN = 20;

	private ItemStack scannedItem = ItemStack.EMPTY;
	// Server-side only
	private boolean shouldAnalyze = false;

	public final Property<CompoundTag> capInventoryProp;
	public final Property<CompoundTag> capEnergyStorageProp;
	public final Property<ItemStack> scannedItemProp;

	public TileMatterAnalyzer(BlockPos pos, BlockState state) {
		super(TileRegistry.TILE_MATTER_ANALYZER.get(), pos, state);

		setSpeed(DEFAULT_SPEED);
		setPowerUsage(USAGE_PER_TICK);
		setProcessingTime(PROCESSING_TIME);

		defaultSpeed = DEFAULT_SPEED;
		defaultPowerStorage = ENERGY_STORAGE;
		defaultPowerUsage = USAGE_PER_TICK;

		capInventoryProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.NBT
				.create(() -> getInventoryCap().serializeNBT(), tag -> getInventoryCap().deserializeNBT(tag)));
		capEnergyStorageProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.NBT
				.create(() -> getEnergyStorageCap().serializeNBT(), tag -> getEnergyStorageCap().deserializeNBT(tag)));
		scannedItemProp = this.getPropertyManager().addTrackedProperty(
				PropertyTypes.ITEM_STACK.create(() -> scannedItem, item -> scannedItem = item.copy()));

		addInventoryCap(new CapabilityInventory(SLOT_COUNT, true, true).setInputs(1).setEnergySlots(1).setUpgrades(4)
				.setOwner(this).setValidUpgrades(InventoryMatterAnalyzer.UPGRADES).setValidator(getValidator())
				.setPropertyManager(capInventoryProp));
		addEnergyStorageCap(new CapabilityEnergyStorage(ENERGY_STORAGE, true, false).setOwner(this)
				.setPropertyManager(capEnergyStorageProp));
		setMenuProvider(new SimpleMenuProvider((id, inv, play) -> new InventoryMatterAnalyzer(id, play.getInventory(),
				getInventoryCap(), getCoordsData()), getContainerName(TypeMachine.MATTER_ANALYZER.id())));
		setTickable();
	}

	@Override
	public void tickServer() {
		UtilsTile.drainElectricSlot(this);
		boolean currState = getLevel().getBlockState(getBlockPos()).getValue(BlockStateProperties.LIT);
		boolean flag = false;
		if (!canRun()) {
			flag = setRunning(false);
			flag |= setProgress(0);
			flag |= setScannedItem(ItemStack.EMPTY);
			if (currState && !isRunning()) {
				UtilsTile.updateLit(this, Boolean.FALSE);
			}
			if (flag) {
				setChanged();
			}
			return;
		}
		CapabilityEnergyStorage energy = getEnergyStorageCap();
		if (energy.getEnergyStored() < getCurrentPowerUsage()) {
			flag = setRunning(false);
			flag |= setProgress(0);
			flag |= setScannedItem(ItemStack.EMPTY);
			if (currState && !isRunning()) {
				UtilsTile.updateLit(this, Boolean.FALSE);
			}
			if (flag) {
				setChanged();
			}
			return;
		}
		CapabilityInventory inv = getInventoryCap();
		ItemStack scanned = inv.getStackInSlot(0);
		if (scanned.isEmpty()) {
			flag = setRunning(false);
			flag |= setProgress(0);
			flag |= setScannedItem(ItemStack.EMPTY);
			if (currState && !isRunning()) {
				UtilsTile.updateLit(this, Boolean.FALSE);
			}
			if (flag) {
				setChanged();
			}
			return;
		}
		NetworkMatter network = getConnectedNetwork();
		if (network == null || !hasAttachedDrives(network)) {
			flag = setRunning(false);
			flag |= setProgress(0);
			flag |= setScannedItem(ItemStack.EMPTY);
			if (currState && !isRunning()) {
				UtilsTile.updateLit(this, Boolean.FALSE);
			}
			if (flag) {
				setChanged();
			}
			return;
		}

		if (scannedItemProp.get().isEmpty()) {
			// this is a very expensive call so the redundant call locations are required
			int[] stored = network.getHighestStorageLocationForItem(scanned.getItem(), true);
			double val = MatterRegister.INSTANCE.getServerMatterValue(scanned);
			if (val <= 0.0 || stored[0] > -1 && stored[3] >= 100) {
				shouldAnalyze = false;
			} else {
				shouldAnalyze = true;
			}

		}
		if (!shouldAnalyze) {
			flag = setRunning(false);
			flag |= setProgress(0);
			flag |= setScannedItem(ItemStack.EMPTY);
			if (currState && !isRunning()) {
				UtilsTile.updateLit(this, Boolean.FALSE);
			}
			if (flag) {
				setChanged();
			}
			return;
		}
		if (!UtilsItem.compareItems(scannedItemProp.get().getItem(), scanned.getItem())) {
			flag = setRunning(false);
			flag |= setProgress(0);
			flag |= setScannedItem(scanned);
			int[] stored = network.getHighestStorageLocationForItem(scannedItemProp.get().getItem(), true);
			double val = MatterRegister.INSTANCE.getServerMatterValue(scanned);
			if (val <= 0.0 || stored[0] > -1 && stored[3] >= 100) {
				shouldAnalyze = false;
			} else {
				shouldAnalyze = true;
			}
			if (currState && !isRunning()) {
				UtilsTile.updateLit(this, Boolean.FALSE);
			}
			if (flag) {
				setChanged();
			}
			return;
		}
		setRunning(true);
		incrementProgress(getCurrentSpeed());
		energy.removeEnergy((int) getCurrentPowerUsage());
		if (!currState && isRunning()) {
			UtilsTile.updateLit(this, Boolean.TRUE);
		}
		setChanged();
		if (getProgress() < PROCESSING_TIME) {
			return;
		}
		int[] stored = network.getHighestStorageLocationForItem(scannedItemProp.get().getItem(), true);
		boolean successFlag = false;
		if (stored[0] > -1) {
			TilePatternStorage drive = network.getStorageFromIndex(stored[0]);
			if (drive != null) {
				if (drive.storeItem(scannedItemProp.get().getItem(), PERCENTAGE_PER_SCAN,
						new int[] { stored[1], stored[2], stored[3] })) {
					successFlag = true;
				} else if (drive.storeItemFirstChance(scannedItemProp.get().getItem(), PERCENTAGE_PER_SCAN)) {
					successFlag = true;
				}
			}
		} else if (network.storeItemFirstChance(scannedItemProp.get().getItem(), PERCENTAGE_PER_SCAN, true)) {
			successFlag = true;
		}

		if (successFlag) {
			scanned.shrink(1);
			playSuccessSound();
		} else {
			playFailureSound();
		}

		setProgress(0);
		shouldAnalyze = false;
		setScannedItem(ItemStack.EMPTY);
		setRunning(false);
		setChanged();
	}

	@Override
	public void tickClient() {
		if (shouldPlaySound() && !clientSoundPlaying) {
			clientSoundPlaying = true;
			SoundBarrierMethods.playTileSound(SoundRegistry.SOUND_MATTER_ANALYZER.get(), this, true);
		}
	}

	@Override
	public boolean canConnectToFace(Direction face) {
		Direction facing = getFacing();
		Direction back = Direction.NORTH;
		Direction relative = UtilsDirection.getRelativeSide(back, handleEastWest(facing));
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
		return true;
	}

	public boolean setScannedItem(ItemStack item) {
		scannedItemProp.set(item);
		return scannedItemProp.isDirtyNoUpdate();
	}

	private boolean hasAttachedDrives(NetworkMatter matter) {
		return matter.getPatternDrives() != null && matter.getPatternDrives().size() > 0;
	}

	private void playFailureSound() {
		getLevel().playSound(null, getBlockPos(), SoundRegistry.SOUND_MATTER_SCANNER_FAIL.get(), SoundSource.BLOCKS,
				0.5F, 1.0F);
	}

	private void playSuccessSound() {
		getLevel().playSound(null, getBlockPos(), SoundRegistry.SOUND_MATTER_SCANNER_SUCCESS.get(), SoundSource.BLOCKS,
				0.5F, 1.0F);
	}

	private static TriPredicate<Integer, ItemStack, CapabilityInventory> getValidator() {
		return (index, stack, cap) -> index == 0 && MatterRegister.INSTANCE.getServerMatterValue(stack) > 0.0
				|| index == 1 && UtilsCapability.hasEnergyCap(stack)
				|| index > 1 && stack.getItem() instanceof ItemUpgrade;
	}

}
