package matteroverdrive.common.tile;

import matteroverdrive.MatterOverdrive;
import matteroverdrive.SoundRegister;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryMatterDecomposer;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.capability.types.matter.CapabilityMatterStorage;
import matteroverdrive.core.capability.types.matter.ICapabilityMatterStorage;
import matteroverdrive.core.matter.MatterRegister;
import matteroverdrive.core.sound.SoundBarrierMethods;
import matteroverdrive.core.tile.types.GenericSoundTile;
import matteroverdrive.core.utils.UtilsMatter;
import matteroverdrive.core.utils.UtilsNbt;
import matteroverdrive.core.utils.UtilsTile;
import matteroverdrive.registry.ItemRegistry;
import matteroverdrive.registry.TileRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class TileMatterDecomposer extends GenericSoundTile {

	public static final int SLOT_COUNT = 8;

	public static final int OPERATING_TIME = 500;
	private static final int USAGE_PER_TICK = 80;
	private static final float FAILURE_CHANCE = 0.005F;
	private static final int MATTER_STORAGE = 1024;
	private static final int ENERGY_STORAGE = 512000;
	private static final int DEFAULT_SPEED = 1;

	private double currRecipeValue = 0;
	private double currProgress = 0;

	public double clientRecipeValue;
	public double clientProgress;

	public TileMatterDecomposer(BlockPos pos, BlockState state) {
		super(TileRegistry.TILE_MATTER_DECOMPOSER.get(), pos, state);
		currentSpeed = DEFAULT_SPEED;
		currentFailureChance = FAILURE_CHANCE;
		currentPowerUsage = USAGE_PER_TICK;
		addInventoryCap(new CapabilityInventory(SLOT_COUNT, true, true).setInputs(1).setOutputs(1).setEnergySlots(1)
				.setMatterSlots(1).setUpgrades(4).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.UP }, new Direction[] { Direction.DOWN })
				.setValidator(machineValidator()).setValidUpgrades(InventoryMatterDecomposer.UPGRADES));
		addEnergyStorageCap(new CapabilityEnergyStorage(ENERGY_STORAGE, true, false).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.WEST, Direction.EAST }, null));
		addMatterStorageCap(
				new CapabilityMatterStorage(MATTER_STORAGE, false, true).setOwner(this).setDefaultDirections(state,
						null, new Direction[] { Direction.NORTH, Direction.EAST, Direction.WEST }));
		setMenuProvider(new SimpleMenuProvider((id, inv, play) -> new InventoryMatterDecomposer(id, play.getInventory(),
				getInventoryCap(), getCoordsData()), getContainerName(TypeMachine.MATTER_DECOMPOSER.id())));
		setHasMenuData();
		setTickable();
	}

	@Override
	public void tickServer() {
		boolean currState = getLevel().getBlockState(getBlockPos()).getValue(BlockStateProperties.LIT);
		if (currState && !isRunning) {
			UtilsTile.updateLit(this, Boolean.FALSE);
		} else if (!currState && isRunning) {
			UtilsTile.updateLit(this, Boolean.TRUE);
		}
		if (!canRun()) {
			currRecipeValue = 0;
			isRunning = false;
			currProgress = 0;
			return;
		}
		UtilsTile.drainElectricSlot(this);
		UtilsTile.fillMatterSlot(this);
		UtilsTile.outputMatter(this);
		CapabilityInventory inv = getInventoryCap();
		ItemStack input = inv.getInputs().get(0);
		if (input.isEmpty()) {
			isRunning = false;
			currRecipeValue = 0;
			currProgress = 0;
			return;
		}

		double matterVal = currRecipeValue > 0.0 ? currRecipeValue
				: MatterRegister.INSTANCE.getServerMatterValue(input);
		if (matterVal <= 0.0) {
			if (UtilsMatter.isRefinedDust(input)) {
				matterVal = UtilsNbt.readMatterVal(input);
			}
			if (matterVal <= 0.0) {
				isRunning = false;
				currRecipeValue = 0;
				currProgress = 0;
				return;
			}
		}
		CapabilityEnergyStorage energy = getEnergyStorageCap();
		if (energy.getEnergyStored() < getCurrentPowerUsage()) {
			isRunning = false;
			return;
		}

		currRecipeValue = matterVal;
		currRecipeValue += input.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE)
				.map(ICapabilityMatterStorage::getMatterStored).orElse(0.0);
		CapabilityMatterStorage storage = getMatterStorageCap();

		if ((storage.getMaxMatterStored() - storage.getMatterStored()) < currRecipeValue) {
			isRunning = false;
			return;
		}

		ItemStack output = inv.getOutputs().get(0);

		if (!(output.isEmpty() || (UtilsNbt.readMatterVal(output) == currRecipeValue
				&& (output.getCount() + 1 <= output.getMaxStackSize())))) {
			isRunning = false;
			return;
		}
		isRunning = true;
		currProgress += getCurrentSpeed();
		energy.removeEnergy((int) getCurrentPowerUsage());
		if (currProgress >= OPERATING_TIME) {
			if (roll() < getCurrentFailure()) {
				if (output.isEmpty()) {
					ItemStack dust = new ItemStack(ItemRegistry.ITEM_RAW_MATTER_DUST.get());
					UtilsNbt.writeMatterVal(dust, currRecipeValue);
					inv.setStackInSlot(1, dust.copy());
				} else {
					output.grow(1);
				}
				input.shrink(1);
			} else {
				storage.giveMatter(currRecipeValue);
				input.shrink(1);
			}
			currProgress = 0;
		}
		setChanged();

	}

	@Override
	public void tickClient() {
		if (shouldPlaySound() && !clientSoundPlaying) {
			clientSoundPlaying = true;
			SoundBarrierMethods.playTileSound(SoundRegister.SOUND_DECOMPOSER.get(), this, true);
		}
	}

	@Override
	public void getMenuData(CompoundTag tag) {
		
		tag.putDouble("recipe", currRecipeValue);
		tag.putDouble("progress", currProgress);

	}

	@Override
	public void readMenuData(CompoundTag tag) {
		clientRecipeValue = tag.getDouble("recipe");
		clientProgress = tag.getDouble("progress");
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);

		CompoundTag additional = new CompoundTag();
		additional.putDouble("progress", currProgress);
		additional.putDouble("speed", currentSpeed);
		additional.putFloat("failure", currentFailureChance);
		additional.putDouble("usage", currentPowerUsage);
		additional.putBoolean("muffled", isMuffled);

		tag.put("additional", additional);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);

		CompoundTag additional = tag.getCompound("additional");
		currProgress = additional.getDouble("progress");
		currentSpeed = additional.getDouble("speed");
		currentFailureChance = additional.getFloat("failure");
		currentPowerUsage = additional.getDouble("usage");
		isMuffled = additional.getBoolean("muffled");
	}

	@Override
	public double getDefaultSpeed() {
		return DEFAULT_SPEED;
	}

	@Override
	public float getDefaultFailure() {
		return FAILURE_CHANCE;
	}

	@Override
	public double getDefaultMatterStorage() {
		return MATTER_STORAGE;
	}

	@Override
	public double getDefaultPowerStorage() {
		return ENERGY_STORAGE;
	}

	@Override
	public double getDefaultPowerUsage() {
		return USAGE_PER_TICK;
	}

	@Override
	public double getCurrentMatterStorage() {
		return getMatterStorageCap().getMaxMatterStored();
	}

	@Override
	public double getCurrentPowerStorage() {
		return getEnergyStorageCap().getMaxEnergyStored();
	}

	@Override
	public void setMatterStorage(double storage) {
		getMatterStorageCap().updateMaxMatterStorage(storage);
	}

	@Override
	public void setPowerStorage(double storage) {
		getEnergyStorageCap().updateMaxEnergyStorage((int) storage);
	}

	@Override
	public double getProcessingTime() {
		return OPERATING_TIME;
	}

	private float roll() {
		return MatterOverdrive.RANDOM.nextFloat();
	}

}
