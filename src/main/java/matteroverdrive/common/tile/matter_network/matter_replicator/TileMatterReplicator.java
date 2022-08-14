package matteroverdrive.common.tile.matter_network.matter_replicator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.mojang.math.Vector3f;

import matteroverdrive.MatterOverdrive;
import matteroverdrive.client.particle.replicator.ParticleOptionReplicator;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryMatterReplicator;
import matteroverdrive.common.item.ItemPatternDrive;
import matteroverdrive.common.item.ItemUpgrade;
import matteroverdrive.common.network.NetworkMatter;
import matteroverdrive.common.tile.matter_network.TileMatterNetworkCable;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.capability.types.item_pattern.ICapabilityItemPatternStorage;
import matteroverdrive.core.capability.types.matter.CapabilityMatterStorage;
import matteroverdrive.core.matter.MatterRegister;
import matteroverdrive.core.network.utils.IMatterNetworkMember;
import matteroverdrive.core.tile.types.GenericSoundTile;
import matteroverdrive.core.utils.UtilsCapability;
import matteroverdrive.core.utils.UtilsDirection;
import matteroverdrive.core.utils.UtilsItem;
import matteroverdrive.core.utils.UtilsMath;
import matteroverdrive.core.utils.UtilsNbt;
import matteroverdrive.core.utils.UtilsTile;
import matteroverdrive.registry.ItemRegistry;
import matteroverdrive.registry.TileRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.TriPredicate;

public class TileMatterReplicator extends GenericSoundTile implements IMatterNetworkMember {

	public static final int SLOT_COUNT = 10;
	
	private static final int USAGE_PER_TICK = 80;
	private static final float FAILURE_CHANCE = 0.005F;
	private static final int MATTER_STORAGE = 1024;
	private static final int ENERGY_STORAGE = 512000;
	private static final int DEFAULT_SPEED = 1;
	private static final double MATTER_MULTIPLIER = 92;
	public static final int SOUND_TICKS = 92;
	public static final int NEEDED_PLATES = 5;
	
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
	private boolean usingFused = false;
	
	//Render data
	public boolean clientPowered;
	public boolean clientRunning = false;
	public CapabilityInventory clientInventory;
	public QueuedReplication clientCurrentOrder;
	private boolean clientMuffled;
	private boolean clientSoundPlaying = false; 
	private SoundHandlerReplicator soundHandler;
	
	//Menu data
	public CapabilityEnergyStorage clientEnergy;
	public CapabilityMatterStorage clientMatter;
	public List<QueuedReplication> clientOrders;
	public int clientEnergyUsage;
	public double clientRecipeValue;
	public double clientProgress;
	public double clientSpeed;
	public float clientFailure;
	public ItemStack outputItem = ItemStack.EMPTY;
	
	public TileMatterReplicator(BlockPos pos, BlockState state) {
		super(TileRegistry.TILE_MATTER_REPLICATOR.get(), pos, state);
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
		UtilsTile.drainElectricSlot(this);
		UtilsTile.drainMatterSlot(this);
		removeCompletedOrders();
		boolean currState = getLevel().getBlockState(getBlockPos()).getValue(BlockStateProperties.LIT);
		if(!canRun()) {
			isRunning = false;
			isPowered = false;
			currProgress = 0;
			if (currState && !isRunning) {
				UtilsTile.updateLit(this, Boolean.FALSE);
			}
			return;
		}
		
		CapabilityEnergyStorage energy = exposeCapability(CapabilityType.Energy);
		if(energy.getEnergyStored() < usage) {
			isRunning = false;
			isPowered = false;
			currProgress = 0;
			if (currState && !isRunning) {
				UtilsTile.updateLit(this, Boolean.FALSE);
			}
			return;
		}
		isPowered = true;
		
		CapabilityInventory inv = exposeCapability(CapabilityType.Item);
		ItemStack drive = inv.getStackInSlot(0);
		if(drive.isEmpty()) {
			usingFused = false;
		} else {
			if(!usingFused && orders.size() > 0) {
				orders.clear();
			}
			if(orders.isEmpty()) {
				LazyOptional<ICapabilityItemPatternStorage> lazy = drive.getCapability(MatterOverdriveCapabilities.STORED_PATTERNS);
				if(lazy.isPresent()) {
					orders.add(new QueuedReplication(((ICapabilityItemPatternStorage)(lazy.cast().resolve().get())).getStoredPatterns()[0], 1));
				}
			} 
			usingFused = true;
		}
		
		if(orders.size() <= 0) {
			isRunning = false;
			currProgress = 0;
			if (currState && !isRunning) {
				UtilsTile.updateLit(this, Boolean.FALSE);
			}
			return;
		}
		currentOrder = orders.get(0);
		
		ItemStack stack = new ItemStack(currentOrder.getItem());
		double value = MatterRegister.INSTANCE.getServerMatterValue(stack);
		if(value <= 0.0 || currentOrder == null || currentOrder.getPercentage() <= 0) {
			currentOrder.cancel();
			isRunning = false;
			currProgress = 0;
			if (currState && !isRunning) {
				UtilsTile.updateLit(this, Boolean.FALSE);
			}
			return;
		}
		currRecipeValue = value;
		
		List<ItemStack> outputs = inv.getOutputs();
		ItemStack dust = outputs.get(1);
		boolean dustEmpty = dust.isEmpty();
		if(!dustEmpty && !(UtilsNbt.readMatterVal(dust) == value && dust.getCount() < dust.getMaxStackSize())) {
			isRunning = false;
			if (currState && !isRunning) {
				UtilsTile.updateLit(this, Boolean.FALSE);
			}
			return;
		}
		
		ItemStack output = outputs.get(0);
		boolean outputEmpty = output.isEmpty();
		if(!outputEmpty && !(ItemStack.isSame(stack, output) && output.getCount() < output.getMaxStackSize())) {
			isRunning = false;
			if (currState && !isRunning) {
				UtilsTile.updateLit(this, Boolean.FALSE);
			}
			return;
		}
		
		CapabilityMatterStorage matter = exposeCapability(CapabilityType.Matter);
		if(matter.getMatterStored() < currRecipeValue) {
			isRunning = false;
			if (currState && !isRunning) {
				UtilsTile.updateLit(this, Boolean.FALSE);
			}
			return;
		}
		
		isRunning = true;
		currProgress += currSpeed;
		
		energy.removeEnergy(usage);
		int plateCount = inv.getStackInSlot(1).getCount();
		if(plateCount < NEEDED_PLATES) {
			int radius = NEEDED_PLATES - plateCount;
			List<LivingEntity> surroundEntities = getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(getBlockPos().offset(radius, radius, radius), getBlockPos().offset(-radius, -radius, -radius)));
			if(surroundEntities != null) {
				for(LivingEntity entity : surroundEntities) {
					entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 1, false, true, false));
				}
			}
		}
		
		if (!currState && isRunning) {
			UtilsTile.updateLit(this, Boolean.TRUE);
		}
		
		setChanged();
		
		
		
		if(currProgress < currRecipeValue * MATTER_MULTIPLIER) {
			return;
		}
		
		currProgress = 0;
		currentOrder.decRemaining();
		float progToFloat = (float) currentOrder.getPercentage() / 100.0F;
		
		if(roll() < getCurrentFailure(false) / progToFloat) {
			if(dustEmpty) {
				ItemStack newDust = new ItemStack(ItemRegistry.ITEM_RAW_MATTER_DUST.get());
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
		
		matter.removeMatter(currRecipeValue);
		currentOrder = null;
		currRecipeValue = 0;
		setChanged();

	}
	
	@Override
	public void tickClient() {
		if(soundHandler == null) {
			soundHandler = new SoundHandlerReplicator(this);
		}
		soundHandler.tick(getAdjustedTicks(), clientSoundPlaying);
		if(clientRunning && clientCurrentOrder != null) {
			Level world = getLevel();
			BlockPos blockPos = getBlockPos();
			ItemEntity entity = new ItemEntity(world, blockPos.getX() + 0.5D, blockPos.getY() + 0.25, blockPos.getZ() + 0.5D, new ItemStack(clientCurrentOrder.getItem()));
			float progress = (float) clientProgress / (float) (getProcessingTime() == 0 ? 1 : getProcessingTime());
			Vector3f vec = new Vector3f((float) entity.getX(), (float) entity.getY(), (float) entity.getZ());
			double entityRadius = entity.getBbWidth();
			Random random = MatterOverdrive.RANDOM;
			double time = Math.min(progress, 1);
			float gravity = 0.1f;
			int count = 100;
			time = 1 - time;

			for (int i = 0; i < count; i++) {
				float speed = 0.05F; 
				float height = vec.y() + random.nextFloat() * entity.getBbHeight();

				Vector3f origin = new Vector3f(vec.x(), height, vec.z());
				Vector3f offset = UtilsMath.randomCirclePoint((float) entityRadius / 1.5F);
				Vector3f pos = new Vector3f(origin.x() + offset.x(), origin.y(), origin.z() + offset.z());
			
				world.addParticle(new ParticleOptionReplicator()
						.setGravity(gravity).setScale(0.01F).setAge(2), pos.x(), pos.y(), pos.z(), 0, speed, 0);
			}
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
		QueuedReplication queued;
		for(int i = 0; i < size; i++) {
			queued = orders.get(i);
			queued.setOwnerLoc(getBlockPos());
			queued.setQueuePos(i);
			queued.writeToNbt(tag, "order" + i);
		}
		tag.putInt("usage", usage);
		tag.putFloat("failure", currFailureChance);
		tag.putDouble("sabonus", saMultiplier);
		
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
		clientSAMultipler = tag.getDouble("sabonus");
		
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
		tag.putBoolean("muffled", isMuffled);
		CapabilityInventory inv = exposeCapability(CapabilityType.Item);
		CompoundTag item = new CompoundTag();
		inv.getStackInSlot(2).save(item);
		tag.put("item", item);
	}
	
	@Override
	public void readRenderData(CompoundTag tag) {
		clientPowered = tag.getBoolean("powered");
		clientRunning = tag.getBoolean("running");
		if(tag.contains("order")) {
			clientCurrentOrder = QueuedReplication.readFromNbt(tag.getCompound("order"));
		} else {
			clientCurrentOrder = null;
		}
		clientRecipeValue = tag.getDouble("recipe");
		clientProgress = tag.getDouble("progress");
		clientSpeed = tag.getDouble("speed");
		clientMuffled = tag.getBoolean("muffled");
		outputItem = ItemStack.of(tag.getCompound("item"));
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
		data.putBoolean("fused", usingFused);
		
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
		usingFused = data.getBoolean("fused");
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
	public double getDefaultSpeed() {
		return DEFAULT_SPEED;
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
	public boolean isPowered(boolean client) {
		return client ? clientPowered : isPowered;
	}
	
	public CompoundTag getNetworkData() {
		CompoundTag data = new CompoundTag();
		
		CapabilityInventory inv = exposeCapability(CapabilityType.Item);
		data.put(inv.getSaveKey(), inv.serializeNBT());
		data.putBoolean("ispowered", isPowered);
		
		int size = orders.size();
		data.putInt("orderCount", size);
		QueuedReplication replication;
		for(int i = 0; i < size; i++) {
			replication = orders.get(i);
			replication.setOwnerLoc(getBlockPos());
			replication.setQueuePos(i);
			replication.writeToNbt(data, "order" + i);
		}
		data.putBoolean("fused", usingFused);
		
		return data;
	}
	
	public MatterReplicatorDataWrapper handleNetworkData(CompoundTag tag) {
		CapabilityInventory inv = new CapabilityInventory();
		inv.deserializeNBT(tag.getCompound(inv.getSaveKey()));
		boolean powered = tag.getBoolean("ispowered");
		
		int orderSize = tag.getInt("orderCount");
		List<QueuedReplication> orders = new ArrayList<>();
		for(int i = 0; i < orderSize; i++) {
			orders.add(QueuedReplication.readFromNbt(tag.getCompound("order" + i)));
		}
		return new MatterReplicatorDataWrapper(inv, powered, orders, tag.getBoolean("fused"));
	}
	
	private float roll() {
		return MatterOverdrive.RANDOM.nextFloat();
	}
	
	private int getAdjustedTicks() {
		return (int) Math.ceil(getProcessingTime() / (clientSpeed == 0 ? 1.0D : clientSpeed));
	}
	
	public int getCurrOrders() {
		return orders == null ? 0 : orders.size();
	}
	
	//Serverside
	public void queueOrder(QueuedReplication replication) {
		if(orders == null) {
			orders = new ArrayList<>();
		}
		orders.add(replication);
		setChanged();
	}
	
	public void cancelOrder(int index) {
		orders.get(index).cancel();
		currProgress = 0;
		currentOrder = null;
		setChanged();
	}
	
	public void setSoundPlaying() {
		clientSoundPlaying = true;
	}
	
	public boolean isFused() {
		return usingFused;
	}
	
	private static TriPredicate<Integer, ItemStack, CapabilityInventory> getValidator() {
		return (index, stack, cap) -> index == 0 && stack.getItem() instanceof ItemPatternDrive drive && drive.isFused(stack)
				|| index == 1 && UtilsItem.compareItems(stack.getItem(), ItemRegistry.ITEM_TRITANIUM_PLATE.get())
				|| index == 4 && UtilsCapability.hasEnergyCap(stack)
				|| index == 5 && UtilsCapability.hasMatterCap(stack)
				|| index > 5 && stack.getItem() instanceof ItemUpgrade;
	}
	
	public static class MatterReplicatorDataWrapper {
		
		private CapabilityInventory inv;
		private boolean isPowered;
		private List<QueuedReplication> orders;
		private boolean isFused;
		
		public MatterReplicatorDataWrapper(CapabilityInventory inv, boolean isPowered, @Nullable List<QueuedReplication> orders,
				boolean fused) {
			this.inv = inv;
			this.isPowered = isPowered;
			if(orders == null) {
				orders = new ArrayList<>();
			} 
			this.orders = orders;
			this.isFused = fused;
		}
		
		public CapabilityInventory getInventory() {
			return inv;
		}
		
		public boolean isPowered() {
			return isPowered;
		}
		
		public List<QueuedReplication> getOrders(){
			return orders;
		}
		
		public boolean isFused() {
			return isFused;
		}
		
	}
	
}
