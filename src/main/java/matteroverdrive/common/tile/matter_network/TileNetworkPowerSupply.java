package matteroverdrive.common.tile.matter_network;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryNetworkPowerSupply;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.network.cable.utils.IMatterNetworkMember;
import matteroverdrive.core.tile.types.GenericUpgradableTile;
import matteroverdrive.core.utils.UtilsTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TileNetworkPowerSupply extends GenericUpgradableTile implements IMatterNetworkMember {

	public static final int SLOT_COUNT = 5;
	public static final int ENERGY_CAPACITY = 1024000;
	
	private int usagePerTick;
	private boolean running = true;
	
	public int clientUsage;
	public boolean clientRunning;
	
	public CapabilityEnergyStorage clientEnergy;
	
	public TileNetworkPowerSupply(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_NETWORK_POWER_SUPPLY.get(), pos, state);
		addCapability(new CapabilityInventory(SLOT_COUNT, false, false).setEnergySlots(1).setUpgrades(4).setOwner(this)
				.setValidator(machineValidator()).setValidUpgrades(InventoryNetworkPowerSupply.UPGRADES));
		addCapability(new CapabilityEnergyStorage(ENERGY_CAPACITY, true, false).setOwner(this));
		setMenuProvider(
				new SimpleMenuProvider(
						(id, inv, play) -> new InventoryNetworkPowerSupply(id, play.getInventory(),
								exposeCapability(CapabilityType.Item), getCoordsData()),
						getContainerName(TypeMachine.NETWORK_POWER_SUPPLY.id())));
		setHasMenuData();
		setTickable();
	}
	
	@Override
	public void tickServer() {
		
	}
	
	@Override
	public void getMenuData(CompoundTag tag) {
		CapabilityEnergyStorage energy = exposeCapability(CapabilityType.Energy);
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
	public boolean canConnectToFace(Direction face) {
		return true;
	}

	@Override
	public int getMaxMode() {
		return 2;
	}
	
	@Override
	public double getDefaultPowerStorage() {
		return ENERGY_CAPACITY;
	}
	
	@Override
	public double getCurrentPowerStorage(boolean clientSide) {
		return clientSide ? clientEnergy.getMaxEnergyStored()
				: this.<CapabilityEnergyStorage>exposeCapability(CapabilityType.Energy).getMaxEnergyStored();
	}

	@Override
	public void setPowerStorage(int storage) {
		CapabilityEnergyStorage energy = exposeCapability(CapabilityType.Energy);
		energy.updateMaxEnergyStorage(storage);
	}

}
