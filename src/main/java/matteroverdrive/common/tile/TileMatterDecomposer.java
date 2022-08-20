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
import matteroverdrive.core.property.Property;
import matteroverdrive.core.property.PropertyTypes;
import matteroverdrive.core.sound.SoundBarrierMethods;
import matteroverdrive.core.tile.types.GenericMachineTile;
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

public class TileMatterDecomposer extends GenericMachineTile {

	public static final int SLOT_COUNT = 8;

	public static final int OPERATING_TIME = 500;
	private static final int USAGE_PER_TICK = 80;
	private static final float FAILURE_CHANCE = 0.005F;
	private static final int MATTER_STORAGE = 1024;
	private static final int ENERGY_STORAGE = 512000;
	private static final int DEFAULT_SPEED = 1;
	
	public final Property<CompoundTag> capInventoryProp;
	public final Property<CompoundTag> capEnergyStorageProp;
	public final Property<CompoundTag> capMatterStorageProp;

	public TileMatterDecomposer(BlockPos pos, BlockState state) {
		super(TileRegistry.TILE_MATTER_DECOMPOSER.get(), pos, state);
		
		setSpeed(DEFAULT_SPEED);
		setFailure(FAILURE_CHANCE);
		setPowerUsage(USAGE_PER_TICK);
		
		defaultSpeed = DEFAULT_SPEED;
		defaultFailureChance = FAILURE_CHANCE;
		defaultMatterStorage = MATTER_STORAGE;
		defaultPowerStorage = ENERGY_STORAGE;
		defaultPowerUsage = USAGE_PER_TICK;
		defaultProcessingTime = OPERATING_TIME;
		
		capInventoryProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.NBT.create(() -> getInventoryCap().serializeNBT(),
				tag -> getInventoryCap().deserializeNBT(tag)));
		capMatterStorageProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.NBT.create(() -> getMatterStorageCap().serializeNBT(),
				tag -> getMatterStorageCap().deserializeNBT(tag)));
		capEnergyStorageProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.NBT.create(() -> getEnergyStorageCap().serializeNBT(),
				tag -> getEnergyStorageCap().deserializeNBT(tag)));
		
		addInventoryCap(new CapabilityInventory(SLOT_COUNT, true, true).setInputs(1).setOutputs(1).setEnergySlots(1)
				.setMatterSlots(1).setUpgrades(4).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.UP }, new Direction[] { Direction.DOWN })
				.setValidator(machineValidator()).setValidUpgrades(InventoryMatterDecomposer.UPGRADES).setPropertyManager(capInventoryProp));
		addEnergyStorageCap(new CapabilityEnergyStorage(ENERGY_STORAGE, true, false).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.WEST, Direction.EAST }, null).setPropertyManager(capEnergyStorageProp));
		addMatterStorageCap(
				new CapabilityMatterStorage(MATTER_STORAGE, false, true).setOwner(this).setDefaultDirections(state,
						null, new Direction[] { Direction.NORTH, Direction.EAST, Direction.WEST }).setPropertyManager(capMatterStorageProp));
		setMenuProvider(new SimpleMenuProvider((id, inv, play) -> new InventoryMatterDecomposer(id, play.getInventory(),
				getInventoryCap(), getCoordsData()), getContainerName(TypeMachine.MATTER_DECOMPOSER.id())));
		setTickable();
	}

	@Override
	public void tickServer() {
		boolean currState = getLevel().getBlockState(getBlockPos()).getValue(BlockStateProperties.LIT);
		if (currState && !isRunning()) {
			UtilsTile.updateLit(this, Boolean.FALSE);
		} else if (!currState && isRunning()) {
			UtilsTile.updateLit(this, Boolean.TRUE);
		}
		boolean flag = false;
		if (!canRun()) {
			flag = setRecipeValue(0);
			flag |= setRunning(false);
			flag |= setProgress(0);
			if(flag) {
				setChanged();
			}
			return;
		}
		UtilsTile.drainElectricSlot(this);
		UtilsTile.fillMatterSlot(this);
		UtilsTile.outputMatter(this);
		CapabilityInventory inv = getInventoryCap();
		ItemStack input = inv.getInputs().get(0);
		if (input.isEmpty()) {
			flag = setRunning(false);
			flag |= setRecipeValue(0);
			flag |= setProgress(0);
			if(flag) {
				setChanged();
			}
			return;
		}

		double matterVal = getRecipeValue() > 0.0 ? getRecipeValue()
				: MatterRegister.INSTANCE.getServerMatterValue(input);
		if (matterVal <= 0.0) {
			if (UtilsMatter.isRefinedDust(input)) {
				matterVal = UtilsNbt.readMatterVal(input);
			}
			if (matterVal <= 0.0) {
				flag = setRunning(false);
				flag |= setRecipeValue(0);
				flag |= setProgress(0);
				if(flag) {
					setChanged();
				}
				return;
			}
		}
		CapabilityEnergyStorage energy = getEnergyStorageCap();
		if (energy.getEnergyStored() < getCurrentPowerUsage()) {
			if(setRunning(false)) {
				setChanged();
			}
			return;
		}

		setRecipeValue(matterVal + input.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE)
				.map(ICapabilityMatterStorage::getMatterStored).orElse(0.0));

		CapabilityMatterStorage storage = getMatterStorageCap();

		if ((storage.getMaxMatterStored() - storage.getMatterStored()) < getRecipeValue()) {
			if(setRunning(false)) {
				setChanged();
			}
			return;
		}

		ItemStack output = inv.getOutputs().get(0);

		if (!(output.isEmpty() || (UtilsNbt.readMatterVal(output) == getRecipeValue()
				&& (output.getCount() + 1 <= output.getMaxStackSize())))) {
			if(setRunning(false)) {
				setChanged();
			}
			return;
		}
		setRunning(true);
		incrementProgress(getCurrentSpeed());
		energy.removeEnergy((int) getCurrentPowerUsage());
		if (getProgress() >= OPERATING_TIME) {
			if (roll() < getCurrentFailure()) {
				if (output.isEmpty()) {
					ItemStack dust = new ItemStack(ItemRegistry.ITEM_RAW_MATTER_DUST.get());
					UtilsNbt.writeMatterVal(dust, getRecipeValue());
					inv.setStackInSlot(1, dust.copy());
				} else {
					output.grow(1);
				}
				input.shrink(1);
			} else {
				storage.giveMatter(getRecipeValue());
				input.shrink(1);
			}
			setProgress(0);
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
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);

		CompoundTag additional = new CompoundTag();
		additional.putDouble("progress", getProgress());
		additional.putDouble("speed", getCurrentSpeed());
		additional.putFloat("failure", getCurrentFailure());
		additional.putDouble("usage", getCurrentPowerUsage());
		additional.putBoolean("muffled", isMuffled());

		tag.put("additional", additional);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);

		CompoundTag additional = tag.getCompound("additional");
		setProgress(additional.getDouble("progress"));
		setSpeed(additional.getDouble("speed"));
		setFailure(additional.getFloat("failure"));
		setPowerUsage(additional.getDouble("usage"));
		setMuffled(additional.getBoolean("muffled"));
	}
	
	@Override
	public void getFirstContactData(CompoundTag tag) {
		saveAdditional(tag);
	}

	private float roll() {
		return MatterOverdrive.RANDOM.nextFloat();
	}

}
