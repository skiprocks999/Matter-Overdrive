package matteroverdrive.common.tile;

import matteroverdrive.client.particle.shockwave.ParticleOptionShockwave;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventorySpacetimeAccelerator;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.capability.types.matter.CapabilityMatterStorage;
import matteroverdrive.core.tile.types.GenericUpgradableTile;
import matteroverdrive.core.tile.utils.IUpgradableTile;
import matteroverdrive.core.utils.UtilsTile;
import matteroverdrive.core.utils.UtilsWorld;
import matteroverdrive.registry.TileRegistry;
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

	public boolean clientRunning;

	public TileSpacetimeAccelerator(BlockPos pos, BlockState state) {
		super(TileRegistry.TILE_SPACETIME_ACCELERATOR.get(), pos, state);
		currentSpeed = DEFAULT_MULTIPLIER;
		currentPowerUsage = ENERGY_USAGE_PER_TICK;
		currentRange = BASE_RADIUS;
		currentMatterUsage = MATTER_USAGE_PER_TICK;
		addInventoryCap(new CapabilityInventory(SLOT_COUNT, true, true).setEnergySlots(1).setMatterSlots(1)
				.setUpgrades(4).setOwner(this).setValidator(machineValidator())
				.setValidUpgrades(InventorySpacetimeAccelerator.UPGRADES));
		addEnergyStorageCap(new CapabilityEnergyStorage(ENERGY_CAPACITY, true, false).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.UP }, null));
		addMatterStorageCap(new CapabilityMatterStorage(MATTER_CAPACITY, true, false).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.DOWN }, null));
		setMenuProvider(
				new SimpleMenuProvider((id, inv, play) -> new InventorySpacetimeAccelerator(id, play.getInventory(),
						getInventoryCap(), getCoordsData()), getContainerName(TypeMachine.SPACETIME_ACCELERATOR.id())));
		setTickable();
		setHasRenderData();
	}

	@Override
	public void tickServer() {
		if (!canRun()) {
			resetRadiusMultipliers();
			running = false;
			return;
		}
		UtilsTile.drainElectricSlot(this);
		UtilsTile.drainMatterSlot(this);
		CapabilityEnergyStorage energy = getEnergyStorageCap();

		if (energy.getEnergyStored() < getCurrentPowerUsage()) {
			resetRadiusMultipliers();
			running = false;
			return;
		}

		CapabilityMatterStorage matter = getMatterStorageCap();
		if (matter.getMatterStored() < getCurrentMatterUsage()) {
			resetRadiusMultipliers();
			running = false;
			return;
		}

		running = true;
		energy.removeEnergy((int) getCurrentPowerUsage());
		matter.removeMatter(getCurrentMatterUsage());
		if (ticks % 10 == 0) {
			updateSurroundingTileMultipliers(getCurrentSpeed());
		}
		setChanged();
	}

	@Override
	public void tickClient() {
		if (clientRunning && ticks % (getCurrentRange() * 5) == 0) {
			ParticleOptionShockwave shockwave = new ParticleOptionShockwave();
			shockwave.setMaxScale((float) getCurrentRange());
			shockwave.setColor(191, 228, 230, 255);
			BlockPos pos = getBlockPos();
			getLevel().addParticle(shockwave, pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, 0, 0, 0);
		}
	}

	@Override
	public void getRenderData(CompoundTag tag) {
		tag.putBoolean("running", running);
	}

	@Override
	public void readRenderData(CompoundTag tag) {
		clientRunning = tag.getBoolean("running");
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);

		CompoundTag additional = new CompoundTag();
		additional.putDouble("speed", currentSpeed);
		additional.putDouble("usage", currentPowerUsage);
		additional.putDouble("radius", currentRange);
		additional.putDouble("matusage", currentMatterUsage);

		tag.put("additional", additional);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);

		CompoundTag additional = tag.getCompound("additional");
		currentSpeed = additional.getDouble("speed");
		currentPowerUsage = additional.getDouble("usage");
		currentMatterUsage = additional.getDouble("matusage");
		currentRange = additional.getDouble("radius");

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
		return ENERGY_USAGE_PER_TICK;
	}

	@Override
	public double getDefaultRange() {
		return BASE_RADIUS;
	}

	@Override
	public double getCurrentMatterStorage() {
		return getMatterStorageCap().getMaxMatterStored();
	}

	@Override
	public double getCurrentPowerStorage() {
		return getEnergyStorageCap().getMaxEnergyStored();
	}

	@Override
	public void setMatterStorage(double storage) {
		getMatterStorageCap().updateMaxMatterStorage(storage);
	}

	@Override
	public void setPowerStorage(double storage) {
		getEnergyStorageCap().updateMaxEnergyStorage((int) storage);
	}

	private void resetRadiusMultipliers() {
		if (running) {
			updateSurroundingTileMultipliers(1.0);
		}
	}

	private void updateSurroundingTileMultipliers(double multipler) {
		BlockPos pos = getBlockPos();
		UtilsWorld
				.getSurroundingBlockEntities(level,
						new AABB(pos.offset(-currentRange, -currentRange, -currentRange), pos.offset(currentRange, currentRange, currentRange)))
				.forEach(entity -> {
					if (entity instanceof IUpgradableTile upgrade) {
						upgrade.setAcceleratorMultiplier(multipler);
						entity.setChanged();
					}
				});
	}

}
