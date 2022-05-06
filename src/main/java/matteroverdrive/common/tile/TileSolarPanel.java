package matteroverdrive.common.tile;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventorySolarPanel;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.tile.types.GenericUpgradableTile;
import matteroverdrive.core.tile.utils.PacketHandler;
import matteroverdrive.core.tile.utils.Ticker;
import matteroverdrive.core.utils.UtilsTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class TileSolarPanel extends GenericUpgradableTile {

	public static final int SLOT_COUNT = 2;
	public static final int GENERATION = 5;

	private static final int ENERGY_STORAGE = 64000;

	private boolean generating = false;
	private int generatingBonus = 1;

	public boolean clientGenerating;
	public int clientGeneratingBonus;
	public CapabilityEnergyStorage clientEnergy;

	public TileSolarPanel(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_SOLAR_PANEL.get(), pos, state);
		addCapability(new CapabilityInventory(SLOT_COUNT, false, false).setUpgrades(SLOT_COUNT).setOwner(this)
				.setValidator(machineValidator()).setValidUpgrades(InventorySolarPanel.UPGRADES));
		addCapability(new CapabilityEnergyStorage(ENERGY_STORAGE, false, true).setOwner(this)
				.setDefaultDirections(state, null, new Direction[] { Direction.DOWN }));
		setMenuProvider(
				new SimpleMenuProvider(
						(id, inv, play) -> new InventorySolarPanel(id, play.getInventory(),
								exposeCapability(CapabilityType.Item), getCoordsData()),
						getContainerName(TypeMachine.SOLAR_PANEL.id())));
		setTicker(new Ticker(this).tickServer(this::tickServer));
		setMenuPacketHandler(
				new PacketHandler(this, true).packetReader(this::clientLoad).packetWriter(this::clientSave));
	}

	private void tickServer(Ticker ticker) {
		if (canRun()) {
			if (ticker.getTicks() % 5 == 0) {
				Level world = getLevel();
				generating = world.isDay() && world.canSeeSky(getBlockPos());
			}
			if (generating) {
				CapabilityEnergyStorage energy = exposeCapability(CapabilityType.Energy);
				energy.giveEnergy(GENERATION * generatingBonus);
			}
			UtilsTile.outputEnergy(this);
		} else {
			generating = false;
		}
	}

	private void clientSave(CompoundTag tag) {
		CapabilityEnergyStorage energy = exposeCapability(CapabilityType.Energy);
		tag.put(energy.getSaveKey(), energy.serializeNBT());

		tag.putInt("redstone", currRedstoneMode);
		tag.putBoolean("generating", generating);
		tag.putInt("bonus", generatingBonus);
	}

	private void clientLoad(CompoundTag tag) {
		clientEnergy = new CapabilityEnergyStorage(0, false, false);
		clientEnergy.deserializeNBT(tag.getCompound(clientEnergy.getSaveKey()));

		clientRedstoneMode = tag.getInt("redstone");
		clientGenerating = tag.getBoolean("generating");
		clientGeneratingBonus = tag.getInt("bonus");
	}

	@Override
	public int getMaxMode() {
		return 2;
	}

	@Override
	public double getDefaultPowerStorage() {
		return ENERGY_STORAGE;
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
