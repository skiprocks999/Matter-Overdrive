package matteroverdrive.common.tile;

import java.util.List;

import matteroverdrive.MatterOverdrive;
import matteroverdrive.SoundRegister;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryInscriber;
import matteroverdrive.common.recipe.RecipeInit;
import matteroverdrive.common.recipe.item2item.specific_machines.InscriberRecipe;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.recipe.CountableIngredient;
import matteroverdrive.core.sound.SoundBarrierMethods;
import matteroverdrive.core.tile.types.GenericSoundTile;
import matteroverdrive.core.utils.UtilsItem;
import matteroverdrive.core.utils.UtilsTile;
import matteroverdrive.registry.TileRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class TileInscriber extends GenericSoundTile {

	public static final int SLOT_COUNT = 8;

	public static final int OPERATING_TIME = 256;
	private static final int USAGE_PER_TICK = 250;
	private static final int ENERGY_STORAGE = 512000;
	private static final int DEFAULT_SPEED = 1;

	private double currProgress = 0;

	public double clientProgress;

	private InscriberRecipe cachedRecipe;

	public TileInscriber(BlockPos pos, BlockState state) {
		super(TileRegistry.TILE_INSCRIBER.get(), pos, state);
		currentSpeed = DEFAULT_SPEED;
		currentPowerUsage = USAGE_PER_TICK;
		addInventoryCap(new CapabilityInventory(SLOT_COUNT, true, true).setInputs(2).setOutputs(1).setEnergySlots(1)
				.setUpgrades(4).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.UP, Direction.NORTH },
						new Direction[] { Direction.DOWN })
				.setValidator(machineValidator()).setValidUpgrades(InventoryInscriber.UPGRADES));
		addEnergyStorageCap(new CapabilityEnergyStorage(ENERGY_STORAGE, true, false).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.WEST, Direction.EAST }, null));
		setMenuProvider(new SimpleMenuProvider(
				(id, inv, play) -> new InventoryInscriber(id, play.getInventory(), getInventoryCap(), getCoordsData()),
				getContainerName(TypeMachine.INSCRIBER.id())));
		setHasMenuData();
		setTickable();
	}

	@Override
	public void tickServer() {
		MatterOverdrive.LOGGER.info("Server Muffled: " + isMuffled);
		if (!canRun()) {
			isRunning = false;
			currProgress = 0;
			return;
		}
		UtilsTile.drainElectricSlot(this);
		CapabilityInventory inv = getInventoryCap();
		List<ItemStack> inputs = inv.getInputs();
		ItemStack input1 = inputs.get(0);
		ItemStack input2 = inputs.get(1);
		if (input1.isEmpty() || input2.isEmpty()) {
			isRunning = false;
			currProgress = 0;
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
			isRunning = false;
			currProgress = 0;
			return;
		}
		CapabilityEnergyStorage energy = getEnergyStorageCap();
		if (energy.getEnergyStored() < getCurrentPowerUsage()) {
			isRunning = false;
			return;
		}
		ItemStack output = inv.getOutputs().get(0);
		ItemStack result = cachedRecipe.getResultItem();
		if ((output.isEmpty() || (UtilsItem.compareItems(output.getItem(), result.getItem())
				&& (output.getCount() + result.getCount() <= result.getMaxStackSize())))) {
			isRunning = true;
			currProgress += getCurrentSpeed();
			energy.removeEnergy((int) getCurrentPowerUsage());
			if (currProgress >= OPERATING_TIME) {
				currProgress = 0;
				if (output.isEmpty()) {
					inv.setStackInSlot(2, result.copy());
				} else {
					output.grow(result.getCount());
				}
				List<CountableIngredient> ings = cachedRecipe.getCountedIngredients();
				List<Integer> slotOrientation = cachedRecipe.getItemArrangment(0);
				for (int i = 0; i < inputs.size(); i++) {
					inputs.get(slotOrientation.get(i)).shrink(ings.get(i).getStackSize());
				}

			}
			setChanged();
		} else {
			isRunning = false;
		}
	}

	@Override
	public void tickClient() {
		MatterOverdrive.LOGGER.info("Client Muffled: " + isMuffled);
		if (shouldPlaySound() && !clientSoundPlaying) {
			clientSoundPlaying = true;
			SoundBarrierMethods.playTileSound(SoundRegister.SOUND_MACHINE.get(), this, 1.0F, 1.0F, true);
		}
	}

	@Override
	public void getMenuData(CompoundTag tag) {
		tag.putDouble("progress", currProgress);
	}

	@Override
	public void readMenuData(CompoundTag tag) {
		clientProgress = tag.getDouble("progress");
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);

		CompoundTag additional = new CompoundTag();
		additional.putDouble("progress", currProgress);
		additional.putDouble("speed", currentSpeed);
		additional.putDouble("usage", currentPowerUsage);
		tag.put("additional", additional);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);

		CompoundTag additional = tag.getCompound("additional");
		currProgress = additional.getDouble("progress");
		currentSpeed = additional.getDouble("speed");
		currentPowerUsage = additional.getDouble("usage");
	}

	@Override
	public void setNotPlaying() {
		clientSoundPlaying = false;
	}

	@Override
	public double getDefaultSpeed() {
		return DEFAULT_SPEED;
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
	public double getCurrentPowerStorage() {
		return getEnergyStorageCap().getMaxEnergyStored();
	}

	@Override
	public void setPowerStorage(double storage) {
		getEnergyStorageCap().updateMaxEnergyStorage((int) storage);
	}

	@Override
	public double getProcessingTime() {
		return OPERATING_TIME;
	}

	private List<InscriberRecipe> getRecipes() {
		return getLevel().getRecipeManager().getAllRecipesFor(RecipeInit.INSCRIBER_TYPE.get());
	}

}
