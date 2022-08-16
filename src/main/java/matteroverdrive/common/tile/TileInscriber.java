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
import matteroverdrive.core.property.Property;
import matteroverdrive.core.property.PropertyTypes;
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

	private boolean running = false;
	private double currProgress = 0;
	private double currSpeed = DEFAULT_SPEED;
	private int usage = USAGE_PER_TICK;
	public Property<Boolean> isMuffled;
	private boolean isPureMuffled;

	public int clientEnergyUsage;
	public double clientProgress;
	public double clientSpeed;
	private boolean clientMuffled;
	public boolean clientRunning;
	private boolean clientSoundPlaying = false;

	public CapabilityInventory clientInventory;
	public CapabilityEnergyStorage clientEnergy;

	private InscriberRecipe cachedRecipe;

	public TileInscriber(BlockPos pos, BlockState state) {
		super(TileRegistry.TILE_INSCRIBER.get(), pos, state);
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
		setHasRenderData();
		setTickable();
		this.isMuffled = this.propertyManager.addTrackedProperty(
						PropertyTypes.BOOLEAN.create(
										() -> this.level.isClientSide() ? isMuffled(true) : isMuffled(false),
										this::setMuffled)
		);
	}

	@Override
	public void tickServer() {
		MatterOverdrive.LOGGER.info("Server Muffled: " + isMuffled.get());
		if (!canRun()) {
			running = false;
			currProgress = 0;
			return;
		}
		UtilsTile.drainElectricSlot(this);
		CapabilityInventory inv = getInventoryCap();
		List<ItemStack> inputs = inv.getInputs();
		ItemStack input1 = inputs.get(0);
		ItemStack input2 = inputs.get(1);
		if (input1.isEmpty() || input2.isEmpty()) {
			running = false;
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
			running = false;
			currProgress = 0;
			return;
		}
		CapabilityEnergyStorage energy = getEnergyStorageCap();
		if (energy.getEnergyStored() < getCurrentPowerUsage(false)) {
			running = false;
			return;
		}
		ItemStack output = inv.getOutputs().get(0);
		ItemStack result = cachedRecipe.getResultItem();
		if ((output.isEmpty() || (UtilsItem.compareItems(output.getItem(), result.getItem())
				&& (output.getCount() + result.getCount() <= result.getMaxStackSize())))) {
			running = true;
			currProgress += getCurrentSpeed(false);
			energy.removeEnergy((int) getCurrentPowerUsage(false));
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
			running = false;
		}
	}

	@Override
	public void tickClient() {
		MatterOverdrive.LOGGER.info("Client Muffled: " + isMuffled.get());
		if (shouldPlaySound() && !clientSoundPlaying) {
			clientSoundPlaying = true;
			SoundBarrierMethods.playTileSound(SoundRegister.SOUND_MACHINE.get(), this, 1.0F, 1.0F, true);
		}
	}

	@Override
	public void getMenuData(CompoundTag tag) {
		CapabilityEnergyStorage energy = getEnergyStorageCap();
		tag.put(energy.getSaveKey(), energy.serializeNBT());

		tag.putInt("redstone", currRedstoneMode);
		tag.putInt("usage", usage);
		tag.putDouble("progress", currProgress);
		tag.putDouble("speed", currSpeed);
	}

	@Override
	public void readMenuData(CompoundTag tag) {
		clientEnergy = new CapabilityEnergyStorage(0, false, false);
		clientEnergy.deserializeNBT(tag.getCompound(clientEnergy.getSaveKey()));

		clientRedstoneMode = tag.getInt("redstone");
		clientEnergyUsage = tag.getInt("usage");
		clientProgress = tag.getDouble("progress");
		clientSpeed = tag.getDouble("speed");
	}

	@Override
	public void getRenderData(CompoundTag tag) {
		CapabilityInventory inv = getInventoryCap();
		tag.put(inv.getSaveKey(), inv.serializeNBT());

		tag.putBoolean("running", running);
		tag.putDouble("sabonus", saMultiplier);
	}

	@Override
	public void readRenderData(CompoundTag tag) {
		clientInventory = new CapabilityInventory();
		clientInventory.deserializeNBT(tag.getCompound(clientInventory.getSaveKey()));

		clientRunning = tag.getBoolean("running");
		clientSAMultipler = tag.getDouble("sabonus");
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);

		CompoundTag additional = new CompoundTag();
		additional.putDouble("progress", currProgress);
		additional.putDouble("speed", currSpeed);
		additional.putInt("usage", usage);
		tag.put("additional", additional);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);

		CompoundTag additional = tag.getCompound("additional");
		currProgress = additional.getDouble("progress");
		currSpeed = additional.getDouble("speed");
		usage = additional.getInt("usage");
	}

	@Override
	public boolean shouldPlaySound() {
		return clientRunning && !clientMuffled;
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
	public boolean isMuffled(boolean clientSide) {
		return isPureMuffled;
	}

	@Override
	public double getCurrentSpeed(boolean clientSide) {
		return clientSide ? clientSpeed * clientSAMultipler : currSpeed * saMultiplier;
	}

	@Override
	public double getCurrentPowerStorage(boolean clientSide) {
		return clientSide ? clientEnergy.getMaxEnergyStored() : getEnergyStorageCap().getMaxEnergyStored();
	}

	@Override
	public double getCurrentPowerUsage(boolean clientSide) {
		return clientSide ? clientEnergyUsage * clientSAMultipler : usage * saMultiplier;
	}

	@Override
	public void setSpeed(double speed) {
		currSpeed = speed;
	}

	@Override
	public void setPowerStorage(int storage) {
		getEnergyStorageCap().updateMaxEnergyStorage(storage);
	}

	@Override
	public void setPowerUsage(int usage) {
		this.usage = usage;
	}

	@Override
	public void setMuffled(boolean muffled) {
		this.isPureMuffled = muffled;
	}

	@Override
	public double getProcessingTime() {
		return OPERATING_TIME;
	}

	private List<InscriberRecipe> getRecipes() {
		return getLevel().getRecipeManager().getAllRecipesFor(RecipeInit.INSCRIBER_TYPE.get());
	}

}
