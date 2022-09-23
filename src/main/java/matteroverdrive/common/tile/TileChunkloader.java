package matteroverdrive.common.tile;

import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryChunkloader;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.property.Property;
import matteroverdrive.core.property.PropertyTypes;
import matteroverdrive.core.tile.types.GenericMachineTile;
import matteroverdrive.registry.TileRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.block.state.BlockState;

public class TileChunkloader extends GenericMachineTile {

	public static final int SLOT_COUNT = 3;
	public static final int ENERGY_CAPACITY = 512000;
	public static final int MATTER_CAPACITY = 1024;
	public static final int POWER_USAGE = 1000;
	public static final double MATTER_USAGE = 1.5;
	public static final double BASE_RANGE = 1;

	public final Property<CompoundTag> capInventoryProp;
	public final Property<CompoundTag> capEnergyStorageProp;
	public final Property<CompoundTag> capMatterStorageProp;

	public TileChunkloader(BlockPos pos, BlockState state) {
		super(TileRegistry.TILE_CHUNKLOADER.get(), pos, state);
		
		setPowerUsage(POWER_USAGE);
		setMatterUsage(MATTER_USAGE);

		defaultPowerStorage = ENERGY_CAPACITY;
		defaultMatterStorage = MATTER_CAPACITY;
		defaultPowerUsage = POWER_USAGE;
		defaultMatterUsage = MATTER_USAGE;

		capInventoryProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.NBT
				.create(() -> getInventoryCap().serializeNBT(), tag -> getInventoryCap().deserializeNBT(tag)));
		capEnergyStorageProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.NBT
				.create(() -> getEnergyStorageCap().serializeNBT(), tag -> getEnergyStorageCap().deserializeNBT(tag)));
		capMatterStorageProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.NBT
				.create(() -> getMatterStorageCap().serializeNBT(), tag -> getMatterStorageCap().deserializeNBT(tag)));

		addInventoryCap(new CapabilityInventory(SLOT_COUNT, false, false).setEnergySlots(1).setMatterSlots(1).setUpgrades(1)
				.setOwner(this).setValidator(machineValidator()).setValidUpgrades(InventoryChunkloader.UPGRADES)
				.setPropertyManager(capInventoryProp));
		addEnergyStorageCap(new CapabilityEnergyStorage(ENERGY_CAPACITY, true, false).setOwner(this)
				.setPropertyManager(capEnergyStorageProp));
		setMenuProvider(new SimpleMenuProvider((id, inv, play) -> new InventoryChunkloader(id, play.getInventory(),
				getInventoryCap(), getCoordsData()), getContainerName(TypeMachine.CHUNKLOADER.id())));
		setTickable();
	}

	@Override
	public void tickServer() {
		// TODO implement
	}

}
