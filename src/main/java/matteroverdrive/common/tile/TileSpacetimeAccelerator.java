package matteroverdrive.common.tile;

import matteroverdrive.client.particle.shockwave.ParticleOptionShockwave;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventorySpacetimeAccelerator;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.capability.types.matter.CapabilityMatterStorage;
import matteroverdrive.core.property.Property;
import matteroverdrive.core.property.PropertyTypes;
import matteroverdrive.core.tile.types.GenericMachineTile;
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

public class TileSpacetimeAccelerator extends GenericMachineTile {

	public static final int SLOT_COUNT = 6;

	public static final int ENERGY_USAGE_PER_TICK = 64;
	public static final double MATTER_USAGE_PER_TICK = 0.2D;
	public static final int BASE_RADIUS = 2;
	public static final int ENERGY_CAPACITY = 512000;
	public static final double MATTER_CAPACITY = 1024;
	public static final double DEFAULT_MULTIPLIER = 1.5;

	public final Property<CompoundTag> capInventoryProp;
	public final Property<CompoundTag> capEnergyStorageProp;
	public final Property<CompoundTag> capMatterStorageProp;

	public TileSpacetimeAccelerator(BlockPos pos, BlockState state) {
		super(TileRegistry.TILE_SPACETIME_ACCELERATOR.get(), pos, state);

		setSpeed(DEFAULT_MULTIPLIER);
		setPowerUsage(ENERGY_USAGE_PER_TICK);
		setRange(BASE_RADIUS);
		setMatterUsage(MATTER_USAGE_PER_TICK);

		defaultSpeed = DEFAULT_MULTIPLIER;
		defaultMatterUsage = MATTER_USAGE_PER_TICK;
		defaultMatterStorage = MATTER_CAPACITY;
		defaultPowerStorage = ENERGY_CAPACITY;
		defaultPowerUsage = ENERGY_USAGE_PER_TICK;
		defaultRange = BASE_RADIUS;

		capInventoryProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.NBT
				.create(() -> getInventoryCap().serializeNBT(), tag -> getInventoryCap().deserializeNBT(tag)));
		capMatterStorageProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.NBT
				.create(() -> getMatterStorageCap().serializeNBT(), tag -> getMatterStorageCap().deserializeNBT(tag)));
		capEnergyStorageProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.NBT
				.create(() -> getEnergyStorageCap().serializeNBT(), tag -> getEnergyStorageCap().deserializeNBT(tag)));

		addInventoryCap(new CapabilityInventory(SLOT_COUNT, true, true).setEnergyInputSlots(1).setMatterInputSlots(1)
				.setUpgrades(4).setOwner(this).setValidator(machineValidator())
				.setValidUpgrades(InventorySpacetimeAccelerator.UPGRADES).setPropertyManager(capInventoryProp));
		addEnergyStorageCap(new CapabilityEnergyStorage(ENERGY_CAPACITY, true, false).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.UP }, null)
				.setPropertyManager(capEnergyStorageProp));
		addMatterStorageCap(new CapabilityMatterStorage(MATTER_CAPACITY, true, false).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.DOWN }, null)
				.setPropertyManager(capMatterStorageProp));
		setMenuProvider(
				new SimpleMenuProvider((id, inv, play) -> new InventorySpacetimeAccelerator(id, play.getInventory(),
						getInventoryCap(), getCoordsData()), getContainerName(TypeMachine.SPACETIME_ACCELERATOR.id())));
		setTickable();
	}

	@Override
	public void tickServer() {
		UtilsTile.drainElectricSlot(this);
		UtilsTile.drainMatterSlot(this);
		if (!canRun()) {
			resetRadiusMultipliers();
			setShouldSaveData(setRunning(false), updateTickable(false));
			return;
		}
		CapabilityEnergyStorage energy = getEnergyStorageCap();

		if (energy.getEnergyStored() < getCurrentPowerUsage()) {
			resetRadiusMultipliers();
			setShouldSaveData(setRunning(false), updateTickable(false));
			return;
		}

		CapabilityMatterStorage matter = getMatterStorageCap();
		if (matter.getMatterStored() < getCurrentMatterUsage()) {
			resetRadiusMultipliers();
			setShouldSaveData(setRunning(false), updateTickable(false));
			return;
		}

		setRunning(true);
		energy.removeEnergy((int) getCurrentPowerUsage());
		matter.removeMatter(getCurrentMatterUsage());
		if (ticks % 10 == 0) {
			updateSurroundingTileMultipliers(getCurrentSpeed());
		}
		setChanged();
	}

	@Override
	public void tickClient() {
		if (isRunning() && ticks % (getCurrentRange() * 5) == 0) {
			ParticleOptionShockwave shockwave = new ParticleOptionShockwave();
			shockwave.setMaxScale((float) getCurrentRange());
			shockwave.setColor(191, 228, 230, 255);
			BlockPos pos = getBlockPos();
			getLevel().addParticle(shockwave, pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, 0, 0, 0);
		}
	}

	private void resetRadiusMultipliers() {
		if (isRunning()) {
			updateSurroundingTileMultipliers(1.0);
		}
	}

	private void updateSurroundingTileMultipliers(double multipler) {
		BlockPos pos = getBlockPos();
		double range = getCurrentRange();
		UtilsWorld
				.getSurroundingBlockEntities(level,
						new AABB(pos.offset(-range, -range, -range), pos.offset(range, range, range)))
				.forEach(entity -> {
					if (entity instanceof IUpgradableTile upgrade) {
						upgrade.setAcceleratorMultiplier(multipler);
						entity.setChanged();
					}
				});
	}

}
