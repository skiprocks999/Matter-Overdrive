package matteroverdrive.common.tile.matter_network;

import javax.annotation.Nullable;

import matteroverdrive.SoundRegister;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryMatterAnalyzer;
import matteroverdrive.common.item.ItemUpgrade;
import matteroverdrive.common.network.NetworkMatter;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.matter.MatterRegister;
import matteroverdrive.core.network.utils.IMatterNetworkMember;
import matteroverdrive.core.sound.SoundBarrierMethods;
import matteroverdrive.core.tile.types.GenericSoundTile;
import matteroverdrive.core.utils.UtilsCapability;
import matteroverdrive.core.utils.UtilsDirection;
import matteroverdrive.core.utils.UtilsItem;
import matteroverdrive.core.utils.UtilsTile;
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

public class TileMatterAnalyzer extends GenericSoundTile implements IMatterNetworkMember {

	public static final int SLOT_COUNT = 6;
	private static final int ENERGY_STORAGE = 512000;
	private static final int USAGE_PER_TICK = 80;
	private static final double PROCESSING_TIME = 800;
	private static final int DEFAULT_SPEED = 1;
	private static final int PERCENTAGE_PER_SCAN = 20;
	
	private double currProgress = 0;
	
	private ItemStack scannedItem = null;
	private boolean shouldAnalyze = false;
	
	
	public boolean clientPowered;
	public double clientProgress;
	public ItemStack clientScannedItem = ItemStack.EMPTY;
	
	public TileMatterAnalyzer(BlockPos pos, BlockState state) {
		super(TileRegistry.TILE_MATTER_ANALYZER.get(), pos, state);
		
		setSpeed(DEFAULT_SPEED);
		setPowerUsage(USAGE_PER_TICK);
		
		defaultSpeed = DEFAULT_SPEED;
		defaultPowerStorage = ENERGY_STORAGE;
		defaultPowerUsage = USAGE_PER_TICK;
		
		addInventoryCap(new CapabilityInventory(SLOT_COUNT, true, true).setInputs(1).setEnergySlots(1)
				.setUpgrades(4).setOwner(this).setValidUpgrades(InventoryMatterAnalyzer.UPGRADES)
				.setValidator(getValidator()));
		addEnergyStorageCap(new CapabilityEnergyStorage(ENERGY_STORAGE, true, false).setOwner(this));
		setMenuProvider(
				new SimpleMenuProvider(
						(id, inv, play) -> new InventoryMatterAnalyzer(id, play.getInventory(),
								getInventoryCap(), getCoordsData()),
						getContainerName(TypeMachine.MATTER_ANALYZER.id())));
		setTickable();
		setHasMenuData();
		setHasRenderData();
	}
	
	@Override
	public void tickServer() {
		UtilsTile.drainElectricSlot(this);
		boolean currState = getLevel().getBlockState(getBlockPos()).getValue(BlockStateProperties.LIT);
		if(!canRun()) {
			isRunning = false;
			currProgress = 0;
			scannedItem = null;
			if (currState && !isRunning) {
				UtilsTile.updateLit(this, Boolean.FALSE);
			}
			return;
		}
		CapabilityEnergyStorage energy = getEnergyStorageCap();
		if(energy.getEnergyStored() < getCurrentPowerUsage()) {
			isRunning = false;
			currProgress = 0;
			scannedItem = null;
			if (currState && !isRunning) {
				UtilsTile.updateLit(this, Boolean.FALSE);
			}
			return;
		}
		CapabilityInventory inv = getInventoryCap();
		ItemStack scanned = inv.getStackInSlot(0);
		if(scanned.isEmpty()) {
			isRunning = false;
			currProgress = 0;
			scannedItem = null;
			if (currState && !isRunning) {
				UtilsTile.updateLit(this, Boolean.FALSE);
			}
			return;
		}
		NetworkMatter network = getConnectedNetwork();
		if(network == null || !hasAttachedDrives(network)) {
			isRunning = false;
			currProgress = 0;
			scannedItem = null;
			if (currState && !isRunning) {
				UtilsTile.updateLit(this, Boolean.FALSE);
			}
			return;
		}
		
		if(scannedItem == null) {
			scannedItem = scanned.copy();
			//this is a very expensive call so the redundant call locations are required
			int[] stored = network.getHighestStorageLocationForItem(scannedItem.getItem(), true);
			double val = MatterRegister.INSTANCE.getServerMatterValue(scanned);
			if(val <= 0.0 || stored[0] > -1 && stored[3] >= 100) {
				shouldAnalyze = false;
			} else {
				shouldAnalyze = true;
			}
			
		}
		if(!shouldAnalyze) {
			isRunning = false;
			currProgress = 0;
			if (currState && !isRunning) {
				UtilsTile.updateLit(this, Boolean.FALSE);
			}
			return;
		}
		if(!UtilsItem.compareItems(scannedItem.getItem(), scanned.getItem())) {
			isRunning = false;
			currProgress = 0;
			scannedItem = scanned;
			int[] stored = network.getHighestStorageLocationForItem(scannedItem.getItem(), true);
			double val = MatterRegister.INSTANCE.getServerMatterValue(scanned);
			if(val <= 0.0 || stored[0] > -1 && stored[3] >= 100) {
				shouldAnalyze = false;
			} else {
				shouldAnalyze = true;
			}
			if (currState && !isRunning) {
				UtilsTile.updateLit(this, Boolean.FALSE);
			}
			return;
		}
		isRunning = true;
		currProgress += getCurrentSpeed();
		energy.removeEnergy((int) getCurrentPowerUsage());
		if (!currState && isRunning) {
			UtilsTile.updateLit(this, Boolean.TRUE);
		}
		setChanged();
		if(currProgress < PROCESSING_TIME) {
			return;
		}
		int[] stored = network.getHighestStorageLocationForItem(scannedItem.getItem(), true);
		boolean successFlag = false;
		if(stored[0] > -1) {
			TilePatternStorage drive = network.getStorageFromIndex(stored[0]);
			if(drive != null) {
				if(drive.storeItem(scannedItem.getItem(), PERCENTAGE_PER_SCAN, new int[] {stored[1], stored[2], stored[3]})) {
					successFlag = true;
				} else if(drive.storeItemFirstChance(scannedItem.getItem(), PERCENTAGE_PER_SCAN)) {
					successFlag = true;
				} 
			} 
		} else if(network.storeItemFirstChance(scannedItem.getItem(), PERCENTAGE_PER_SCAN, true)){
			successFlag = true;
		} 
		
		if(successFlag) {
			scanned.shrink(1);
			playSuccessSound();
		} else {
			playFailureSound();
		}
		
		currProgress = 0;
		shouldAnalyze = false;
		scannedItem = null;
		isRunning = false;
		setChanged();
	}
	
	@Override
	public void tickClient() {
		if (shouldPlaySound() && !clientSoundPlaying) {
			clientSoundPlaying = true;
			SoundBarrierMethods.playTileSound(SoundRegister.SOUND_MATTER_ANALYZER.get(), this, true);
		}
	}
	
	@Override
	public void getMenuData(CompoundTag tag) {
		
		tag.putDouble("progress", currProgress);
		if(scannedItem != null && !scannedItem.isEmpty()) {
			CompoundTag item = new CompoundTag();
			scannedItem.save(item);
			tag.put("item", item);
		}
		
	}
	
	@Override
	public void readMenuData(CompoundTag tag) {
		
		clientProgress = tag.getDouble("progress");
		if(tag.contains("item")) {
			clientScannedItem = ItemStack.of(tag.getCompound("item"));
		} else {
			clientScannedItem = ItemStack.EMPTY;
		}
		
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		CompoundTag data = new CompoundTag();
		
		data.putDouble("progress", currProgress);
		data.putDouble("speed", getCurrentSpeed());
		data.putDouble("usage", getCurrentPowerUsage());
		data.putBoolean("muffled", isMuffled());
		
		tag.put("data", data);
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		CompoundTag data = tag.getCompound("data");
		
		currProgress = data.getDouble("progress");
		setSpeed(data.getDouble("speed"));
		setPowerUsage(data.getDouble("usage"));
		setMuffled(data.getBoolean("muffled"));
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
		if(entity != null && entity instanceof TileMatterNetworkCable cable) {
			return (NetworkMatter) cable.getNetwork(false);
		}
		return null;
	}

	@Override
	public boolean isPowered(boolean client) {
		return true;
	}

	@Override
	public double getCurrentPowerStorage() {
		return getEnergyStorageCap().getMaxEnergyStored();
	}

	@Override
	public void setPowerStorage(double storage) {
		getEnergyStorageCap().updateMaxEnergyStorage((int) storage);
	}
	
	@Override
	public double getProcessingTime() {
		return PROCESSING_TIME;
	}
	
	private boolean hasAttachedDrives(NetworkMatter matter) {
		return matter.getPatternDrives() != null && matter.getPatternDrives().size() > 0;
	}
	
	private void playFailureSound() {
		getLevel().playSound(null, getBlockPos(), SoundRegister.SOUND_MATTER_SCANNER_FAIL.get(), SoundSource.BLOCKS, 0.5F, 1.0F);
	}
	
	private void playSuccessSound() {
		getLevel().playSound(null, getBlockPos(), SoundRegister.SOUND_MATTER_SCANNER_SUCCESS.get(), SoundSource.BLOCKS, 0.5F, 1.0F);
	}
	
	private static TriPredicate<Integer, ItemStack, CapabilityInventory> getValidator() {
		return (index, stack, cap) -> index == 0 && MatterRegister.INSTANCE.getServerMatterValue(stack) > 0.0
				|| index == 1 && UtilsCapability.hasEnergyCap(stack)
				|| index > 1 && stack.getItem() instanceof ItemUpgrade;
	}

}
