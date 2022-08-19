package matteroverdrive.common.tile;

import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryChunkloader;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.property.Property;
import matteroverdrive.core.property.PropertyTypes;
import matteroverdrive.core.tile.types.GenericUpgradableTile;
import matteroverdrive.registry.TileRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.block.state.BlockState;

public class TileChunkloader extends GenericUpgradableTile {

	public static final int SLOT_COUNT = 5;
	public static final int ENERGY_CAPACITY = 1024000;
	public static final int USAGE = 0;

	private boolean running = true;

	public boolean clientRunning;
	
	public final Property<CompoundTag> capInventoryProp;
	public final Property<CompoundTag> capEnergyStorageProp;

	public TileChunkloader(BlockPos pos, BlockState state) {
		super(TileRegistry.TILE_CHUNKLOADER.get(), pos, state);
		
		setPowerUsage(USAGE);
		
		defaultPowerStorage = ENERGY_CAPACITY;
		
		capInventoryProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.NBT.create(() -> getInventoryCap().serializeNBT(),
				tag -> getInventoryCap().deserializeNBT(tag)));
		capEnergyStorageProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.NBT.create(() -> getEnergyStorageCap().serializeNBT(),
				tag -> getEnergyStorageCap().deserializeNBT(tag)));
		
		addInventoryCap(new CapabilityInventory(SLOT_COUNT, false, false).setEnergySlots(1).setUpgrades(4)
				.setOwner(this).setValidator(machineValidator()).setValidUpgrades(InventoryChunkloader.UPGRADES).setPropertyManager(capInventoryProp));
		addEnergyStorageCap(new CapabilityEnergyStorage(ENERGY_CAPACITY, true, false).setOwner(this).setPropertyManager(capEnergyStorageProp));
		setMenuProvider(new SimpleMenuProvider((id, inv, play) -> new InventoryChunkloader(id, play.getInventory(),
				getInventoryCap(), getCoordsData()), getContainerName(TypeMachine.CHUNKLOADER.id())));
		setHasMenuData();
		setTickable();
	}

	@Override
	public void tickServer() {
		//TODO implement
	}

	@Override
	public void getMenuData(CompoundTag tag) {
		tag.putBoolean("running", running);
	}

	@Override
	public void readMenuData(CompoundTag tag) {
		clientRunning = tag.getBoolean("running");
	}

	@Override
	public double getCurrentPowerStorage() {
		return getEnergyStorageCap().getMaxEnergyStored();
	}

	@Override
	public void setPowerStorage(double storage) {
		getEnergyStorageCap().updateMaxEnergyStorage((int) storage);
	}

}
