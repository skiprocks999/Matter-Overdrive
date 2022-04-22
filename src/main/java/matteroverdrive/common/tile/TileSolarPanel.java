package matteroverdrive.common.tile;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.inventory.InventorySolarPanel;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.tile.GenericTile;
import matteroverdrive.core.tile.IRedstoneMode;
import matteroverdrive.core.tile.utils.PacketHandler;
import matteroverdrive.core.tile.utils.Ticker;
import matteroverdrive.core.utils.UtilsTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class TileSolarPanel extends GenericTile implements IRedstoneMode {

	public static final int SLOT_COUNT = 2;
	public static final int GENERATION = 5;

	private int currRedstoneMode;
	private boolean generating = false;
	private int generatingBonus = 1;

	public int clientRedstoneMod;
	public boolean clientGenerating;
	public int clientGeneratingBonus;
	public int clientStored;
	public int clientMaxStorage;

	public TileSolarPanel(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_SOLAR_PANEL.get(), pos, state);
		addCapability(new CapabilityInventory(SLOT_COUNT, false, false).setUpgrades(SLOT_COUNT).setOwner(this));
		addCapability(new CapabilityEnergyStorage(64000, false, true).setOwner(this).setDefaultDirections(state, null,
				new Direction[] { Direction.DOWN }));
		setMenuProvider(
				new SimpleMenuProvider(
						(id, inv, play) -> new InventorySolarPanel(id, play.getInventory(),
								exposeCapability(CapabilityType.Item), getCoordsData()),
						getContainerName("solar_panel")));
		setTicker(new Ticker(this).tickServer(this::tickServer));
		setMenuPacketHandler(
				new PacketHandler(this, true).packetReader(this::clientLoad).packetWriter(this::clientSave));
	}

	private void tickServer(Ticker ticker) {
		if (ticker.getTicks() % 5 == 0) {
			Level world = getLevel();
			generating = world.isDay() && world.canSeeSky(getBlockPos());
		}
		if (generating) {
			CapabilityEnergyStorage energy = exposeCapability(CapabilityType.Energy);
			energy.giveEnergy(GENERATION * generatingBonus);
		}
		UtilsTile.outputEnergy(this);
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
		tag.putBoolean("generating", generating);
		tag.putInt("bonus", generatingBonus);
		CapabilityEnergyStorage energy = exposeCapability(CapabilityType.Energy);
		tag.putInt("stored", energy.getEnergyStored());
		tag.putInt("maxstore", energy.getMaxEnergyStored());
	}

	private void clientLoad(CompoundTag tag) {
		clientRedstoneMod = tag.getInt("redstone");
		clientGenerating = tag.getBoolean("generating");
		clientGeneratingBonus = tag.getInt("bonus");
		clientStored = tag.getInt("stored");
		clientMaxStorage = tag.getInt("maxstore");
	}

	@Override
	public int getMaxMode() {
		return 2;
	}

}
