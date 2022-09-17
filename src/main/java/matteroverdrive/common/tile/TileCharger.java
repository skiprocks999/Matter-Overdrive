package matteroverdrive.common.tile;

import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryCharger;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.property.Property;
import matteroverdrive.core.property.PropertyTypes;
import matteroverdrive.core.tile.types.GenericMachineTile;
import matteroverdrive.registry.TileRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class TileCharger extends GenericMachineTile {

	public static final int SLOT_COUNT = 2;

	public static final int CHARGE_RATE = 512;
	private static final int ENERGY_STORAGE = 512000;
	private static final int DEFAULT_RADIUS = 8;

	public final Property<CompoundTag> capInventoryProp;
	public final Property<CompoundTag> capEnergyStorageProp;

	public TileCharger(BlockPos pos, BlockState state) {
		super(TileRegistry.TILE_CHARGER.get(), pos, state);

		setPowerUsage(CHARGE_RATE);
		setRange(DEFAULT_RADIUS);

		defaultPowerStorage = ENERGY_STORAGE;
		defaultPowerUsage = CHARGE_RATE;
		defaultRange = DEFAULT_RADIUS;

		capInventoryProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.NBT
				.create(() -> getInventoryCap().serializeNBT(), tag -> getInventoryCap().deserializeNBT(tag)));
		capEnergyStorageProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.NBT
				.create(() -> getEnergyStorageCap().serializeNBT(), tag -> getEnergyStorageCap().deserializeNBT(tag)));

		addInventoryCap(new CapabilityInventory(SLOT_COUNT, false, false).setUpgrades(2).setOwner(this)
				.setValidator(machineValidator()).setValidUpgrades(InventoryCharger.UPGRADES)
				.setPropertyManager(capInventoryProp));
		addEnergyStorageCap(new CapabilityEnergyStorage(ENERGY_STORAGE, true, false).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.DOWN, Direction.NORTH }, null)
				.setPropertyManager(capEnergyStorageProp));
		setMenuProvider(new SimpleMenuProvider(
				(id, inv, play) -> new InventoryCharger(id, play.getInventory(), getInventoryCap(), getCoordsData()),
				getContainerName(TypeMachine.CHARGER.id())));
		setTickable();
	}

	@Override
	public void tickServer() {
		// TODO implement
	}

	@Override
	public AABB getRenderBoundingBox() {
		return super.getRenderBoundingBox().inflate(2);
	}

}
