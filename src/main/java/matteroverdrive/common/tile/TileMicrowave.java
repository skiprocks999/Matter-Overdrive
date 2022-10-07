package matteroverdrive.common.tile;

import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryMicrowave;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.property.Property;
import matteroverdrive.core.property.PropertyTypes;
import matteroverdrive.core.sound.SoundBarrierMethods;
import matteroverdrive.core.tile.types.GenericMachineTile;
import matteroverdrive.core.utils.UtilsTile;
import matteroverdrive.registry.SoundRegistry;
import matteroverdrive.registry.TileRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class TileMicrowave extends GenericMachineTile {

	public static final int SLOT_COUNT = 7;

	public static final int OPERATING_TIME = 50;
	public static final int USAGE_PER_TICK = 30;
	public static final int ENERGY_STORAGE = 512000;
	public static final int DEFAULT_SPEED = 1;

	private SmokingRecipe cachedRecipe;

	public final Property<CompoundTag> capInventoryProp;
	public final Property<CompoundTag> capEnergyStorageProp;

	public TileMicrowave(BlockPos pos, BlockState state) {
		super(TileRegistry.TILE_MICROWAVE.get(), pos, state);

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

		addInventoryCap(new CapabilityInventory(SLOT_COUNT, true, true).setInputs(1).setOutputs(1).setEnergyInputSlots(1)
				.setUpgrades(4).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.UP, Direction.NORTH },
						new Direction[] { Direction.DOWN })
				.setValidator(machineValidator()).setValidUpgrades(InventoryMicrowave.UPGRADES)
				.setPropertyManager(capInventoryProp));
		addEnergyStorageCap(new CapabilityEnergyStorage(ENERGY_STORAGE, true, false).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.WEST, Direction.EAST }, null)
				.setPropertyManager(capEnergyStorageProp));
		setMenuProvider(new SimpleMenuProvider(
				(id, inv, play) -> new InventoryMicrowave(id, play.getInventory(), getInventoryCap(), getCoordsData()),
				getContainerName(TypeMachine.MICROWAVE.id())));
		setTickable();
	}

	@Override
	public void tickServer() {
		UtilsTile.drainElectricSlot(this);
		
		handleOnState();

		if (!canRun()) {
			setShouldSaveData(setRunning(false), setProgress(0), updateTickable(false));
			return;
		}
		
		CapabilityInventory inv = getInventoryCap();
		ItemStack input = inv.getInputs().get(0);
		if (input.isEmpty()) {
			setShouldSaveData(setRunning(false), setProgress(0), updateTickable(false));
			return;
		}

		boolean matched = false;
		if (cachedRecipe == null) {
			Level world = getLevel();
			for (SmokingRecipe recipe : world.getRecipeManager().getAllRecipesFor(RecipeType.SMOKING)) {
				if (recipe.getIngredients().get(0).test(input)) {
					cachedRecipe = recipe;
					matched = true;
				}
			}
		} else {
			matched = cachedRecipe.getIngredients().get(0).test(input);
		}
		if (!matched) {
			setShouldSaveData(setRunning(false), setProgress(0), updateTickable(false));
			return;
		}

		CapabilityEnergyStorage energy = getEnergyStorageCap();
		if (energy.getEnergyStored() < getCurrentPowerUsage()) {
			setShouldSaveData(setRunning(false), updateTickable(false));
			return;
		}

		ItemStack output = inv.getOutputs().get(0);
		ItemStack result = cachedRecipe.getResultItem();

		if (!(output.isEmpty() || doesOutputFit(output, result))) {
			setShouldSaveData(setRunning(false), updateTickable(false));
			return;
		}

		setRunning(true);
		incrementProgress(getCurrentSpeed());
		energy.removeEnergy((int) getCurrentPowerUsage());
		if (getProgress() >= OPERATING_TIME) {
			setProgress(0);
			input.shrink(1);
			if (output.isEmpty()) {
				inv.setStackInSlot(1, result.copy());
			} else {
				output.grow(result.getCount());
			}
		}
		setShouldSaveData(true);

	}

	@Override
	public void tickClient() {
		if (shouldPlaySound() && !clientSoundPlaying) {
			clientSoundPlaying = true;
			SoundBarrierMethods.playTileSound(SoundRegistry.SOUND_MACHINE.get(), this, 1.0F, 1.0F, true);
		}
	}

}
