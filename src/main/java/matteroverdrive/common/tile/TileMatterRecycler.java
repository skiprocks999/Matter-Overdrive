package matteroverdrive.common.tile;

import matteroverdrive.SoundRegister;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryMatterRecycler;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
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

public class TileMatterRecycler extends GenericMachineTile {

	public static final int SLOT_COUNT = 7;

	public static final int OPERATING_TIME = 50;
	public static final int USAGE_PER_TICK = 30;
	public static final int ENERGY_STORAGE = 512000;
	public static final int DEFAULT_SPEED = 1;

	public final Property<CompoundTag> capInventoryProp;
	public final Property<CompoundTag> capEnergyStorageProp;

	public TileMatterRecycler(BlockPos pos, BlockState state) {
		super(TileRegistry.TILE_MATTER_RECYCLER.get(), pos, state);

		setSpeed(DEFAULT_SPEED);
		setPowerUsage(USAGE_PER_TICK);
		setProcessingTime(OPERATING_TIME);

		defaultSpeed = DEFAULT_SPEED;
		defaultPowerStorage = ENERGY_STORAGE;
		defaultPowerUsage = USAGE_PER_TICK;

		capInventoryProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.NBT
				.create(() -> getInventoryCap().serializeNBT(), tag -> getInventoryCap().deserializeNBT(tag)));
		capEnergyStorageProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.NBT
				.create(() -> getEnergyStorageCap().serializeNBT(), tag -> getEnergyStorageCap().deserializeNBT(tag)));

		addInventoryCap(new CapabilityInventory(SLOT_COUNT, true, true).setInputs(1).setOutputs(1).setEnergySlots(1)
				.setUpgrades(4).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.UP, Direction.NORTH },
						new Direction[] { Direction.DOWN })
				.setValidator(machineValidator()).setValidUpgrades(InventoryMatterRecycler.UPGRADES)
				.setPropertyManager(capInventoryProp));
		addEnergyStorageCap(new CapabilityEnergyStorage(ENERGY_STORAGE, true, false).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.WEST, Direction.EAST }, null)
				.setPropertyManager(capEnergyStorageProp));
		setMenuProvider(new SimpleMenuProvider((id, inv, play) -> new InventoryMatterRecycler(id, play.getInventory(),
				getInventoryCap(), getCoordsData()), getContainerName(TypeMachine.MATTER_RECYCLER.id())));
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
			flag = setRunning(false);
			flag |= setProgress(0);
			if (flag) {
				setChanged();
			}
			return;
		}

		UtilsTile.drainElectricSlot(this);
		CapabilityInventory inv = getInventoryCap();
		ItemStack input = inv.getInputs().get(0);

		if (input.isEmpty() || !UtilsMatter.isRawDust(input)) {
			flag = setRunning(false);
			flag |= setProgress(0);
			if (flag) {
				setChanged();
			}
			return;
		}

		double value = UtilsNbt.readMatterVal(input);
		if (value <= 0) {
			flag = setRunning(false);
			flag |= setProgress(0);
			if (flag) {
				setChanged();
			}
			return;
		}

		CapabilityEnergyStorage energy = getEnergyStorageCap();

		if (energy.getEnergyStored() < getCurrentPowerUsage()) {
			if (setRunning(false)) {
				setChanged();
			}
			return;
		}

		ItemStack output = inv.getOutputs().get(0);
		if (!(output.isEmpty()
				|| (output.getCount() < output.getMaxStackSize() && UtilsNbt.readMatterVal(output) == value))) {
			if (setRunning(false)) {
				setChanged();
			}
			return;
		}

		setRunning(true);
		incrementProgress(getCurrentSpeed());
		energy.removeEnergy((int) getCurrentPowerUsage());
		if (getProgress() >= OPERATING_TIME) {
			setProgress(0);
			input.shrink(1);
			if (output.isEmpty()) {
				ItemStack refinedDust = new ItemStack(ItemRegistry.ITEM_MATTER_DUST.get(), 1);
				UtilsNbt.writeMatterVal(refinedDust, value);
				inv.setStackInSlot(1, refinedDust.copy());
			} else {
				output.grow(1);
			}
		}
		setChanged();

	}

	@Override
	public void tickClient() {
		if (shouldPlaySound() && !clientSoundPlaying) {
			clientSoundPlaying = true;
			SoundBarrierMethods.playTileSound(SoundRegister.SOUND_MACHINE.get(), this, 1.0F, 1.0F, true);
		}
	}

}
