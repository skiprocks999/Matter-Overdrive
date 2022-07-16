package matteroverdrive.common.tile.matter_network.matter_replicator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.MatterOverdrive;
import matteroverdrive.SoundRegister;
import matteroverdrive.common.block.machine.variants.BlockLightableMachine;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryMatterReplicator;
import matteroverdrive.common.item.ItemPatternDrive;
import matteroverdrive.common.item.tools.electric.ItemElectric;
import matteroverdrive.common.item.tools.electric.ItemMatterScanner;
import matteroverdrive.common.network.NetworkMatter;
import matteroverdrive.common.tile.matter_network.TileMatterNetworkCable;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.capability.types.matter.CapabilityMatterStorage;
import matteroverdrive.core.matter.MatterRegister;
import matteroverdrive.core.network.utils.IMatterNetworkMember;
import matteroverdrive.core.sound.SoundBarrierMethods;
import matteroverdrive.core.tile.types.GenericSoundTile;
import matteroverdrive.core.utils.UtilsDirection;
import matteroverdrive.core.utils.UtilsNbt;
import matteroverdrive.core.utils.UtilsTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.TriPredicate;

public class TileMatterReplicator extends GenericSoundTile implements IMatterNetworkMember {

	public static final int SLOT_COUNT = 10;
	
	private static final int USAGE_PER_TICK = 80;
	private static final float FAILURE_CHANCE = 0.005F;
	private static final int MATTER_STORAGE = 1024;
	private static final int ENERGY_STORAGE = 512000;
	private static final int DEFAULT_SPEED = 1;
	private static final double MATTER_MULTIPLIER = 92;
	private static final int SOUND_TICKS = 92;
	
	private boolean isPowered = false;
	private boolean isRunning = false;
	private List<QueuedReplication> orders = new ArrayList<>();
	private double currRecipeValue = 0;
	private double currProgress = 0;
	private double currSpeed = DEFAULT_SPEED;
	private float currFailureChance = FAILURE_CHANCE;
	private int usage = USAGE_PER_TICK;
	private boolean isMuffled = false;
	private QueuedReplication currentOrder = null;
	
	//Render data
	public boolean clientPowered;
	public boolean clientRunning;
	public CapabilityInventory clientInventory;
	public QueuedReplication clientCurrentOrder;
	private boolean clientMuffled;
	private boolean clientSoundPlaying = false; 
	
	//Menu data
	public CapabilityEnergyStorage clientEnergy;
	public CapabilityMatterStorage clientMatter;
	public List<QueuedReplication> clientOrders;
	public int clientEnergyUsage;
	public double clientRecipeValue;
	public double clientProgress;
	public double clientSpeed;
	public float clientFailure;
	
	//network data
	public CapabilityInventory clientNetworkInventory;
	public boolean clientNetworkPowered;
	public List<QueuedReplication> clientNetworkOrders;
	
	
	public TileMatterReplicator(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_MATTER_REPLICATOR.get(), pos, state);
		addCapability(new CapabilityInventory(SLOT_COUNT, true, true).setInputs(2).setOutputs(2).setEnergySlots(1)
				.setMatterSlots(1).setUpgrades(4).setOwner(this).setValidUpgrades(InventoryMatterReplicator.UPGRADES)
				.setValidator(getValidator()));
		addCapability(new CapabilityEnergyStorage(ENERGY_STORAGE, true, false).setOwner(this));
		addCapability(new CapabilityMatterStorage(MATTER_STORAGE, true, false).setOwner(this));
		setMenuProvider(
				new SimpleMenuProvider(
						(id, inv, play) -> new InventoryMatterReplicator(id, play.getInventory(),
								exposeCapability(CapabilityType.Item), getCoordsData()),
						getContainerName(TypeMachine.MATTER_REPLICATOR.id())));
		setTickable();
		setHasMenuData();
		setHasRenderData();
	}
	
	@Override
	public void tickServer() {
		removeCompletedOrders();
		if(!canRun()) {
			isRunning = false;
			isPowered = false;
			return;
		}
		
		CapabilityEnergyStorage energy = exposeCapability(CapabilityType.Energy);
		if(energy.getEnergyStored() < usage) {
			isRunning = false;
			isPowered = false;
			return;
		}
		isPowered = true;
		if(orders.size() <= 0) {
			isRunning = false;
			return;
		}
		
		currentOrder = orders.get(0);
		ItemStack stack = new ItemStack(currentOrder.getItem());
		Double value = MatterRegister.INSTANCE.getServerMatterValue(stack);
		if(value == null || value <= 0 || currentOrder == null || currentOrder.getPercentage() <= 0) {
			currentOrder.cancel();
			isRunning = false;
			return;
		}
		currRecipeValue = value;
		CapabilityInventory inv = exposeCapability(CapabilityType.Item);
		List<ItemStack> outputs = inv.getOutputs();
		ItemStack dust = outputs.get(1);
		boolean dustEmpty = dust.isEmpty();
		if(!dustEmpty && !(UtilsNbt.readMatterVal(dust) == value && dust.getCount() < dust.getMaxStackSize())) {
			isRunning = false;
			return;
		}
		
		ItemStack output = outputs.get(0);
		boolean outputEmpty = output.isEmpty();
		if(!outputEmpty && !(ItemStack.isSame(stack, output) && output.getCount() < output.getMaxStackSize())) {
			isRunning = false;
			return;
		}
		
		CapabilityMatterStorage matter = exposeCapability(CapabilityType.Matter);
		if(matter.getMatterStored() < currRecipeValue) {
			isRunning = false;
			return;
		}
		
		isRunning = true;
		currProgress += currSpeed;
		boolean currState = getLevel().getBlockState(getBlockPos()).getValue(BlockLightableMachine.LIT);
		if (currState && !isRunning) {
			UtilsTile.updateLit(this, Boolean.FALSE);
		} else if (!currState && isRunning) {
			UtilsTile.updateLit(this, Boolean.TRUE);
		}
		
		if(currProgress < currRecipeValue * MATTER_MULTIPLIER) {
			return;
		}
		
		currProgress = 0;
		currentOrder.decRemaining();
		float progToFloat = (float) currentOrder.getPercentage() / 100.0F;
		
		if(roll() < getCurrentFailure(false) / progToFloat) {
			if(dustEmpty) {
				ItemStack newDust = new ItemStack(DeferredRegisters.ITEM_RAW_MATTER_DUST.get());
				UtilsNbt.writeMatterVal(newDust, currRecipeValue);
				inv.setStackInSlot(3, newDust.copy());
			} else {
				dust.grow(1);
			}
			return;
		} 
		
		if(outputEmpty) {
			inv.setStackInSlot(2, stack.copy());
		} else {
			output.grow(1);
		}

	}
	
	@Override
	public void tickClient() {
		int adjustedTicks = getAdjustedTicks();
		boolean shouldPlaySound = shouldPlaySound();
		boolean adjustTicks = adjustedTicks <= SOUND_TICKS;
		boolean greaterThan = getProcessingTime() - clientProgress > SOUND_TICKS;
		boolean lessThanOne = clientRecipeValue < 1;
		if (shouldPlaySound && !clientSoundPlaying) {
			if(lessThanOne || adjustTicks) {
				clientSoundPlaying = true;
				SoundBarrierMethods.playTileSound(SoundRegister.SOUND_DECOMPOSER.get(), this, true);
			} else if(!greaterThan) {
				clientSoundPlaying = true;
				SoundBarrierMethods.playTileSound(SoundRegister.SOUND_DECOMPOSER.get(), this, false);
			}
		}
		if(clientSoundPlaying && shouldPlaySound && !lessThanOne && !adjustTicks && greaterThan) {
			setNotPlaying();
		}
	}
	
	private void removeCompletedOrders() {
		Iterator<QueuedReplication> it = orders.iterator();
		int oldSize = orders.size();
		QueuedReplication queued;
		while(it.hasNext()) {
			queued = it.next();
			if(queued.isFinished()) {
				it.remove();
			}
		}
		if(oldSize > orders.size()) {
			setChanged();
		}
	}

	@Override
	public void getMenuData(CompoundTag tag) {
		
		CapabilityEnergyStorage energy = exposeCapability(CapabilityType.Energy);
		tag.put(energy.getSaveKey(), energy.serializeNBT());
		CapabilityInventory inv = exposeCapability(CapabilityType.Item);
		tag.put(inv.getSaveKey(), inv.serializeNBT());
		CapabilityMatterStorage storage = exposeCapability(CapabilityType.Matter);
		tag.put(storage.getSaveKey(), storage.serializeNBT());
		int size = orders.size();
		tag.putInt("orderCount", size);
		for(int i = 0; i < size; i++) {
			orders.get(i).writeToNbt(tag, "order" + i);
		}
		tag.putInt("usage", usage);
		tag.putFloat("failure", currFailureChance);
		
	}
	
	@Override
	public void readMenuData(CompoundTag tag) {
		
		clientEnergy = new CapabilityEnergyStorage(0, false, false);
		clientEnergy.deserializeNBT(tag.getCompound(clientEnergy.getSaveKey()));
		clientInventory = new CapabilityInventory();
		clientInventory.deserializeNBT(tag.getCompound(clientInventory.getSaveKey()));
		clientMatter = new CapabilityMatterStorage(0, false, false);
		clientMatter.deserializeNBT(tag.getCompound(clientMatter.getSaveKey()));
		int orderSize = tag.getInt("orderCount");
		clientOrders = new ArrayList<>();
		for(int i = 0; i < orderSize; i++) {
			clientOrders.add(QueuedReplication.readFromNbt(tag.getCompound("order" + i)));
		}
		clientEnergyUsage = tag.getInt("usage");
		clientFailure = tag.getFloat("failure");
		
	}
	
	@Override
	public void getRenderData(CompoundTag tag) {
		tag.putBoolean("powered", isPowered);
		tag.putBoolean("running", isRunning);
		if(orders.size() > 0) {
			orders.get(0).writeToNbt(tag, "order");
		}
		tag.putDouble("progress", currProgress);
		tag.putDouble("recipe", currRecipeValue);
		tag.putDouble("speed", currSpeed);
	}
	
	@Override
	public void readRenderData(CompoundTag tag) {
		clientPowered = tag.getBoolean("powered");
		clientRunning = tag.getBoolean("running");
		if(tag.contains("order")) {
			clientCurrentOrder = QueuedReplication.readFromNbt(tag.getCompound("order"));
		}
		clientRecipeValue = tag.getDouble("recipe");
		clientProgress = tag.getDouble("progress");
		clientSpeed = tag.getDouble("speed");
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		CompoundTag data = new CompoundTag();
		
		int size = orders.size();
		data.putInt("orderCount", size);
		for(int i = 0; i < size; i++) {
			orders.get(i).writeToNbt(data, "order" + i);
		}
		
		data.putDouble("progress", currProgress);
		data.putDouble("speed", currSpeed);
		data.putFloat("failure", currFailureChance);
		data.putInt("usage", usage);
		data.putBoolean("muffled", isMuffled);
		
		tag.put("data", data);
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		CompoundTag data = tag.getCompound("data");
		
		int orderSize = data.getInt("orderCount");
		orders = new ArrayList<>();
		for(int i = 0; i < orderSize; i++) {
			orders.add(QueuedReplication.readFromNbt(data.getCompound("order" + i)));
		}
		
		currProgress = data.getDouble("progress");
		currSpeed = data.getDouble("speed");
		currFailureChance = data.getFloat("failure");
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
	public float getDefaultFailure() {
		return FAILURE_CHANCE;
	}

	@Override
	public double getDefaultMatterStorage() {
		return MATTER_STORAGE;
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
	public float getCurrentFailure(boolean clientSide) {
		return clientSide ? clientFailure * (float) clientSAMultipler : currFailureChance * (float) saMultiplier;
	}

	@Override
	public double getCurrentMatterStorage(boolean clientSide) {
		return clientSide ? clientMatter.getMaxMatterStored()
				: this.<CapabilityMatterStorage>exposeCapability(CapabilityType.Matter).getMaxMatterStored();
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
	public void setFailure(float failure) {
		currFailureChance = failure;
	}

	@Override
	public void setMatterStorage(double storage) {
		CapabilityMatterStorage matter = exposeCapability(CapabilityType.Matter);
		matter.updateMaxMatterStorage(storage);
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
		return clientRecipeValue * MATTER_MULTIPLIER;
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
	public boolean isPowered(boolean client, boolean network) {
		return client ? network ? clientNetworkPowered : clientPowered : isPowered;
	}
	
	public CompoundTag getNetworkData() {
		CompoundTag data = new CompoundTag();
		
		CapabilityInventory inv = exposeCapability(CapabilityType.Item);
		data.put(inv.getSaveKey(), inv.serializeNBT());
		data.putBoolean("ispowered", isPowered);
		
		int size = orders.size();
		data.putInt("orderCount", size);
		for(int i = 0; i < size; i++) {
			orders.get(i).writeToNbt(data, "order" + i);
		}
		
		return data;
	}
	
	public void handleNetworkData(CompoundTag tag) {
		
		clientNetworkInventory = new CapabilityInventory();
		clientNetworkInventory.deserializeNBT(tag.getCompound(clientNetworkInventory.getSaveKey()));
		clientNetworkPowered = tag.getBoolean("ispowered");
		
		int orderSize = tag.getInt("orderCount");
		clientNetworkOrders = new ArrayList<>();
		for(int i = 0; i < orderSize; i++) {
			clientNetworkOrders.add(QueuedReplication.readFromNbt(tag.getCompound("order" + i)));
		}
	}
	
	private float roll() {
		return MatterOverdrive.RANDOM.nextFloat();
	}
	
	private int getAdjustedTicks() {
		return (int) Math.ceil(getProcessingTime() / clientSpeed == 0 ? 1.0D : clientSpeed);
	}
	
	private static TriPredicate<Integer, ItemStack, CapabilityInventory> getValidator() {
		return (index, stack, cap) -> index < 7 && stack.getItem() instanceof ItemPatternDrive
				|| index == 7 && stack.getItem() instanceof ItemMatterScanner 
				|| index == 8 && stack.getItem() instanceof ItemElectric;
	}
	
}
