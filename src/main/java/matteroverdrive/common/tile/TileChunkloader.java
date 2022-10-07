package matteroverdrive.common.tile;

import matteroverdrive.MatterOverdrive;
import matteroverdrive.References;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryChunkloader;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.capability.types.matter.CapabilityMatterStorage;
import matteroverdrive.core.config.MatterOverdriveConfig;
import matteroverdrive.core.property.Property;
import matteroverdrive.core.property.PropertyTypes;
import matteroverdrive.core.tile.types.GenericMachineTile;
import matteroverdrive.core.utils.UtilsTile;
import matteroverdrive.registry.TileRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.world.ForgeChunkManager;

public class TileChunkloader extends GenericMachineTile {

	public static final int SLOT_COUNT = 6;
	public static final int ENERGY_CAPACITY = 512000;
	public static final int MATTER_CAPACITY = 1024;
	public static final int POWER_USAGE = 1000;
	public static final double MATTER_USAGE = 1.5;
	public static final double BASE_RANGE = 1;

	public final Property<CompoundTag> capInventoryProp;
	public final Property<CompoundTag> capEnergyStorageProp;
	public final Property<CompoundTag> capMatterStorageProp;

	private boolean didRangeChange = true;

	public TileChunkloader(BlockPos pos, BlockState state) {
		super(TileRegistry.TILE_CHUNKLOADER.get(), pos, state);

		setPowerUsage(POWER_USAGE);
		setMatterUsage(MATTER_USAGE);
		setRange(BASE_RANGE);

		defaultPowerStorage = ENERGY_CAPACITY;
		defaultMatterStorage = MATTER_CAPACITY;
		defaultPowerUsage = POWER_USAGE;
		defaultMatterUsage = MATTER_USAGE;
		defaultRange = BASE_RANGE;

		capInventoryProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.NBT
				.create(() -> getInventoryCap().serializeNBT(), tag -> getInventoryCap().deserializeNBT(tag)));
		capEnergyStorageProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.NBT
				.create(() -> getEnergyStorageCap().serializeNBT(), tag -> getEnergyStorageCap().deserializeNBT(tag)));
		capMatterStorageProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.NBT
				.create(() -> getMatterStorageCap().serializeNBT(), tag -> getMatterStorageCap().deserializeNBT(tag)));

		addInventoryCap(new CapabilityInventory(SLOT_COUNT, false, false).setEnergyInputSlots(1).setMatterInputSlots(1)
				.setUpgrades(4).setOwner(this).setValidator(machineValidator())
				.setValidUpgrades(InventoryChunkloader.UPGRADES).setPropertyManager(capInventoryProp));
		addEnergyStorageCap(new CapabilityEnergyStorage(ENERGY_CAPACITY, true, false).setOwner(this)
				.setPropertyManager(capEnergyStorageProp));
		addMatterStorageCap(new CapabilityMatterStorage(MATTER_CAPACITY, true, false).setOwner(this)
				.setPropertyManager(capMatterStorageProp));
		setMenuProvider(new SimpleMenuProvider((id, inv, play) -> new InventoryChunkloader(id, play.getInventory(),
				getInventoryCap(), getCoordsData()), getContainerName(TypeMachine.CHUNKLOADER.id())));
		setTickable();
	}

	@Override
	public void tickServer() {
		UtilsTile.drainElectricSlot(this);
		UtilsTile.drainMatterSlot(this);
		
		handleOnState();
		
		if (!canRun()) {
			if(isRunning()) {
				updateChunks(false, getLevel(), getBlockPos());
				didRangeChange = true;
			}
			setShouldSaveData(setRunning(false), updateTickable(false));
			return;
		}
		CapabilityMatterStorage matter = getMatterStorageCap();
		if (matter.getMatterStored() < getCurrentMatterUsage()) {
			if(isRunning()) {
				updateChunks(false, getLevel(), getBlockPos());
				didRangeChange = true;
			}
			setShouldSaveData(setRunning(false), updateTickable(false));
			return;
		}
		CapabilityEnergyStorage energy = getEnergyStorageCap();
		if (energy.getEnergyStored() < getCurrentPowerUsage()) {
			if(isRunning()) {
				updateChunks(false, getLevel(), getBlockPos());
				didRangeChange = true;
			}
			setShouldSaveData(setRunning(false), updateTickable(false));
			return;
		}
		matter.removeMatter(getCurrentMatterUsage());
		energy.removeEnergy((int) getCurrentPowerUsage());
		setRunning(true);
		if (didRangeChange) {
			updateChunks(true, getLevel(), getBlockPos());
			didRangeChange = false;
		}
		setShouldSaveData(true);
	}

	@Override
	public void onUpgradesChange(double[] prevValues, double[] newValues) {
		double oldRange = prevValues[6];
		double newRange = newValues[6];
		if (oldRange != newRange) {
			didRangeChange = true;
		}
	}

	@Override
	public boolean setRange(double range) {
		if (range > MatterOverdriveConfig.CHUNKLOADER_RANGE.get()) {
			range = MatterOverdriveConfig.CHUNKLOADER_RANGE.get();
		}
		return super.setRange(range);
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putBoolean("didrangechange", didRangeChange);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		didRangeChange = tag.getBoolean("didrangechange");
	}
	
	@Override
	public void onBlockBroken(Level world, BlockPos pos) {
		updateChunks(false, world, pos);
	}

	private void updateChunks(boolean load, Level world, BlockPos pos) {

		int offset = (int) (getCurrentRange() - 1);
		ChunkPos currChunkPos = world.getChunk(pos).getPos();
		int lowerXOffset = currChunkPos.x - offset;
		int lowerZOffset = currChunkPos.z - offset;

		int delta = currChunkPos.x + offset - lowerXOffset;

		BlockPos[][] ownerPos = new BlockPos[delta + 1][delta + 1];

		BlockPos bottomLeft = pos.offset(-16 * offset, 0, -16 * offset);

		for (int i = 0; i <= delta; i++) {
			for (int j = 0; j <= delta; j++) {
				ownerPos[i][j] = bottomLeft.offset(16 * i, 0, 16 * j);
			}
		}

		for (int i = 0; i <= delta; i++) {
			for (int j = 0; j <= delta; j++) {
				ForgeChunkManager.forceChunk((ServerLevel) world, References.ID, ownerPos[i][j], lowerXOffset + i,
						lowerZOffset + j, load, true);
				String action = load ? "loading" : "unloading";
				MatterOverdrive.LOGGER.info(action + " chunk at " + lowerXOffset + i + "," + lowerZOffset + j);
			}
		}

	}

}
