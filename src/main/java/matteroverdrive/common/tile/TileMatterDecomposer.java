package matteroverdrive.common.tile;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.inventory.InventoryMatterDecomposer;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.capability.types.matter.CapabilityMatterStorage;
import matteroverdrive.core.tile.GenericTile;
import matteroverdrive.core.tile.IRedstoneMode;
import matteroverdrive.core.tile.utils.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.block.state.BlockState;

public class TileMatterDecomposer extends GenericTile implements IRedstoneMode {

	public static final int SLOT_COUNT = 8;
	
	private int currRedstoneMode;
	private boolean running = false;
	private int usage = 0;
	private int currRecipeValue = 0;

	public int clientRedstoneMode;
	public boolean clientRunning;
	public int clientEnergyUsage;
	public int clientEnergyStored;
	public int clientMaxEnergyStorage;
	
	public int clientMatterStored;
	public int clientMaxMatterStorage;
	public int clientCurrRecipeValue;

	public TileMatterDecomposer(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_MATTER_DECOMPOSER.get(), pos, state);
		addCapability(new CapabilityInventory(SLOT_COUNT).setInputs(1).setOutputs(1).setEnergySlots(1).setMatterSlots(1)
				.setUpgrades(4).setOwner(this));
		addCapability(new CapabilityEnergyStorage(512000, true, false).setOwner(this));
		addCapability(new CapabilityMatterStorage(1024, false, true).setOwner(this));
		setMenuProvider(new SimpleMenuProvider(
				(id, inv, play) -> new InventoryMatterDecomposer(id, play.getInventory(),
						exposeCapability(CapabilityType.Item), getCoordsData()),
				getContainerName("matter_decomposer")));
		setMenuPacketHandler(
				new PacketHandler(this, true).packetReader(this::clientLoad).packetWriter(this::clientSave));
	}

	@Override
	public void setMode(int mode) {
		currRedstoneMode = mode;
	}

	@Override
	public int getCurrMod() {
		return currRedstoneMode;
	}

	@Override
	public int getMaxMode() {
		return 2;
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		saveMode(tag);
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		loadMode(tag);
	}
	
	private void clientSave(CompoundTag tag) {
		tag.putInt("redstone", currRedstoneMode);
		tag.putBoolean("running", running);
		tag.putInt("usage", usage);
		CapabilityEnergyStorage energy = exposeCapability(CapabilityType.Energy);
		tag.putInt("stored", energy.getEnergyStored());
		tag.putInt("maxstore", energy.getMaxEnergyStored());
		CapabilityMatterStorage matter = exposeCapability(CapabilityType.Matter);
		tag.putInt("mattStored", matter.getMatterStored());
		tag.putInt("mattMax", matter.getMaxMatterStored());
		tag.putInt("recipe", currRecipeValue);
	}

	private void clientLoad(CompoundTag tag) {
		clientRedstoneMode = tag.getInt("redstone");
		clientRunning = tag.getBoolean("running");
		clientEnergyUsage = tag.getInt("usage");
		clientEnergyStored = tag.getInt("stored");
		clientMaxEnergyStorage = tag.getInt("maxstore");
		clientMatterStored = tag.getInt("mattStored");
		clientMaxMatterStorage = tag.getInt("mattMax");
		clientCurrRecipeValue = tag.getInt("recipe");
	}

}
