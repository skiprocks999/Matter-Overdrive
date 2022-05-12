package matteroverdrive.common.tile;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.MatterOverdrive;
import matteroverdrive.SoundRegister;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryMatterDecomposer;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.capability.types.matter.CapabilityMatterStorage;
import matteroverdrive.core.sound.TickableSoundTile;
import matteroverdrive.core.tile.types.GenericSoundTile;
import matteroverdrive.core.tile.utils.PacketHandler;
import matteroverdrive.core.tile.utils.Ticker;
import matteroverdrive.core.tile.utils.TransporterLocationWrapper;
import matteroverdrive.core.utils.UtilsMath;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

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
	public int clientDestination;

	public CapabilityInventory clientInventory;
	public CapabilityEnergyStorage clientEnergy;
	public CapabilityMatterStorage clientMatter;

	public TileTransporter(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_TRANSPORTER.get(), pos, state);
		addCapability(new CapabilityInventory(SLOT_COUNT, true, true).setInputs(1).setEnergySlots(1).setMatterSlots(1)
				.setUpgrades(5).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.SOUTH }, new Direction[] { Direction.DOWN })
				.setValidator(machineValidator()).setValidUpgrades(InventoryMatterDecomposer.UPGRADES));
		addCapability(new CapabilityEnergyStorage(ENERGY_STORAGE, true, false).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.WEST, Direction.EAST }, null));
		addCapability(new CapabilityMatterStorage(MATTER_STORAGE, true, false).setOwner(this).setDefaultDirections(
				state, null, new Direction[] { Direction.NORTH, Direction.EAST, Direction.WEST }));
		setMenuProvider(new SimpleMenuProvider(
				(id, inv, play) -> new InventoryMatterDecomposer(id, play.getInventory(),
						exposeCapability(CapabilityType.Item), getCoordsData()),
				getContainerName(TypeMachine.MATTER_DECOMPOSER.id())));
		setMenuPacketHandler(
				new PacketHandler(this, true).packetReader(this::clientMenuLoad).packetWriter(this::clientMenuSave));
		setRenderPacketHandler(
				new PacketHandler(this, false).packetReader(this::clientTileLoad).packetWriter(this::clientTileSave));
		setTicker(new Ticker(this).tickServer(this::tickServer).tickClient(this::tickClient));
		Arrays.fill(LOCATIONS, new TransporterLocationWrapper());
	}

	private void tickServer(Ticker ticker) {
		if (canRun()) {
			if (cooldownTimer >= COOLDOWN) {
				List<LivingEntity> entitiesAbove = level.getEntitiesOfClass(LivingEntity.class,
						new AABB(getBlockPos().above()));
				if (entitiesAbove.size() > 0 && currDestination >= 0) {
					TransporterLocationWrapper curLoc = LOCATIONS[currDestination];
					Pair<Boolean, Integer> validData = validDestination(curLoc);
					if (validData.getFirst()) {
						CapabilityEnergyStorage energy = exposeCapability(CapabilityType.Energy);
						CapabilityMatterStorage matter = exposeCapability(CapabilityType.Matter);
						if (energy.getEnergyStored() >= energyUsage && matter.getMatterStored() >= matterUsage) {
							energy.removeEnergy(energyUsage);
							running = true;
							currProgress += getProgress(validData.getSecond());
							if (currProgress >= BUILD_UP_TIME) {
								cooldownTimer = 0;
								matter.removeMatter(matterUsage);
								int size = entitiesAbove.size() >= ENTITIES_PER_BATCH ? ENTITIES_PER_BATCH
										: entitiesAbove.size();
								double x = curLoc.getDestination().getX() + 0.5;
								double y = curLoc.getDestination().getY() + 0.01;
								double z = curLoc.getDestination().getZ() + 0.5;
								for (int i = 0; i < size; i++) {
									entitiesAbove.get(i).teleportToWithTicket(x, y, z);
								}
							}
						} else {
							running = false;
						}
					} else {
						running = false;
						currProgress = 0;
					}
				} else {
					running = false;
					currProgress = 0;
				}
			} else {
				cooldownTimer++;
				running = false;
				currProgress = 0;
			}
		} else {
			running = false;
			currProgress = 0;
		}
	}

	private void tickClient(Ticker ticker) {
		if (shouldPlaySound() && !clientSoundPlaying) {
			clientSoundPlaying = true;
			Minecraft.getInstance().getSoundManager()
					.play(new TickableSoundTile(SoundRegister.SOUND_TRANSPORTER.get(), this));
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

		Arrays.fill(CLIENT_LOCATIONS, new TransporterLocationWrapper());
		for (int i = 0; i < CLIENT_LOCATIONS.length; i++) {
			CLIENT_LOCATIONS[i].deserializeNbt(tag.getCompound("destination" + i));
		}
	}

	private void clientTileSave(CompoundTag tag) {
		tag.putBoolean("running", running);
		tag.putBoolean("muffled", isMuffled);
		tag.putDouble("progress", currProgress);
	}

	private void clientTileLoad(CompoundTag tag) {
		clientRunning = tag.getBoolean("running");
		clientMuffled = tag.getBoolean("muffled");
		clientProgress = tag.getDouble("progress");
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

	@Override
	public boolean isMuffled(boolean clientSide) {
		return clientSide ? clientMuffled : isMuffled;
	}

	@Override
	public double getCurrentSpeed(boolean clientSide) {
		return clientSide ? clientSpeed : currSpeed;
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
		return clientSide ? clientEnergyUsage : energyUsage;
	}

	@Override
	public double getCurrentMatterUsage(boolean clientSide) {
		return clientSide ? clientMatterUsage : matterUsage;
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

	@Override
	public int getProcessingTime() {
		return BUILD_UP_TIME;
	}

	private Pair<Boolean, Integer> validDestination(TransporterLocationWrapper loc) {
		int distance = UtilsMath.getDistanceBetween(loc.getDestination(), worldPosition);
		if (distance <= radius) {
			return Pair.of(true, distance);
		}
		return Pair.of(false, 0);
	}

	private int getProgress(double distance) {
		int val = (int) (currSpeed / (distance / 4.0));
		return val < 1 ? 1 : val;
	}

	private void handleParticles(LivingEntity entity, Vector3f vec) {

		double entityRadius = entity.getBbWidth();
		double entityArea = Math.max(entityRadius * entity.getBbHeight(), 0.3);
		Random random = MatterOverdrive.RANDOM;
		double radiusX = entityRadius + random.nextDouble() * 0.2f;
		double radiusZ = entityRadius + random.nextDouble() * 0.2f;
		double time = Math.min((double) currProgress / (double) (BUILD_UP_TIME), 1);
		double gravity = 0.015f;
		int age = (int) Math.round(UtilsMath.easeIn(time, 5, 15, 1));
		int count = (int) Math.round(UtilsMath.easeIn(time, 2, entityArea * 15, 1));

		for (int i = 0; i < count; i++) {
			float speed = random.nextFloat() * 0.05f + 0.15f;
			float height = vec.y() + random.nextFloat() * entity.getBbHeight();

			Vector3f origin = new Vector3f(vec.x(), height, vec.z());
			Vector3f pos = UtilsMath.randomSpherePoint(origin.x(), origin.y(), origin.z(),
					new Vector3d(radiusX, 0, radiusZ), random);
			origin.sub(pos);
			origin.cross(new Vector3f(0, 0, 0));
			
			//Vector3f dir = Vector3f.cross(Vector3f.sub(origin, pos, null), new Vector3f(0, 0, 0), null);
			origin.mul(speed);
			// dir.scale(speed);
			ReplicatorParticle replicatorParticle = new ReplicatorParticle(this.level, pos.x(), pos.y(), pos.z(),
					origin.x(), origin.y(), origin.z());
			replicatorParticle.setCenter(origin.x(), origin.y(), origin.z());

			replicatorParticle.setParticleAge(age);
			replicatorParticle.setPointGravityScale(gravity);

			Minecraft.getInstance().particleEngine.add(replicatorParticle);
			//Minecraft.getInstance().effectRenderer.addEffect(replicatorParticle);
		}

	}

}
