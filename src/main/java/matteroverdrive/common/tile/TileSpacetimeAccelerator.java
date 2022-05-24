package matteroverdrive.common.tile;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.client.particle.shockwave.ParticleOptionShockwave;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventorySpacetimeAccelerator;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.capability.types.matter.CapabilityMatterStorage;
import matteroverdrive.core.tile.types.GenericUpgradableTile;
import matteroverdrive.core.tile.utils.IUpgradableTile;
import matteroverdrive.core.tile.utils.PacketHandler;
import matteroverdrive.core.tile.utils.Ticker;
import matteroverdrive.core.utils.UtilsTile;
import matteroverdrive.core.utils.UtilsWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class TileSpacetimeAccelerator extends GenericUpgradableTile {

	public static final int SLOT_COUNT = 6;
	
	public static final int ENERGY_USAGE_PER_TICK = 64;
	public static final double MATTER_USAGE_PER_TICK = 0.2D;
	public static final int BASE_RADIUS = 2;
	public static final int ENERGY_CAPACITY = 512000;
	public static final double MATTER_CAPACITY = 1024;
	public static final double DEFAULT_MULTIPLIER = 1.5;
	
	private boolean running = false;
	private double currSpeed = DEFAULT_MULTIPLIER;
	private int energyUsage = ENERGY_USAGE_PER_TICK;
	private int radius = BASE_RADIUS;
	private double matterUsage = MATTER_USAGE_PER_TICK;

	public int clientEnergyUsage;
	public double clientMatterUsage;
	public double clientSpeed;
	public boolean clientRunning;
	public int clientRadius;

	public CapabilityInventory clientInventory;
	public CapabilityEnergyStorage clientEnergy;
	public CapabilityMatterStorage clientMatter;
	
	public TileSpacetimeAccelerator(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_SPACETIME_ACCELERATOR.get(), pos, state);
		addCapability(new CapabilityInventory(SLOT_COUNT, true, true).setEnergySlots(1).setMatterSlots(1)
				.setUpgrades(4).setOwner(this)
				.setValidator(machineValidator()).setValidUpgrades(InventorySpacetimeAccelerator.UPGRADES));
		addCapability(new CapabilityEnergyStorage(ENERGY_CAPACITY, true, false).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.UP}, null));
		addCapability(new CapabilityMatterStorage(MATTER_CAPACITY, true, false).setOwner(this).setDefaultDirections(
				state, new Direction[] { Direction.DOWN }, null));
		setMenuProvider(new SimpleMenuProvider(
				(id, inv, play) -> new InventorySpacetimeAccelerator(id, play.getInventory(),
						exposeCapability(CapabilityType.Item), getCoordsData()),
				getContainerName(TypeMachine.SPACETIME_ACCELERATOR.id())));
		setMenuPacketHandler(
				new PacketHandler(this, true).packetReader(this::clientMenuLoad).packetWriter(this::clientMenuSave));
		setRenderPacketHandler(
				new PacketHandler(this, false).packetReader(this::clientTileLoad).packetWriter(this::clientTileSave));
		setTicker(new Ticker(this).tickServer(this::tickServer).tickClient(this::tickClient));
	}
	
	private void tickServer(Ticker ticker) {
		if(canRun()) {
			UtilsTile.drainElectricSlot(this);
			UtilsTile.drainMatterSlot(this);
			CapabilityEnergyStorage energy = exposeCapability(CapabilityType.Energy);
			if(energy.getEnergyStored() >= getCurrentPowerUsage(false)) {
				CapabilityMatterStorage matter = exposeCapability(CapabilityType.Matter);
				if(matter.getMatterStored() >= getCurrentMatterUsage(false)) {
					running = true;
					energy.removeEnergy((int) getCurrentPowerUsage(false));
					matter.removeMatter(getCurrentMatterUsage(false));
					if(ticker.getTicks() % 10 == 0) {
						updateSurroundingTileMultipliers(getCurrentSpeed(false));
					}
					setChanged();
				} else {
					resetRadiusMultipliers();
					running = false;
				}
			} else {
				resetRadiusMultipliers();
				running = false;
			}
		} else {
			resetRadiusMultipliers();
			running = false;
		}
	}
	
	private void tickClient(Ticker ticker) {
		if(clientRunning && ticker.getTicks() % 10 == 0) {
			ParticleOptionShockwave shockwave = new ParticleOptionShockwave();
			shockwave.setMaxScale((float) getCurrentRange(true));
			shockwave.setColor(51, 78, 120, 1);
			BlockPos pos = getBlockPos();
			getLevel().addParticle(shockwave, pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, 0, 0, 0);
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
		tag.putDouble("matusage", matterUsage);
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
		clientMatterUsage = tag.getDouble("matusage");
	}
	
	private void clientTileSave(CompoundTag tag) {
		tag.putBoolean("running", running);
		tag.putInt("radius", radius);
	}
	
	private void clientTileLoad(CompoundTag tag) {
		clientRunning = tag.getBoolean("running");
		clientRadius = tag.getInt("radius");
	}

	@Override
	public int getMaxMode() {
		return 2;
	}
	
	@Override
	public double getDefaultSpeed() {
		return DEFAULT_MULTIPLIER;
	}

	@Override
	public double getDefaultMatterUsage() {
		return MATTER_USAGE_PER_TICK;
	}

	@Override
	public double getDefaultMatterStorage() {
		return MATTER_CAPACITY;
	}

	@Override
	public double getDefaultPowerStorage() {
		return ENERGY_CAPACITY;
	}

	@Override
	public double getDefaultPowerUsage() {
		return MATTER_USAGE_PER_TICK;
	}
	
	@Override
	public double getDefaultRange() {
		return BASE_RADIUS;
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
	
	private void resetRadiusMultipliers() {
		if(running) {
			updateSurroundingTileMultipliers(1.0);
		}	
	}
	
	private void updateSurroundingTileMultipliers(double multipler) {
		BlockPos pos = getBlockPos();
		UtilsWorld.getSurroundingBlockEntities(level, new AABB(pos.offset(-radius, -radius, -radius), pos.offset(radius, radius, radius))).forEach(entity -> {
			if(entity instanceof IUpgradableTile upgrade) {
				upgrade.setAcceleratorMultiplier(multipler);
				entity.setChanged();
			}
		});
	}

}
