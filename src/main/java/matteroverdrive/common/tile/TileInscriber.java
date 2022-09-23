package matteroverdrive.common.tile;

import java.util.List;

import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryInscriber;
import matteroverdrive.common.recipe.RecipeInit;
import matteroverdrive.common.recipe.item2item.specific_machines.InscriberRecipe;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.property.Property;
import matteroverdrive.core.property.PropertyTypes;
import matteroverdrive.core.recipe.CountableIngredient;
import matteroverdrive.core.sound.SoundBarrierMethods;
import matteroverdrive.core.tile.types.GenericMachineTile;
import matteroverdrive.core.utils.UtilsItem;
import matteroverdrive.core.utils.UtilsTile;
import matteroverdrive.registry.SoundRegistry;
import matteroverdrive.registry.TileRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class TileInscriber extends GenericMachineTile {

	public static final int SLOT_COUNT = 8;

	public static final int OPERATING_TIME = 256;
	private static final int USAGE_PER_TICK = 250;
	private static final int ENERGY_STORAGE = 512000;
	private static final int DEFAULT_SPEED = 1;

	private InscriberRecipe cachedRecipe;

	public final Property<CompoundTag> capInventoryProp;
	public final Property<CompoundTag> capEnergyStorageProp;

	public TileInscriber(BlockPos pos, BlockState state) {
		super(TileRegistry.TILE_INSCRIBER.get(), pos, state);

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

		addInventoryCap(new CapabilityInventory(SLOT_COUNT, true, true).setInputs(2).setOutputs(1).setEnergySlots(1)
				.setUpgrades(4).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.UP, Direction.NORTH },
						new Direction[] { Direction.DOWN })
				.setValidator(machineValidator()).setValidUpgrades(InventoryInscriber.UPGRADES)
				.setPropertyManager(capInventoryProp));
		addEnergyStorageCap(new CapabilityEnergyStorage(ENERGY_STORAGE, true, false).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.WEST, Direction.EAST }, null)
				.setPropertyManager(capEnergyStorageProp));
		setMenuProvider(new SimpleMenuProvider(
				(id, inv, play) -> new InventoryInscriber(id, play.getInventory(), getInventoryCap(), getCoordsData()),
				getContainerName(TypeMachine.INSCRIBER.id())));
		setTickable();
	}

	@Override
	public void tickServer() {
		boolean flag = false;
		if (!canRun()) {
			flag = setRunning(false);
			flag |= setProgress(0);
			flag |= setProcessingTime(0);
			flag |= setPowerUsage(0);
			if (flag) {
				setChanged();
			}
			return;
		}
		UtilsTile.drainElectricSlot(this);
		CapabilityInventory inv = getInventoryCap();
		List<ItemStack> inputs = inv.getInputs();
		ItemStack input1 = inputs.get(0);
		ItemStack input2 = inputs.get(1);
		if (input1.isEmpty() || input2.isEmpty()) {
			flag = setRunning(false);
			flag |= setProgress(0);
			flag |= setProcessingTime(0);
			flag |= setPowerUsage(0);
			if (flag) {
				setChanged();
			}
			return;
		}
		boolean matched = false;
		if (cachedRecipe == null) {
			for (InscriberRecipe recipe : getRecipes()) {
				if (recipe.matchesRecipe(inv, 0)) {
					cachedRecipe = recipe;
					matched = true;
				}
			}
		} else {
			matched = cachedRecipe.matchesRecipe(inv, 0);
		}
		if (!matched) {
			flag = setRunning(false);
			flag |= setProgress(0);
			flag |= setProcessingTime(0);
			flag |= setPowerUsage(0);
			if (flag) {
				setChanged();
			}
			return;
		}
		
		flag = setProcessingTime(cachedRecipe.getProcessTime());
		flag |= updatePowerUsageFromRecipe(cachedRecipe.getUsagePerTick());
		if(flag) {
			setChanged();
		}
		CapabilityEnergyStorage energy = getEnergyStorageCap();
		if (energy.getEnergyStored() < getCurrentPowerUsage()) {
			if (setRunning(false)) {
				setChanged();
			}
			return;
		}
		ItemStack output = inv.getOutputs().get(0);
		ItemStack result = cachedRecipe.getResultItem();
		if ((output.isEmpty() || (UtilsItem.compareItems(output.getItem(), result.getItem())
				&& (output.getCount() + result.getCount() <= result.getMaxStackSize())))) {
			setRunning(true);
			incrementProgress(getCurrentSpeed());
			energy.removeEnergy((int) getCurrentPowerUsage());
			if (getProgress() >= getProcessingTime()) {
				setProgress(0);
				List<CountableIngredient> ings = cachedRecipe.getCountedIngredients();
				List<Integer> slotOrientation = cachedRecipe.getItemArrangment(0);
				for (int i = 0; i < inputs.size(); i++) {
					inputs.get(slotOrientation.get(i)).shrink(ings.get(i).getStackSize());
				}
				if (output.isEmpty()) {
					inv.setStackInSlot(2, result.copy());
				} else {
					output.grow(result.getCount());
				}
			}
			setChanged();
		} else {
			if (setRunning(false)) {
				setChanged();
			}
		}
	}

	@Override
	public void tickClient() {
		if (shouldPlaySound() && !clientSoundPlaying) {
			clientSoundPlaying = true;
			SoundBarrierMethods.playTileSound(SoundRegistry.SOUND_MACHINE.get(), this, 1.0F, 1.0F, true);
		}
	}

	private List<InscriberRecipe> getRecipes() {
		return getLevel().getRecipeManager().getAllRecipesFor(RecipeInit.INSCRIBER_TYPE.get());
	}

}
