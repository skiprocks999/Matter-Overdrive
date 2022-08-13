package matteroverdrive.common.tile;

import matteroverdrive.MatterOverdrive;
import matteroverdrive.SoundRegister;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryMatterDecomposer;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.CapabilityType;
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

	private boolean running = false;
	private double currRecipeValue = 0;
	private double currProgress = 0;
	private double currSpeed = DEFAULT_SPEED;
	private float currFailureChance = FAILURE_CHANCE;
	private int usage = USAGE_PER_TICK;
	private boolean isMuffled = false;

	public int clientEnergyUsage;
	public double clientRecipeValue;
	public double clientProgress;
	public double clientSpeed;
	public float clientFailure;
	private boolean clientMuffled;
	public boolean clientRunning;
	private boolean clientSoundPlaying = false;

	public CapabilityInventory clientInventory;
	public CapabilityEnergyStorage clientEnergy;
	public CapabilityMatterStorage clientMatter;

	public TileMatterDecomposer(BlockPos pos, BlockState state) {
		super(TileRegistry.TILE_MATTER_DECOMPOSER.get(), pos, state);

		addCapability(new CapabilityInventory(SLOT_COUNT, true, true).setInputs(1).setOutputs(1).setEnergySlots(1)
				.setMatterSlots(1).setUpgrades(4).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.UP }, new Direction[] { Direction.DOWN })
				.setValidator(machineValidator()).setValidUpgrades(InventoryMatterDecomposer.UPGRADES));
		addCapability(new CapabilityEnergyStorage(ENERGY_STORAGE, true, false).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.WEST, Direction.EAST }, null));
		addCapability(new CapabilityMatterStorage(MATTER_STORAGE, false, true).setOwner(this).setDefaultDirections(
				state, null, new Direction[] { Direction.NORTH, Direction.EAST, Direction.WEST }));
		setMenuProvider(new SimpleMenuProvider(
				(id, inv, play) -> new InventoryMatterDecomposer(id, play.getInventory(),
						exposeCapability(CapabilityType.Item), getCoordsData()),
				getContainerName(TypeMachine.MATTER_DECOMPOSER.id())));
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
			currRecipeValue = 0;
			running = false;
			currProgress = 0;
			return;
		} 
		UtilsTile.drainElectricSlot(this);
		UtilsTile.fillMatterSlot(this);
		UtilsTile.outputMatter(this);
		CapabilityInventory inv = exposeCapability(CapabilityType.Item);
		ItemStack input = inv.getInputs().get(0);
		if (input.isEmpty()) {
			running = false;
			currRecipeValue = 0;
			currProgress = 0;
			return;
		} 
		
		
		Double matterVal = currRecipeValue > 0 ? Double.valueOf(currRecipeValue)
				: MatterRegister.INSTANCE.getServerMatterValue(input);
		if(matterVal == null) {
			matterVal = 0.0;
			if(UtilsMatter.isRefinedDust(input)) {
				matterVal = UtilsNbt.readMatterVal(input);
			}
			if(matterVal <= 0) {
				running = false;
				currRecipeValue = 0;
				currProgress = 0;
				return;
			}
		}
		CapabilityEnergyStorage energy = exposeCapability(CapabilityType.Energy);
		if(energy.getEnergyStored() < getCurrentPowerUsage(false)) {
			running = false;
			return;
		}
		
		currRecipeValue = matterVal.doubleValue();
		currRecipeValue += input.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE)
				.map(ICapabilityMatterStorage::getMatterStored).orElse(0.0);
		CapabilityMatterStorage storage = exposeCapability(CapabilityType.Matter);
		
		if((storage.getMaxMatterStored() - storage.getMatterStored()) < currRecipeValue) {
			running = false;
			return;
		}

		ItemStack output = inv.getOutputs().get(0);
		
		if(!(output.isEmpty() || (UtilsNbt.readMatterVal(output) == currRecipeValue
				&& (output.getCount() + 1 <= output.getMaxStackSize())))) {
			running = false;
			return;
		}
		running = true;
		currProgress += getCurrentSpeed(false);
		energy.removeEnergy((int) getCurrentPowerUsage(false));
		if (currProgress >= OPERATING_TIME) {
			if (roll() < getCurrentFailure(false)) {
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
		CapabilityInventory inv = exposeCapability(CapabilityType.Item);
		tag.put(inv.getSaveKey(), inv.serializeNBT());
		CapabilityEnergyStorage energy = exposeCapability(CapabilityType.Energy);
		tag.put(energy.getSaveKey(), energy.serializeNBT());
		CapabilityMatterStorage matter = exposeCapability(CapabilityType.Matter);
		tag.put(matter.getSaveKey(), matter.serializeNBT());

		tag.putInt("redstone", currRedstoneMode);
		tag.putInt("usage", usage);
		tag.putDouble("recipe", currRecipeValue);
		tag.putDouble("progress", currProgress);
		tag.putDouble("speed", currSpeed);
		tag.putFloat("failure", currFailureChance);
	}

	@Override
	public void readMenuData(CompoundTag tag) {
		clientInventory = new CapabilityInventory();
		clientInventory.deserializeNBT(tag.getCompound(clientInventory.getSaveKey()));
		clientEnergy = new CapabilityEnergyStorage(0, false, false);
		clientEnergy.deserializeNBT(tag.getCompound(clientEnergy.getSaveKey()));
		clientMatter = new CapabilityMatterStorage(0, false, false);
		clientMatter.deserializeNBT(tag.getCompound(clientMatter.getSaveKey()));

		clientRedstoneMode = tag.getInt("redstone");
		clientEnergyUsage = tag.getInt("usage");
		clientRecipeValue = tag.getDouble("recipe");
		clientProgress = tag.getDouble("progress");
		clientSpeed = tag.getDouble("speed");
		clientFailure = tag.getFloat("failure");
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
		additional.putFloat("failure", currFailureChance);
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
		currFailureChance = additional.getFloat("failure");
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
	public boolean isMuffled(boolean clientSide) {
		return clientSide ? clientMuffled : isMuffled;
	}

	@Override
	public double getCurrentSpeed(boolean clientSide) {
		return clientSide ? clientSpeed * clientSAMultipler : currSpeed * saMultiplier;
	}

	@Override
	public float getCurrentFailure(boolean clientSide) {
		return clientSide ? clientFailure * (float) clientSAMultipler : currFailureChance * (float) saMultiplier;
	}

	@Override
	public double getCurrentMatterStorage(boolean clientSide) {
		return clientSide ? clientMatter.getMaxMatterStored()
				: this.<CapabilityMatterStorage>exposeCapability(CapabilityType.Matter).getMaxMatterStored();
	}

	@Override
	public double getCurrentPowerStorage(boolean clientSide) {
		return clientSide ? clientEnergy.getMaxEnergyStored()
				: this.<CapabilityEnergyStorage>exposeCapability(CapabilityType.Energy).getMaxEnergyStored();
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
	public void setFailure(float failure) {
		currFailureChance = failure;
	}

	@Override
	public void setMatterStorage(double storage) {
		CapabilityMatterStorage matter = exposeCapability(CapabilityType.Matter);
		matter.updateMaxMatterStorage(storage);
	}

	@Override
	public void setPowerStorage(int storage) {
		CapabilityEnergyStorage energy = exposeCapability(CapabilityType.Energy);
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

	private float roll() {
		return MatterOverdrive.RANDOM.nextFloat();
	}

}
