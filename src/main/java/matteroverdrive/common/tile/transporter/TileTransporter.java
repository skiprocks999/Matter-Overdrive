package matteroverdrive.common.tile.transporter;

import java.util.List;
import java.util.Random;

import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;

import matteroverdrive.MatterOverdrive;
import matteroverdrive.client.particle.replicator.ParticleOptionReplicator;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.event.ServerEventHandler;
import matteroverdrive.common.inventory.InventoryTransporter;
import matteroverdrive.common.item.ItemUpgrade;
import matteroverdrive.common.item.tools.ItemTransporterFlashdrive;
import matteroverdrive.common.item.tools.electric.ItemCommunicator;
import matteroverdrive.common.tile.transporter.utils.ActiveTransportDataWrapper;
import matteroverdrive.common.tile.transporter.utils.EntityDataWrapper;
import matteroverdrive.common.tile.transporter.utils.TransporterDimensionManager;
import matteroverdrive.common.tile.transporter.utils.TransporterEntityDataManager;
import matteroverdrive.common.tile.transporter.utils.TransporterLocationManager;
import matteroverdrive.common.tile.transporter.utils.TransporterLocationWrapper;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.capability.types.matter.CapabilityMatterStorage;
import matteroverdrive.core.config.MatterOverdriveConfig;
import matteroverdrive.core.property.Property;
import matteroverdrive.core.property.PropertyTypes;
import matteroverdrive.core.sound.SoundBarrierMethods;
import matteroverdrive.core.tile.types.GenericMachineTile;
import matteroverdrive.core.utils.UtilsCapability;
import matteroverdrive.core.utils.UtilsMath;
import matteroverdrive.core.utils.UtilsTile;
import matteroverdrive.registry.SoundRegistry;
import matteroverdrive.registry.TileRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.TriPredicate;
import net.minecraftforge.server.ServerLifecycleHooks;

public class TileTransporter extends GenericMachineTile {

	public static final int SLOT_COUNT = 10;

	public static final int ENTITIES_PER_BATCH = 3;

	public static final int BUILD_UP_TIME = 70;
	private static final int USAGE_PER_TICK = 80;
	private static final int MATTER_STORAGE = 512;
	public static final int MATTER_USAGE = 25;
	private static final int ENERGY_STORAGE = 1024000;
	private static final int DEFAULT_SPEED = 1;
	private static final int DEFAULT_RADIUS = 32;

	private int cooldownTimer = 0;
	private int currDestination = -1;
	public TransporterLocationManager locationManager = new TransporterLocationManager(5);
	public TransporterEntityDataManager entityDataManager = new TransporterEntityDataManager();
	private boolean isReciever = false;
	
	public static final TransporterDimensionManager MANAGER = new TransporterDimensionManager();

	public final Property<CompoundTag> capInventoryProp;
	public final Property<CompoundTag> capEnergyStorageProp;
	public final Property<CompoundTag> capMatterStorageProp;
	public final Property<Integer> cooldownProp;
	public final Property<Integer> destinationProp;
	public final Property<CompoundTag> locationManagerProp;
	public final Property<CompoundTag> entityDataProp;
	public final Property<Boolean> recieverProp;

	public TileTransporter(BlockPos pos, BlockState state) {
		super(TileRegistry.TILE_TRANSPORTER.get(), pos, state);

		setMatterUsage(MATTER_USAGE);
		setSpeed(DEFAULT_SPEED);
		setPowerUsage(USAGE_PER_TICK);
		setRange(DEFAULT_RADIUS);
		setProcessingTime(BUILD_UP_TIME);

		defaultSpeed = DEFAULT_SPEED;
		defaultMatterUsage = MATTER_USAGE;
		defaultMatterStorage = MATTER_STORAGE;
		defaultPowerStorage = ENERGY_STORAGE;
		defaultPowerUsage = USAGE_PER_TICK;
		defaultRange = DEFAULT_RADIUS;

		capInventoryProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.NBT
				.create(() -> getInventoryCap().serializeNBT(), tag -> getInventoryCap().deserializeNBT(tag)));
		capMatterStorageProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.NBT
				.create(() -> getMatterStorageCap().serializeNBT(), tag -> getMatterStorageCap().deserializeNBT(tag)));
		capEnergyStorageProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.NBT
				.create(() -> getEnergyStorageCap().serializeNBT(), tag -> getEnergyStorageCap().deserializeNBT(tag)));
		cooldownProp = this.getPropertyManager()
				.addTrackedProperty(PropertyTypes.INTEGER.create(() -> cooldownTimer, timer -> cooldownTimer = timer));
		destinationProp = this.getPropertyManager().addTrackedProperty(
				PropertyTypes.INTEGER.create(() -> currDestination, dest -> currDestination = dest));
		locationManagerProp = this.getPropertyManager().addTrackedProperty(
				PropertyTypes.NBT.create(locationManager::serializeNbt, locationManager::deserializeNbt));
		entityDataProp = this.getPropertyManager().addTrackedProperty(
				PropertyTypes.NBT.create(entityDataManager::serializeNbt, entityDataManager::deserializeNbt));
		recieverProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.BOOLEAN.create(() -> isReciever, recieve -> isReciever = recieve));

		addInventoryCap(new CapabilityInventory(SLOT_COUNT, true, true).setInputs(2).setOutputs(1).setEnergyInputSlots(1).setMatterInputSlots(1)
				.setUpgrades(5).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.SOUTH }, new Direction[] { Direction.DOWN })
				.setValidator(getValidator()).setValidUpgrades(InventoryTransporter.UPGRADES)
				.setPropertyManager(capInventoryProp));
		addEnergyStorageCap(new CapabilityEnergyStorage(ENERGY_STORAGE, true, false).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.WEST, Direction.EAST }, null)
				.setPropertyManager(capEnergyStorageProp));
		addMatterStorageCap(new CapabilityMatterStorage(MATTER_STORAGE, true, false).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.NORTH, Direction.EAST, Direction.WEST }, null)
				.setPropertyManager(capMatterStorageProp));
		setMenuProvider(new SimpleMenuProvider((id, inv, play) -> new InventoryTransporter(id, play.getInventory(),
				getInventoryCap(), getCoordsData()), getContainerName(TypeMachine.TRANSPORTER.id())));
		setTickable();

		locationManager.setVars(locationManagerProp, this);
		entityDataManager.setVars(entityDataProp, this);

	}

	@Override
	public void tickServer() {
		UtilsTile.drainElectricSlot(this);
		UtilsTile.drainMatterSlot(this);
		
		CapabilityInventory inv = getInventoryCap();
		
		ItemStack stack = inv.getStackInSlot(0);
		
		if(stack.getItem() instanceof ItemCommunicator communicator && inv.getStackInSlot(2).isEmpty()) {
			communicator.writeDimension(stack, level.dimension());
			communicator.bindCoordinates(stack, getBlockPos().above());
			inv.setStackInSlot(2, stack.copy());
			inv.setStackInSlot(0, ItemStack.EMPTY);
		}
		
		if (!canRun()) {
			setShouldSaveData(setRunning(false), setProgress(0), entityDataManager.wipe(), updateTickable(false));
			return;
		}

		if (cooldownProp.get() < MatterOverdriveConfig.TRANSPORTER_COOLDOWN.get()) {
			cooldownProp.set(cooldownProp.get() + 1);
			setShouldSaveData(setRunning(false), setProgress(0), entityDataManager.wipe());
			return;
		}

		if(isReciever) {
			return;
		}
		
		List<Entity> currentEntities = level.getEntitiesOfClass(Entity.class, new AABB(getBlockPos().above()));

		if (currentEntities.size() <= 0 || destinationProp.get() < 0) {
			setShouldSaveData(setRunning(false), setProgress(0), entityDataManager.wipe(), updateTickable(false));
			return;
		}

		TransporterLocationWrapper curLoc = locationManager.getLocation(destinationProp.get());
		Pair<Boolean, Integer> validData = validDestination(curLoc);

		if (!validData.getFirst()) {
			setShouldSaveData(setRunning(false), setProgress(0), entityDataManager.wipe(), updateTickable(false));
			return;
		}

		CapabilityEnergyStorage energy = getEnergyStorageCap();
		if (energy.getEnergyStored() < getCurrentPowerUsage()) {
			setShouldSaveData(setRunning(false), entityDataManager.wipe(), updateTickable(false));
			return;
		}

		CapabilityMatterStorage matter = getMatterStorageCap();
		if (matter.getMatterStored() < getCurrentMatterUsage()) {
			setShouldSaveData(setRunning(false), entityDataManager.wipe(), updateTickable(false));
			return;
		}

		int size = currentEntities.size() >= ENTITIES_PER_BATCH ? ENTITIES_PER_BATCH : currentEntities.size();
		currentEntities = currentEntities.subList(0, size);
		energy.removeEnergy((int) getCurrentPowerUsage());
		setRunning(true);
		incrementProgress(getCurrentSpeed());
		entityDataManager.setEntities(currentEntities);

		if (getProgress() >= BUILD_UP_TIME) {
			cooldownProp.set(0);
			matter.removeMatter(getCurrentMatterUsage());
			setProgress(0);
			double x = curLoc.getDestination().getX() + 0.5;
			double y = curLoc.getDestination().getY();
			double z = curLoc.getDestination().getZ() + 0.5;
			for (Entity entity : currentEntities) {
				ServerLevel dim = handleDimensionChange(entity);
				entity.teleportToWithTicket(x, y, z);
				entity.getCapability(MatterOverdriveCapabilities.ENTITY_DATA).ifPresent(h -> {
					h.setTransporterTimer(BUILD_UP_TIME);
				});
				level.getCapability(MatterOverdriveCapabilities.OVERWORLD_DATA).ifPresent(h -> {
					h.addActiveTransport(new ActiveTransportDataWrapper(entity.getUUID(), BUILD_UP_TIME, dim.dimension()));
				});
				ServerEventHandler.TASK_HANDLER.queueTask(() -> {
					dim.playSound(null, curLoc.getDestination(), SoundRegistry.SOUND_TRANSPORTER_ARRIVE.get(),
							SoundSource.BLOCKS, 1.0F, 1.0F);
				});
			}
		}
		setShouldSaveData(true);
	}

	@Override
	public void tickClient() {
		if (shouldPlaySound() && !clientSoundPlaying) {
			clientSoundPlaying = true;
			SoundBarrierMethods.playTileSound(SoundRegistry.SOUND_TRANSPORTER.get(), this, false);
		}

		if (getProgress() > 0 && isRunning()) {
			int particlesPerTick = (int) ((getProgress() / (double) BUILD_UP_TIME) * 20);
			for (int i = 0; i < particlesPerTick; i++) {
				for (EntityDataWrapper entityData : entityDataManager.getEntityData()) {
					handleParticles(entityData, new Vector3f((float) entityData.xPos(), (float) getBlockPos().getY(),
							(float) entityData.zPos()));
				}
			}
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);

		CompoundTag additional = new CompoundTag();
		additional.putInt("cooldown", cooldownProp.get());
		additional.putInt("dest", destinationProp.get());
		additional.put("locations", locationManager.serializeNbt());
		additional.putBoolean("reciever", recieverProp.get());

		tag.put("additional", additional);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);

		CompoundTag additional = tag.getCompound("additional");
		cooldownProp.set(additional.getInt("cooldown"));
		destinationProp.set(additional.getInt("dest"));
		locationManagerProp.set(additional.getCompound("locations"));
		recieverProp.set(additional.getBoolean("reciever"));
	}

	public Pair<Boolean, Integer> validDestination(TransporterLocationWrapper loc) {
		int distance = UtilsMath.getDistanceBetween(loc.getDestination(), worldPosition);
		if (distance <= getCurrentRange()) {
			return Pair.of(true, distance);
		}
		return Pair.of(false, 0);
	}

	private void handleParticles(EntityDataWrapper entityData, Vector3f vec) {
		double entityRadius = entityData.bbWidth();
		double entityArea = Math.max(entityRadius * entityData.bbHeight(), 0.3);
		Random random = MatterOverdrive.RANDOM;
		double time = Math.min(getProgress() / (double) (BUILD_UP_TIME), 1);
		float gravity = 0.015f;
		int age = (int) Math.round(UtilsMath.easeIn(time, 5, 15, 1));
		int count = (int) Math.round(UtilsMath.easeIn(time, 2, entityArea * 15, 1));

		for (int i = 0; i < count; i++) {
			float speed = 0.5F;
			float height = vec.y() + random.nextFloat() * entityData.bbHeight();

			Vector3f origin = new Vector3f(vec.x(), height, vec.z());
			Vector3f pos = UtilsMath.randomSpherePoint(origin.x(), origin.y(), origin.z(),
					new Vector3d(entityRadius, 0, entityRadius), random);
			origin.sub(pos);
			origin.mul(speed);

			getLevel().addParticle(new ParticleOptionReplicator().setGravity(gravity).setScale(0.1F).setAge(age),
					pos.x(), pos.y(), pos.z(), 0, speed, 0);
		}

	}

	private ServerLevel handleDimensionChange(Entity entity) {
		ResourceKey<Level> dim = locationManager.getLocation(destinationProp.get()).getDimension();
		if (dim != null) {
			ServerLevel level = ServerLifecycleHooks.getCurrentServer().getLevel(dim);
			entity.changeDimension(level, MANAGER);
			return level;
		}
		return (ServerLevel) getLevel();
	}
	
	@Override
	public void onEntityContact(BlockState state, Entity entity, boolean inside) {
		if(!level.isClientSide) {
			setShouldSaveData(updateTickable(true));
		}
	}
	
	private static TriPredicate<Integer, ItemStack, CapabilityInventory> getValidator() {
		return (x, y, i) -> x == 0 && y.getItem() instanceof ItemCommunicator
						|| x == 1 && y.getItem() instanceof ItemTransporterFlashdrive
						|| x >= i.energyInputSlotsIndex() && x < i.matterInputSlotsIndex() && UtilsCapability.hasEnergyCap(y)
						|| x >= i.matterInputSlotsIndex() && x < i.energyOutputSlotsIndex() && UtilsCapability.hasMatterCap(y)
						|| x >= i.energyOutputSlotsIndex() && x < i.matterOutputSlotsIndex() && UtilsCapability.hasEnergyCap(y)
						|| x >= i.matterOutputSlotsIndex() && x < i.upgradeIndex() && UtilsCapability.hasMatterCap(y)
						|| x >= i.upgradeIndex() && y.getItem() instanceof ItemUpgrade upgrade
								&& i.isUpgradeValid(upgrade.type);
	}

}
