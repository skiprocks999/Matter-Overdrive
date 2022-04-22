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
import net.minecraft.core.Direction;
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
	public int clientRecipeValue;

	public CapabilityInventory clientInventory;
	public CapabilityEnergyStorage clientEnergy;
	public CapabilityMatterStorage clientMatter;

	public TileMatterDecomposer(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_MATTER_DECOMPOSER.get(), pos, state);

		addCapability(new CapabilityInventory(SLOT_COUNT, true, true).setInputs(1).setOutputs(1).setEnergySlots(1)
				.setMatterSlots(1).setUpgrades(4).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.UP }, new Direction[] { Direction.DOWN }));
		addCapability(new CapabilityEnergyStorage(512000, true, false).setOwner(this).setDefaultDirections(state,
				new Direction[] { Direction.WEST, Direction.EAST }, null));
		addCapability(new CapabilityMatterStorage(1024, false, true).setOwner(this).setDefaultDirections(state, null,
				new Direction[] { Direction.SOUTH }));

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
		tag.putInt("recipe", currRecipeValue);
		CapabilityInventory inv = exposeCapability(CapabilityType.Item);
		tag.put(inv.getSaveKey(), inv.serializeNBT());
		CapabilityEnergyStorage energy = exposeCapability(CapabilityType.Energy);
		tag.put(energy.getSaveKey(), energy.serializeNBT());
		CapabilityMatterStorage matter = exposeCapability(CapabilityType.Matter);
		tag.put(matter.getSaveKey(), matter.serializeNBT());
	}

	private void clientLoad(CompoundTag tag) {
		clientRedstoneMode = tag.getInt("redstone");
		clientRunning = tag.getBoolean("running");
		clientEnergyUsage = tag.getInt("usage");
		clientRecipeValue = tag.getInt("recipe");
		clientInventory = new CapabilityInventory();
		clientInventory.deserializeNBT(tag.getCompound(clientInventory.getSaveKey()));
		clientEnergy = new CapabilityEnergyStorage(0, false, false);
		clientEnergy.deserializeNBT(tag.getCompound(clientEnergy.getSaveKey()));
		clientMatter = new CapabilityMatterStorage(0, false, false);
		clientMatter.deserializeNBT(tag.getCompound(clientMatter.getSaveKey()));
	}

}
