package matteroverdrive.common.tile.matter_network;

import javax.annotation.Nullable;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.SoundRegister;
import matteroverdrive.common.block.machine.variants.BlockLightableMachine;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryMatterAnalyzer;
import matteroverdrive.common.item.ItemUpgrade;
import matteroverdrive.common.network.NetworkMatter;
import matteroverdrive.core.capability.types.CapabilityType;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.TriPredicate;

public class TileMatterAnalyzer extends GenericSoundTile implements IMatterNetworkMember {

	public static final int SLOT_COUNT = 6;
	private static final int ENERGY_STORAGE = 512000;
	private static final int USAGE_PER_TICK = 80;
	private static final double PROCESSING_TIME = 800;
	private static final int DEFAULT_SPEED = 1;
	private static final int PERCENTAGE_PER_SCAN = 20;
	
	private boolean isRunning = false;
	private double currProgress = 0;
	private double currSpeed = DEFAULT_SPEED;
	private int usage = USAGE_PER_TICK;
	private boolean isMuffled = false;
	
	private ItemStack scannedItem = null;
	private boolean shouldAnalyze = false;
	
	
	public boolean clientPowered;
	public boolean clientRunning = false;
	private boolean clientMuffled;
	private boolean clientSoundPlaying = false; 
	public CapabilityEnergyStorage clientEnergy;
	public int clientEnergyUsage;
	public double clientProgress;
	public double clientSpeed;
	public ItemStack clientScannedItem = ItemStack.EMPTY;
	
	public TileMatterAnalyzer(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_MATTER_ANALYZER.get(), pos, state);
		addCapability(new CapabilityInventory(SLOT_COUNT, true, true).setInputs(1).setEnergySlots(1)
				.setUpgrades(4).setOwner(this).setValidUpgrades(InventoryMatterAnalyzer.UPGRADES)
				.setValidator(getValidator()));
		addCapability(new CapabilityEnergyStorage(ENERGY_STORAGE, true, false).setOwner(this));
		setMenuProvider(
				new SimpleMenuProvider(
						(id, inv, play) -> new InventoryMatterAnalyzer(id, play.getInventory(),
								exposeCapability(CapabilityType.Item), getCoordsData()),
						getContainerName(TypeMachine.MATTER_ANALYZER.id())));
		setTickable();
		setHasMenuData();
		setHasRenderData();
	}
	
	@Override
	public void tickServer() {
		UtilsTile.drainElectricSlot(this);
		boolean currState = getLevel().getBlockState(getBlockPos()).getValue(BlockLightableMachine.LIT);
		if(!canRun()) {
			isRunning = false;
			currProgress = 0;
			scannedItem = null;
			if (currState && !isRunning) {
				UtilsTile.updateLit(this, Boolean.FALSE);
			}
			return;
		}
		CapabilityEnergyStorage energy = exposeCapability(CapabilityType.Energy);
		if(energy.getEnergyStored() < usage) {
			isRunning = false;
			currProgress = 0;
			scannedItem = null;
			if (currState && !isRunning) {
				UtilsTile.updateLit(this, Boolean.FALSE);
			}
			return;
		}
		CapabilityInventory inv = exposeCapability(CapabilityType.Item);
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
			Double val = MatterRegister.INSTANCE.getServerMatterValue(scanned);
			if(val == null || stored[0] > -1 && stored[3] >= 100) {
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
			Double val = MatterRegister.INSTANCE.getServerMatterValue(scanned);
			if(val == null || stored[0] > -1 && stored[3] >= 100) {
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
		currProgress += currSpeed;
		energy.removeEnergy(usage);
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
		
		CapabilityEnergyStorage energy = exposeCapability(CapabilityType.Energy);
		tag.put(energy.getSaveKey(), energy.serializeNBT());
		tag.putInt("usage", usage);
		tag.putDouble("sabonus", saMultiplier);
		tag.putBoolean("running", isRunning);
		tag.putDouble("progress", currProgress);
		tag.putDouble("speed", currSpeed);
		tag.putBoolean("muffled", isMuffled);
		if(scannedItem != null && !scannedItem.isEmpty()) {
			CompoundTag item = new CompoundTag();
			scannedItem.save(item);
			tag.put("item", item);
		}
		
	}
	
	@Override
	public void readMenuData(CompoundTag tag) {
		
		clientEnergy = new CapabilityEnergyStorage(0, false, false);
		clientEnergy.deserializeNBT(tag.getCompound(clientEnergy.getSaveKey()));
		clientEnergyUsage = tag.getInt("usage");
		clientSAMultipler = tag.getDouble("sabonus");
		clientRunning = tag.getBoolean("running");
		clientProgress = tag.getDouble("progress");
		clientSpeed = tag.getDouble("speed");
		clientMuffled = tag.getBoolean("muffled");
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
		data.putDouble("speed", currSpeed);
		data.putInt("usage", usage);
		data.putBoolean("muffled", isMuffled);
		
		tag.put("data", data);
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		CompoundTag data = tag.getCompound("data");
		
		currProgress = data.getDouble("progress");
		currSpeed = data.getDouble("speed");
		usage = data.getInt("usage");
		isMuffled = data.getBoolean("muffled");
	}

	@Override
	public boolean shouldPlaySound() {
		return clientRunning && !clientMuffled;
	}

	@Override
	public void setNotPlaying() {
		clientSoundPlaying = false;
	}

	@Override
	public int getMaxMode() {
		return 2;
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
	public double getDefaultSpeed() {
		return DEFAULT_SPEED;
	}

	@Override
	public double getDefaultPowerStorage() {
		return ENERGY_STORAGE;
	}

	@Override
	public double getDefaultPowerUsage() {
		return USAGE_PER_TICK;
	}

	@Override
	public boolean isMuffled(boolean clientSide) {
		return clientSide ? clientMuffled : isMuffled;
	}

	@Override
	public double getCurrentSpeed(boolean clientSide) {
		return clientSide ? clientSpeed * clientSAMultipler : currSpeed * saMultiplier;
	}

	@Override
	public double getCurrentPowerStorage(boolean clientSide) {
		return clientSide ? clientEnergy.getMaxEnergyStored()
				: this.<CapabilityEnergyStorage>exposeCapability(CapabilityType.Energy).getMaxEnergyStored();
	}

	@Override
	public double getCurrentPowerUsage(boolean clientSide) {
		return clientSide ? clientEnergyUsage * clientSAMultipler : usage * saMultiplier;
	}

	@Override
	public void setSpeed(double speed) {
		currSpeed = speed;
	}

	@Override
	public void setPowerStorage(int storage) {
		CapabilityEnergyStorage energy = exposeCapability(CapabilityType.Energy);
		energy.updateMaxEnergyStorage(storage);
	}

	@Override
	public void setPowerUsage(int usage) {
		this.usage = usage;
	}

	@Override
	public void setMuffled(boolean muffled) {
		isMuffled = muffled;
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
		return (index, stack, cap) -> index == 0 && MatterRegister.INSTANCE.getServerMatterValue(stack) != null
				|| index == 1 && UtilsCapability.hasEnergyCap(stack)
				|| index > 1 && stack.getItem() instanceof ItemUpgrade;
	}

}
