package matteroverdrive.common.tile.transporter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.MatterOverdrive;
import matteroverdrive.SoundRegister;
import matteroverdrive.client.particle.replicator.ParticleOptionReplicator;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryTransporter;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.capability.types.matter.CapabilityMatterStorage;
import matteroverdrive.core.sound.TickableSoundTile;
import matteroverdrive.core.tile.types.GenericSoundTile;
import matteroverdrive.core.tile.utils.PacketHandler;
import matteroverdrive.core.tile.utils.Ticker;
import matteroverdrive.core.utils.UtilsMath;
import matteroverdrive.core.utils.UtilsTile;
import matteroverdrive.core.utils.misc.EntityDataWrapper;
import matteroverdrive.core.utils.misc.Scheduler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.server.ServerLifecycleHooks;

public class TileTransporter extends GenericSoundTile {

	public static final int SLOT_COUNT = 8;

	private static final int COOLDOWN = 80;
	public static final int ENTITIES_PER_BATCH = 3;

	public static final int BUILD_UP_TIME = 70;
	private static final int USAGE_PER_TICK = 80;
	private static final int MATTER_STORAGE = 512;
	private static final int MATTER_USAGE = 25;
	private static final int ENERGY_STORAGE = 1024000;
	private static final int DEFAULT_SPEED = 1;
	private static final int DEFAULT_RADUIS = 32;

	private boolean running = false;
	private double currProgress = 0;
	private double currSpeed = DEFAULT_SPEED;
	private int energyUsage = USAGE_PER_TICK;
	private boolean isMuffled = false;
	private int radius = DEFAULT_RADUIS;
	private int cooldownTimer = 0;
	private double matterUsage = MATTER_USAGE;
	private TransporterLocationWrapper[] LOCATIONS = new TransporterLocationWrapper[5];
	private int currDestination = -1;
	private List<Entity> currEntities = new ArrayList<>();
	
	private static final TransporterDimensionManager MANAGER = new TransporterDimensionManager();

	public int clientEnergyUsage;
	public double clientMatterUsage;
	public double clientProgress;
	public double clientSpeed;
	private boolean clientMuffled;
	public boolean clientRunning;
	public int clientRadius;
	private boolean clientSoundPlaying = false;
	public int clientCooldown;
	public TransporterLocationWrapper[] CLIENT_LOCATIONS = new TransporterLocationWrapper[5];
	public int clientDestination = -1;
	private List<EntityDataWrapper> clientEntityData = new ArrayList<>();

	public CapabilityInventory clientInventory;
	public CapabilityEnergyStorage clientEnergy;
	public CapabilityMatterStorage clientMatter;

	public TileTransporter(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_TRANSPORTER.get(), pos, state);
		addCapability(new CapabilityInventory(SLOT_COUNT, true, true).setInputs(1).setEnergySlots(1).setMatterSlots(1)
				.setUpgrades(5).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.SOUTH }, new Direction[] { Direction.DOWN })
				.setValidator(machineValidator()).setValidUpgrades(InventoryTransporter.UPGRADES));
		addCapability(new CapabilityEnergyStorage(ENERGY_STORAGE, true, false).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.WEST, Direction.EAST }, null));
		addCapability(new CapabilityMatterStorage(MATTER_STORAGE, true, false).setOwner(this).setDefaultDirections(
				state, new Direction[] { Direction.NORTH, Direction.EAST, Direction.WEST }, null));
		setMenuProvider(new SimpleMenuProvider(
				(id, inv, play) -> new InventoryTransporter(id, play.getInventory(),
						exposeCapability(CapabilityType.Item), getCoordsData()),
				getContainerName(TypeMachine.TRANSPORTER.id())));
		setMenuPacketHandler(
				new PacketHandler(this, true).packetReader(this::clientMenuLoad).packetWriter(this::clientMenuSave));
		setRenderPacketHandler(
				new PacketHandler(this, false).packetReader(this::clientTileLoad).packetWriter(this::clientTileSave));
		setTicker(new Ticker(this).tickServer(this::tickServer).tickClient(this::tickClient));
		fillLocations(LOCATIONS);
	}

	private void tickServer(Ticker ticker) {
		if (canRun()) {
			UtilsTile.drainElectricSlot(this);
			UtilsTile.drainMatterSlot(this);
			if (cooldownTimer >= COOLDOWN) {
				List<Entity> entitiesAbove = level.getEntitiesOfClass(Entity.class,
						new AABB(getBlockPos().above()));
				if (entitiesAbove.size() > 0 && currDestination >= 0) {
					TransporterLocationWrapper curLoc = LOCATIONS[currDestination];
					Pair<Boolean, Integer> validData = validDestination(curLoc);
					if (validData.getFirst()) {
						CapabilityEnergyStorage energy = exposeCapability(CapabilityType.Energy);
						CapabilityMatterStorage matter = exposeCapability(CapabilityType.Matter);
						if (energy.getEnergyStored() >= getCurrentPowerUsage(false) && matter.getMatterStored() >= getCurrentMatterUsage(false)) {
							int size = entitiesAbove.size() >= ENTITIES_PER_BATCH ? ENTITIES_PER_BATCH
									: entitiesAbove.size();
							energy.removeEnergy((int) getCurrentPowerUsage(false));
							running = true;
							currProgress += getCurrentSpeed(false);
							currEntities.clear();
							currEntities.addAll(entitiesAbove.subList(0, size));
							if (currProgress >= BUILD_UP_TIME) {
								cooldownTimer = 0;
								matter.removeMatter(getCurrentMatterUsage(false));
								currProgress = 0;
								double x = curLoc.getDestination().getX() + 0.5;
								double y = curLoc.getDestination().getY();
								double z = curLoc.getDestination().getZ() + 0.5;
								for (Entity entity : currEntities) {
									ServerLevel dim = handleDimensionChange(entity);
									entity.teleportToWithTicket(x, y, z);
									entity.getCapability(MatterOverdriveCapabilities.ENTITY_DATA).ifPresent(h -> {
										h.setTransporterTimer(70);
									});
									level.getCapability(MatterOverdriveCapabilities.OVERWORLD_DATA).ifPresent(h -> {
										h.addActiveTransport(new ActiveTransportDataWrapper(entity.getUUID(), 70, dim.dimension()));
									});
									Scheduler.schedule(1, () -> {
										dim.playSound(null, curLoc.getDestination(), SoundRegister.SOUND_TRANSPORTER_ARRIVE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
									});
								}
							}
							setChanged();
						} else {
							running = false;
							currEntities.clear();
						}
					} else {
						running = false;
						currProgress = 0;
						currEntities.clear();
					}
				} else {
					running = false;
					currProgress = 0;
					currEntities.clear();
				}
			} else {
				cooldownTimer++;
				running = false;
				currProgress = 0;
				currEntities.clear();
			}
		} else {
			running = false;
			currProgress = 0;
			currEntities.clear();
		}
	}

	private void tickClient(Ticker ticker) {
		if (shouldPlaySound() && !clientSoundPlaying) {
			clientSoundPlaying = true;
			Minecraft.getInstance().getSoundManager()
					.play(new TickableSoundTile(SoundRegister.SOUND_TRANSPORTER.get(), this));
		}
		if (clientProgress > 0 && clientEntityData != null && clientRunning) {
			int particlesPerTick = (int) ((clientProgress / (double) BUILD_UP_TIME) * 20);
			for (int i = 0; i < particlesPerTick; i++) {
				for (EntityDataWrapper entityData : clientEntityData) {
					handleParticles(entityData, new Vector3f((float) entityData.xPos(), (float) getBlockPos().getY(),
							(float) entityData.zPos()));
				}
			}
		}
	}

	private void clientMenuSave(CompoundTag tag) {
		CapabilityInventory inv = exposeCapability(CapabilityType.Item);
		tag.put(inv.getSaveKey(), inv.serializeNBT());
		CapabilityEnergyStorage energy = exposeCapability(CapabilityType.Energy);
		tag.put(energy.getSaveKey(), energy.serializeNBT());
		CapabilityMatterStorage matter = exposeCapability(CapabilityType.Matter);
		tag.put(matter.getSaveKey(), matter.serializeNBT());

		tag.putInt("redstone", currRedstoneMode);
		tag.putInt("usage", energyUsage);
		tag.putDouble("speed", currSpeed);
		tag.putInt("radius", radius);
		tag.putInt("cooldown", cooldownTimer);
		tag.putDouble("matusage", matterUsage);
		tag.putInt("dest", currDestination);

		for (int i = 0; i < LOCATIONS.length; i++) {
			LOCATIONS[i].serializeNbt(tag, "destination" + i);
		}
	}

	private void clientMenuLoad(CompoundTag tag) {
		clientInventory = new CapabilityInventory();
		clientInventory.deserializeNBT(tag.getCompound(clientInventory.getSaveKey()));
		clientEnergy = new CapabilityEnergyStorage(0, false, false);
		clientEnergy.deserializeNBT(tag.getCompound(clientEnergy.getSaveKey()));
		clientMatter = new CapabilityMatterStorage(0, false, false);
		clientMatter.deserializeNBT(tag.getCompound(clientMatter.getSaveKey()));

		clientRedstoneMode = tag.getInt("redstone");
		clientEnergyUsage = tag.getInt("usage");
		clientSpeed = tag.getDouble("speed");
		clientRadius = tag.getInt("radius");
		clientCooldown = tag.getInt("cooldown");
		clientMatterUsage = tag.getDouble("matusage");
		clientDestination = tag.getInt("dest");

		fillLocations(CLIENT_LOCATIONS);
		for (int i = 0; i < CLIENT_LOCATIONS.length; i++) {
			CLIENT_LOCATIONS[i].deserializeNbt(tag.getCompound("destination" + i));
		}
	}

	private void clientTileSave(CompoundTag tag) {
		tag.putBoolean("running", running);
		tag.putBoolean("muffled", isMuffled);
		tag.putDouble("progress", currProgress);
		tag.putInt("entities", currEntities.size());
		tag.putDouble("sabonus", saMultiplier);
		for (int i = 0; i < currEntities.size(); i++) {
			Entity entity = currEntities.get(i);
			EntityDataWrapper wrapper = new EntityDataWrapper(entity.getBbHeight(), entity.getBbWidth(), entity.getX(),
					entity.getZ());
			wrapper.serializeNbt(tag, "entity" + i);
		}
	}

	private void clientTileLoad(CompoundTag tag) {
		clientRunning = tag.getBoolean("running");
		clientMuffled = tag.getBoolean("muffled");
		clientProgress = tag.getDouble("progress");
		clientSAMultipler = tag.getDouble("sabonus");
		clientEntityData = new ArrayList<>();
		int size = tag.getInt("entities");
		for (int i = 0; i < size; i++) {
			clientEntityData.add(EntityDataWrapper.fromNbt(tag.getCompound("entity" + i)));
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);

		CompoundTag additional = new CompoundTag();
		additional.putDouble("progress", currProgress);
		additional.putDouble("speed", currSpeed);
		additional.putInt("usage", energyUsage);
		additional.putBoolean("muffled", isMuffled);
		additional.putInt("radius", radius);
		additional.putDouble("matusage", matterUsage);
		additional.putInt("cooldown", cooldownTimer);
		additional.putInt("dest", currDestination);
		for (int i = 0; i < LOCATIONS.length; i++) {
			LOCATIONS[i].serializeNbt(additional, "destination" + i);
		}

		tag.put("additional", additional);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);

		CompoundTag additional = tag.getCompound("additional");
		currProgress = additional.getDouble("progress");
		currSpeed = additional.getDouble("speed");
		energyUsage = additional.getInt("usage");
		matterUsage = additional.getDouble("matusage");
		cooldownTimer = additional.getInt("cooldown");
		radius = additional.getInt("radius");
		isMuffled = additional.getBoolean("muffled");
		currDestination = additional.getInt("dest");
		for (int i = 0; i < LOCATIONS.length; i++) {
			LOCATIONS[i].deserializeNbt(additional.getCompound("destination" + i));
		}
	}

	@Override
	public boolean shouldPlaySound() {
		return clientRunning && !clientMuffled && clientProgress > 0;
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
	public double getDefaultSpeed() {
		return DEFAULT_SPEED;
	}

	@Override
	public double getDefaultMatterUsage() {
		return MATTER_USAGE;
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
	
	public static int getDefaultRaduis() {
		return DEFAULT_RADUIS;
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
		return clientSide ? clientEnergyUsage * clientSAMultipler : energyUsage * saMultiplier;
	}

	@Override
	public double getCurrentMatterUsage(boolean clientSide) {
		return clientSide ? clientMatterUsage * clientSAMultipler : matterUsage * saMultiplier;
	}
	
	@Override
	public double getCurrentRange(boolean clientSide) {
		return clientSide ? clientRadius : radius;
	}

	@Override
	public void setSpeed(double speed) {
		currSpeed = speed;
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
		this.energyUsage = usage;
	}

	@Override
	public void setMatterUsage(double matter) {
		this.matterUsage = matter;
	}

	@Override
	public void setMuffled(boolean muffled) {
		isMuffled = muffled;
	}
	
	public void setRadius(int radius) {
		this.radius = radius;
	}

	@Override
	public int getProcessingTime() {
		return BUILD_UP_TIME;
	}
	
	@Override
	public void setAcceleratorMultiplier(double multiplier) {
		saMultiplier = multiplier;
	}

	public Pair<Boolean, Integer> validDestination(TransporterLocationWrapper loc) {
		int distance = UtilsMath.getDistanceBetween(loc.getDestination(), worldPosition);
		if (distance <= radius) {
			return Pair.of(true, distance);
		}
		return Pair.of(false, 0);
	}

	private void handleParticles(EntityDataWrapper entityData, Vector3f vec) {
		double entityRadius = entityData.bbWidth();
		double entityArea = Math.max(entityRadius * entityData.bbHeight(), 0.3);
		Random random = MatterOverdrive.RANDOM;
		double radiusX = entityRadius;
		double radiusZ = entityRadius;
		double time = Math.min((double) currProgress / (double) (BUILD_UP_TIME), 1);
		float gravity = 0.015f;
		int age = (int) Math.round(UtilsMath.easeIn(time, 5, 15, 1));
		int count = (int) Math.round(UtilsMath.easeIn(time, 2, entityArea * 15, 1));

		for (int i = 0; i < count; i++) {
			float speed = 0.5F;
			float height = vec.y() + random.nextFloat() * entityData.bbHeight();

			Vector3f origin = new Vector3f(vec.x(), height, vec.z());
			Vector3f pos = UtilsMath.randomSpherePoint(origin.x(), origin.y(), origin.z(),
					new Vector3d(radiusX, 0, radiusZ), random);
			origin.sub(pos);
			origin.mul(speed);

			getLevel().addParticle(new ParticleOptionReplicator().setCenter(origin.x(), origin.y(), origin.z())
					.setGravity(gravity).setAge(age), pos.x(), pos.y(), pos.z(), 0, speed, 0);
		}

	}

	public void setDestination(int index) {
		currDestination = index;
	}

	public int getServerDestination() {
		return currDestination;
	}

	public TransporterLocationWrapper[] getServerLocations() {
		return LOCATIONS;
	}
	
	private void fillLocations(TransporterLocationWrapper[] holder) {
		for(int i = 0; i < holder.length; i++) {
			holder[i] = new TransporterLocationWrapper();
		}
	}
	
	private ServerLevel handleDimensionChange(Entity entity) {
		ResourceKey<Level> dim = LOCATIONS[currDestination].getDimension();
		if(dim != null) {
			ServerLevel level = ServerLifecycleHooks.getCurrentServer().getLevel(dim);
			entity.changeDimension(level, MANAGER);
			return level;
		}
		return (ServerLevel) getLevel();
	}

}
