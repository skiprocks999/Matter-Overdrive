package matteroverdrive.common.tile.matter_network.matter_replicator;

import java.util.ArrayList;
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
import matteroverdrive.common.tile.matter_network.matter_replicator.utils.QueuedReplication;
import matteroverdrive.common.tile.matter_network.matter_replicator.utils.ReplicatorOrderManager;
import matteroverdrive.common.tile.matter_network.matter_replicator.utils.SoundHandlerReplicator;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.capability.types.item_pattern.ICapabilityItemPatternStorage;
import matteroverdrive.core.capability.types.matter.CapabilityMatterStorage;
import matteroverdrive.core.config.MatterOverdriveConfig;
import matteroverdrive.core.matter.MatterRegister;
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
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.TriPredicate;

public class TileMatterReplicator extends GenericMachineTile implements IMatterNetworkMember {

	public static final int SLOT_COUNT = 10;

	private static final int USAGE_PER_TICK = 80;
	private static final float FAILURE_CHANCE = 0.005F;
	private static final int MATTER_STORAGE = 1024;
	private static final int ENERGY_STORAGE = 512000;
	private static final int DEFAULT_SPEED = 1;
	private static final double MATTER_MULTIPLIER = 92;
	public static final int SOUND_TICKS = 92;
	public static final int NEEDED_PLATES = 5;

	public ReplicatorOrderManager orderManager = new ReplicatorOrderManager();
	private QueuedReplication currentOrder = QueuedReplication.EMPTY;
	private boolean usingFused = false;

	public final Property<CompoundTag> capInventoryProp;
	public final Property<CompoundTag> capEnergyStorageProp;
	public final Property<CompoundTag> capMatterStorageProp;
	public final Property<CompoundTag> currentOrderProp;
	public final Property<CompoundTag> ordersProp;

	// Client only
	private SoundHandlerReplicator soundHandler;

	public TileMatterReplicator(BlockPos pos, BlockState state) {
		super(TileRegistry.TILE_MATTER_REPLICATOR.get(), pos, state);

		setSpeed(DEFAULT_SPEED);
		setFailure(FAILURE_CHANCE);
		setPowerUsage(USAGE_PER_TICK);

		defaultSpeed = DEFAULT_SPEED;
		defaultFailureChance = FAILURE_CHANCE;
		defaultMatterStorage = MATTER_STORAGE;
		defaultPowerStorage = ENERGY_STORAGE;
		defaultPowerUsage = USAGE_PER_TICK;

		capInventoryProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.NBT
				.create(() -> getInventoryCap().serializeNBT(), tag -> getInventoryCap().deserializeNBT(tag)));
		capMatterStorageProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.NBT
				.create(() -> getMatterStorageCap().serializeNBT(), tag -> getMatterStorageCap().deserializeNBT(tag)));
		capEnergyStorageProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.NBT
				.create(() -> getEnergyStorageCap().serializeNBT(), tag -> getEnergyStorageCap().deserializeNBT(tag)));
		currentOrderProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.NBT
				.create(() -> currentOrder.writeToNbt(), tag -> currentOrder = QueuedReplication.readFromNbt(tag)));
		ordersProp = this.getPropertyManager()
				.addTrackedProperty(PropertyTypes.NBT.create(orderManager::serializeNbt, orderManager::deserializeNbt));

		addInventoryCap(new CapabilityInventory(SLOT_COUNT, true, true).setInputs(2).setOutputs(2)
				.setEnergyInputSlots(1).setMatterInputSlots(1).setUpgrades(4).setOwner(this)
				.setValidUpgrades(InventoryMatterReplicator.UPGRADES).setValidator(getValidator())
				.setPropertyManager(capInventoryProp));
		addEnergyStorageCap(new CapabilityEnergyStorage(ENERGY_STORAGE, true, false).setOwner(this)
				.setPropertyManager(capEnergyStorageProp));
		addMatterStorageCap(new CapabilityMatterStorage(MATTER_STORAGE, true, false).setOwner(this)
				.setPropertyManager(capMatterStorageProp));
		setMenuProvider(new SimpleMenuProvider((id, inv, play) -> new InventoryMatterReplicator(id, play.getInventory(),
				getInventoryCap(), getCoordsData()), getContainerName(TypeMachine.MATTER_REPLICATOR.id())));
		setTickable();

		orderManager.setVars(this, ordersProp);

	}

	// Not sure if I like this, but I have no idea how to get orders from each replicator to get a true
	// total count of all the orders.

	public ReplicatorOrderManager getOrderManager() {
		return orderManager;
	}

	@Override
	public void tickServer() {
		UtilsTile.drainElectricSlot(this);
		UtilsTile.drainMatterSlot(this);

		handleOnState();

		orderManager.removeCompletedOrders();
		if (!canRun()) {
			setShouldSaveData(setRunning(false), setPowered(false), setProgress(0),
					setCurrentOrder(QueuedReplication.EMPTY), updateTickable(false));
			return;
		}

		CapabilityEnergyStorage energy = getEnergyStorageCap();
		if (energy.getEnergyStored() < getCurrentPowerUsage()) {
			setShouldSaveData(setRunning(false), setPowered(false), setProgress(0),
					setCurrentOrder(QueuedReplication.EMPTY), updateTickable(false));
			return;
		}
		setPowered(true);

		CapabilityInventory inv = getInventoryCap();
		ItemStack drive = inv.getStackInSlot(0);
		if (drive.isEmpty()) {
			usingFused = false;
		} else {
			if (!usingFused && orderManager.size() > 0) {
				orderManager.wipeOrders();
			}
			if (orderManager.isEmpty()) {
				LazyOptional<ICapabilityItemPatternStorage> lazy = drive
						.getCapability(MatterOverdriveCapabilities.STORED_PATTERNS);
				if (lazy.isPresent()) {
					orderManager.addOrder(
							new QueuedReplication(((ICapabilityItemPatternStorage) (lazy.cast().resolve().get()))
									.getStoredPatterns()[getFuseIndex(drive)], 1));
				}
			}
			usingFused = true;
		}

		if (orderManager.isEmpty()) {
			setShouldSaveData(setRunning(false), setProgress(0), setCurrentOrder(QueuedReplication.EMPTY),
					updateTickable(false));
			return;
		}

		ItemStack stack = new ItemStack(orderManager.getOrder(0).getItem());
		double value = MatterRegister.INSTANCE.getServerMatterValue(stack);
		if (value <= 0.0 || orderManager.getOrder(0).getPercentage() <= 0) {
			orderManager.cancelOrder(0);
			setShouldSaveData(setRunning(false), setProgress(0), setCurrentOrder(QueuedReplication.EMPTY),
					updateTickable(false));
			return;
		}
		setRecipeValue(value);
		currentOrderProp.set(orderManager.getOrder(0).writeToNbt());
		setChanged();
		List<ItemStack> outputs = inv.getOutputs();
		ItemStack dust = outputs.get(1);
		boolean dustEmpty = dust.isEmpty();
		if (!dustEmpty && !(UtilsNbt.readMatterVal(dust) == value && dust.getCount() < dust.getMaxStackSize())) {
			setShouldSaveData(setRunning(false), updateTickable(false));
			return;
		}

		ItemStack output = outputs.get(0);
		boolean outputEmpty = output.isEmpty();
		if (!outputEmpty && !(ItemStack.isSame(stack, output) && output.getCount() < output.getMaxStackSize())) {
			setShouldSaveData(setRunning(false), updateTickable(false));
			return;
		}

		CapabilityMatterStorage matter = getMatterStorageCap();
		if (matter.getMatterStored() < getRecipeValue()) {
			setShouldSaveData(setRunning(false), updateTickable(false));
			return;
		}

		setRunning(true);
		incrementProgress(getCurrentSpeed());

		energy.removeEnergy((int) getCurrentPowerUsage());
		int plateCount = inv.getStackInSlot(1).getCount();
		if (plateCount < NEEDED_PLATES) {
			int radius = NEEDED_PLATES - plateCount;
			List<LivingEntity> surroundEntities = getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(
					getBlockPos().offset(radius, radius, radius), getBlockPos().offset(-radius, -radius, -radius)));
			if (surroundEntities != null) {
				for (LivingEntity entity : surroundEntities) {
					entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 1, false, true, false));
				}
			}
		}

		setShouldSaveData(true);

		if (getProgress() < getRecipeValue() * MATTER_MULTIPLIER) {
			return;
		}

		setProgress(0);
		orderManager.decRemaining(0);
		float progToFloat = (float) orderManager.getOrder(0).getPercentage() / 100.0F;
		setCurrentOrder(orderManager.getOrder(0));
		if (roll() < getCurrentFailure() / progToFloat) {
			if (dustEmpty) {
				ItemStack newDust = new ItemStack(ItemRegistry.ITEM_RAW_MATTER_DUST.get());
				UtilsNbt.writeMatterVal(newDust, getRecipeValue());
				inv.setStackInSlot(3, newDust.copy());
			} else {
				dust.grow(1);
			}
			return;
		}

		if (outputEmpty) {
			inv.setStackInSlot(2, stack.copy());
		} else {
			output.grow(1);
		}

		matter.removeMatter(getRecipeValue());
		setCurrentOrder(QueuedReplication.EMPTY);
		setRecipeValue(0);
		setShouldSaveData(true);

	}

	@Override
	public void tickClient() {
		if (soundHandler == null) {
			soundHandler = new SoundHandlerReplicator(this);
		}
		soundHandler.tick(getAdjustedTicks(), clientSoundPlaying);
		QueuedReplication currentOrder = getCurrentOrder();
		if (isRunning() && !currentOrder.isEmpty()) {
			if (MatterOverdriveConfig.MATTER_REPLICATOR_ITEM_PARTICLES.get()) {
				Level world = getLevel();
				BlockPos blockPos = getBlockPos();
				ItemEntity entity = new ItemEntity(world, blockPos.getX() + 0.5D, blockPos.getY() + 0.25,
						blockPos.getZ() + 0.5D, new ItemStack(currentOrder.getItem()));
				float progress = (float) getProgress() / (float) (getProcessingTime() == 0 ? 1 : getProcessingTime());
				Vector3f vec = new Vector3f((float) entity.getX(), (float) entity.getY(), (float) entity.getZ());
				double entityRadius = entity.getBbWidth();
				Random random = MatterOverdrive.RANDOM;
				double time = Math.min(progress, 1);
				float gravity = 0.1f;
				int count = (int) (progress * 100.0F);
				time = 1 - time;

				for (int i = 0; i < count; i++) {
					float speed = 0.05F;
					float height = vec.y() + random.nextFloat() * entity.getBbHeight();

					Vector3f origin = new Vector3f(vec.x(), height, vec.z());
					Vector3f offset = UtilsMath.randomCirclePoint((float) entityRadius / 1.5F, MatterOverdrive.RANDOM);
					Vector3f pos = new Vector3f(origin.x() + offset.x(), origin.y(), origin.z() + offset.z());

					world.addParticle(new ParticleOptionReplicator().setGravity(gravity).setScale(0.01F).setAge(2),
							pos.x(), pos.y(), pos.z(), 0, speed, 0);
				}
			}
			if (MatterOverdriveConfig.MATTER_REPLICATOR_VENT_PARTICLES.get()) {
				// left of block
				if (MatterOverdrive.RANDOM.nextFloat() < 0.2F) {
					Vector3f pos = UtilsMath.moveToEdgeOfFaceAndCenter(getFacing().getClockWise(), getBlockPos());
					UtilsParticle.spawnVentParticlesAtFace(pos, 0.03F, getFacing().getClockWise(), 1);
				}
				// right of block
				if (MatterOverdrive.RANDOM.nextFloat() < 0.2F) {
					Vector3f pos = UtilsMath.moveToEdgeOfFaceAndCenter(getFacing().getCounterClockWise(),
							getBlockPos());
					UtilsParticle.spawnVentParticlesAtFace(pos, 0.03F, getFacing().getCounterClockWise(), 1);
				}
			}
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		CompoundTag data = new CompoundTag();

		data.put("orders", ordersProp.get());
		data.putBoolean("fused", usingFused);
		data.put("currorder", currentOrder.writeToNbt());

		tag.put("data", data);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		CompoundTag data = tag.getCompound("data");

		ordersProp.set(data.getCompound("orders"));
		usingFused = data.getBoolean("fused");
		currentOrderProp.set(data.getCompound("currorder"));
	}

	@Override
	public double getProcessingTime() {
		return getRecipeValue() * MATTER_MULTIPLIER;
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
		return isPowered();
	}

	public CompoundTag getNetworkData() {
		CompoundTag data = new CompoundTag();

		CapabilityInventory inv = getInventoryCap();
		data.put(inv.getSaveKey(), inv.serializeNBT());
		data.putBoolean("ispowered", isPowered());

		int size = orderManager.size();
		data.putInt("orderCount", size);
		QueuedReplication replication;
		for (int i = 0; i < size; i++) {
			replication = orderManager.getAllOrders().get(i);
			replication.setOwnerLoc(getBlockPos());
			replication.setQueuePos(i);
			data.put("order" + i, replication.writeToNbt());
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
		for (int i = 0; i < orderSize; i++) {
			orders.add(QueuedReplication.readFromNbt(tag.getCompound("order" + i)));
		}
		return new MatterReplicatorDataWrapper(inv, powered, orders, tag.getBoolean("fused"));
	}

	private float roll() {
		return MatterOverdrive.RANDOM.nextFloat();
	}

	private int getAdjustedTicks() {
		return (int) Math.ceil(getProcessingTime() / (getCurrentSpeed() == 0 ? 1.0D : getCurrentSpeed()));
	}

	public void setSoundPlaying() {
		clientSoundPlaying = true;
	}

	public boolean isFused() {
		return usingFused;
	}

	public QueuedReplication getCurrentOrder() {
		return QueuedReplication.readFromNbt(currentOrderProp.get());
	}

	public boolean setCurrentOrder(QueuedReplication replication) {
		currentOrderProp.set(replication.writeToNbt());
		return currentOrderProp.isDirtyNoUpdate();
	}

	public int getFuseIndex(ItemStack drive) {
		return drive.getOrCreateTag().getInt(UtilsNbt.INDEX);
	}

	private static TriPredicate<Integer, ItemStack, CapabilityInventory> getValidator() {
		return (index, stack,
				cap) -> index == 0 && stack.getItem() instanceof ItemPatternDrive drive && drive.isFused(stack)
						|| index == 1
								&& UtilsItem.compareItems(stack.getItem(), ItemRegistry.ITEM_TRITANIUM_PLATE.get())
						|| index == 4 && UtilsCapability.hasEnergyCap(stack)
						|| index == 5 && UtilsCapability.hasMatterCap(stack)
						|| index > 5 && stack.getItem() instanceof ItemUpgrade;
	}

	public static class MatterReplicatorDataWrapper {

		private CapabilityInventory inv;
		private boolean isPowered;
		private List<QueuedReplication> orders;
		private boolean isFused;

		public MatterReplicatorDataWrapper(CapabilityInventory inv, boolean isPowered,
				@Nullable List<QueuedReplication> orders, boolean fused) {
			this.inv = inv;
			this.isPowered = isPowered;
			if (orders == null) {
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

		public List<QueuedReplication> getOrders() {
			return orders;
		}

		public boolean isFused() {
			return isFused;
		}

	}

}
