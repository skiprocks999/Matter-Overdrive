package matteroverdrive.common.tile;

import matteroverdrive.SoundRegister;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryMicrowave;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
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
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class TileMicrowave extends GenericSoundTile {

	public static final int SLOT_COUNT = 7;

	public static final int OPERATING_TIME = 50;
	private static final int USAGE_PER_TICK = 30;
	private static final int ENERGY_STORAGE = 512000;
	private static final int DEFAULT_SPEED = 1;

	private boolean running = false;
	private double currProgress = 0;
	private double currSpeed = DEFAULT_SPEED;
	private int usage = USAGE_PER_TICK;
	private boolean isMuffled = false;

	public int clientEnergyUsage;
	public double clientProgress;
	public double clientSpeed;
	private boolean clientMuffled;
	public boolean clientRunning;
	private boolean clientSoundPlaying = false;

	public CapabilityInventory clientInventory;
	public CapabilityEnergyStorage clientEnergy;

	private SmokingRecipe cachedRecipe;

	public TileMicrowave(BlockPos pos, BlockState state) {
		super(TileRegistry.TILE_MICROWAVE.get(), pos, state);
		addCapability(new CapabilityInventory(SLOT_COUNT, true, true).setInputs(1).setOutputs(1).setEnergySlots(1)
				.setUpgrades(4).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.UP, Direction.NORTH },
						new Direction[] { Direction.DOWN })
				.setValidator(machineValidator()).setValidUpgrades(InventoryMicrowave.UPGRADES));
		addCapability(new CapabilityEnergyStorage(ENERGY_STORAGE, true, false).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.WEST, Direction.EAST }, null));
		setMenuProvider(new SimpleMenuProvider(
				(id, inv, play) -> new InventoryMicrowave(id, play.getInventory(),
						exposeCapability(CapabilityType.ITEM), getCoordsData()),
				getContainerName(TypeMachine.MICROWAVE.id())));
		setHasMenuData();
		setHasRenderData();
		setTickable();
	}

	@Override
	public void tickServer() {
		boolean currState = getLevel().getBlockState(getBlockPos()).getValue(BlockStateProperties.LIT);
		if (currState && !running) {
			UtilsTile.updateLit(this, Boolean.FALSE);
		} else if (!currState && running) {
			UtilsTile.updateLit(this, Boolean.TRUE);
		}
		if (!canRun()) {
			running = false;
			currProgress = 0;
			return;
		} 
		UtilsTile.drainElectricSlot(this);
		CapabilityInventory inv = exposeCapability(CapabilityType.ITEM);
		ItemStack input = inv.getInputs().get(0);
		if (input.isEmpty()) {
			running = false;
			currProgress = 0;
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
			running = false;
			currProgress = 0;
			return;
		} 
		
		CapabilityEnergyStorage energy = exposeCapability(CapabilityType.ENERGY);
		if(energy.getEnergyStored() < getCurrentPowerUsage(false)) {
			running = false;
			return;
		}
		
		ItemStack output = inv.getOutputs().get(0);
		ItemStack result = cachedRecipe.getResultItem();
		
		if(!(output.isEmpty() || (UtilsItem.compareItems(output.getItem(), result.getItem())
				&& (output.getCount() + result.getCount() <= result.getMaxStackSize())))) {
			running = false;
			return;
		}
		
		if (energy.getEnergyStored() >= getCurrentPowerUsage(false)
				&& (output.isEmpty() || (UtilsItem.compareItems(output.getItem(), result.getItem())
						&& (output.getCount() + result.getCount() <= result.getMaxStackSize())))) {
			
		}
		
		running = true;
		currProgress += getCurrentSpeed(false);
		energy.removeEnergy((int) getCurrentPowerUsage(false));
		if (currProgress >= OPERATING_TIME) {
			currProgress = 0;
			if (output.isEmpty()) {
				inv.setStackInSlot(1, result.copy());
			} else {
				output.grow(result.getCount());
			}
			input.shrink(1);
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

	public void getMenuData(CompoundTag tag) {
		CapabilityInventory inv = exposeCapability(CapabilityType.ITEM);
		tag.put(inv.getSaveKey(), inv.serializeNBT());
		CapabilityEnergyStorage energy = exposeCapability(CapabilityType.ENERGY);
		tag.put(energy.getSaveKey(), energy.serializeNBT());

		tag.putInt("redstone", currRedstoneMode);
		tag.putInt("usage", usage);
		tag.putDouble("progress", currProgress);
		tag.putDouble("speed", currSpeed);
	}

	@Override
	public void readMenuData(CompoundTag tag) {
		clientInventory = new CapabilityInventory();
		clientInventory.deserializeNBT(tag.getCompound(clientInventory.getSaveKey()));
		clientEnergy = new CapabilityEnergyStorage(0, false, false);
		clientEnergy.deserializeNBT(tag.getCompound(clientEnergy.getSaveKey()));

		clientRedstoneMode = tag.getInt("redstone");
		clientEnergyUsage = tag.getInt("usage");
		clientProgress = tag.getDouble("progress");
		clientSpeed = tag.getDouble("speed");
	}

	@Override
	public void getRenderData(CompoundTag tag) {
		tag.putBoolean("running", running);
		tag.putBoolean("muffled", isMuffled);
		tag.putDouble("sabonus", saMultiplier);
	}

	@Override
	public void readRenderData(CompoundTag tag) {
		clientRunning = tag.getBoolean("running");
		clientMuffled = tag.getBoolean("muffled");
		clientSAMultipler = tag.getDouble("sabonus");
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);

		CompoundTag additional = new CompoundTag();
		additional.putDouble("progress", currProgress);
		additional.putDouble("speed", currSpeed);
		additional.putInt("usage", usage);
		additional.putBoolean("muffled", isMuffled);

		tag.put("additional", additional);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);

		CompoundTag additional = tag.getCompound("additional");
		currProgress = additional.getDouble("progress");
		currSpeed = additional.getDouble("speed");
		usage = additional.getInt("usage");
		isMuffled = additional.getBoolean("muffled");
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
		return clientSide ? clientMuffled : isMuffled;
	}

	@Override
	public double getCurrentSpeed(boolean clientSide) {
		return clientSide ? clientSpeed * clientSAMultipler : currSpeed * saMultiplier;
	}

	@Override
	public double getCurrentPowerStorage(boolean clientSide) {
		return clientSide ? clientEnergy.getMaxEnergyStored()
				: this.<CapabilityEnergyStorage>exposeCapability(CapabilityType.ENERGY).getMaxEnergyStored();
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
		CapabilityEnergyStorage energy = exposeCapability(CapabilityType.ENERGY);
		energy.updateMaxEnergyStorage(storage);
	}

	@Override
	public void setPowerUsage(int usage) {
		this.usage = usage;
	}

	@Override
	public void setMuffled(boolean muffled) {
		isMuffled = muffled;
	}

	@Override
	public double getProcessingTime() {
		return OPERATING_TIME;
	}

}
