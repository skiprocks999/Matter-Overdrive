package matteroverdrive.common.tile;

import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryChunkloader;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.tile.types.GenericUpgradableTile;
import matteroverdrive.registry.TileRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.block.state.BlockState;

public class TileChunkloader extends GenericUpgradableTile {

	public static final int SLOT_COUNT = 5;
	public static final int ENERGY_CAPACITY = 1024000;

	private int usagePerTick;
	private boolean running = true;

	public int clientUsage;
	public boolean clientRunning;

	public CapabilityEnergyStorage clientEnergy;

	public TileChunkloader(BlockPos pos, BlockState state) {
		super(TileRegistry.TILE_CHUNKLOADER.get(), pos, state);
		addInventoryCap(new CapabilityInventory(SLOT_COUNT, false, false).setEnergySlots(1).setUpgrades(4)
				.setOwner(this).setValidator(machineValidator()).setValidUpgrades(InventoryChunkloader.UPGRADES));
		addEnergyStorageCap(new CapabilityEnergyStorage(ENERGY_CAPACITY, true, false).setOwner(this));
		setMenuProvider(new SimpleMenuProvider((id, inv, play) -> new InventoryChunkloader(id, play.getInventory(),
				getInventoryCap(), getCoordsData()), getContainerName(TypeMachine.CHUNKLOADER.id())));
		setHasMenuData();
		setTickable();
	}

	@Override
	public void tickServer() {

	}

	@Override
	public void getMenuData(CompoundTag tag) {
		CapabilityEnergyStorage energy = getEnergyStorageCap();
		tag.put(energy.getSaveKey(), energy.serializeNBT());

		tag.putInt("usage", usagePerTick);
		tag.putDouble("sabonus", saMultiplier);
		tag.putBoolean("running", running);
	}

	@Override
	public void readMenuData(CompoundTag tag) {
		clientEnergy = new CapabilityEnergyStorage(0, false, false);
		clientEnergy.deserializeNBT(tag.getCompound(clientEnergy.getSaveKey()));

		clientUsage = tag.getInt("usage");
		clientSAMultipler = tag.getDouble("sabonus");
		clientRunning = tag.getBoolean("running");
	}

	@Override
	public double getDefaultPowerStorage() {
		return ENERGY_CAPACITY;
	}

	@Override
	public double getCurrentPowerStorage(boolean clientSide) {
		return clientSide ? clientEnergy.getMaxEnergyStored() : getEnergyStorageCap().getMaxEnergyStored();
	}

	@Override
	public void setPowerStorage(int storage) {
		getEnergyStorageCap().updateMaxEnergyStorage(storage);
	}

}
