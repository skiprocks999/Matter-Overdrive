package matteroverdrive.common.tile;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.SoundRegister;
import matteroverdrive.common.block.BlockLightableMachine;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryMicrowave;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.sound.TickableSoundTile;
import matteroverdrive.core.tile.types.GenericSoundTile;
import matteroverdrive.core.tile.utils.PacketHandler;
import matteroverdrive.core.tile.utils.Ticker;
import matteroverdrive.core.utils.UtilsItem;
import matteroverdrive.core.utils.UtilsTile;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

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
		super(DeferredRegisters.TILE_MICROWAVE.get(), pos, state);
		addCapability(new CapabilityInventory(SLOT_COUNT, true, true).setInputs(1).setOutputs(1).setEnergySlots(1)
				.setUpgrades(4).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.UP, Direction.SOUTH },
						new Direction[] { Direction.DOWN })
				.setValidator(machineValidator()).setValidUpgrades(InventoryMicrowave.UPGRADES));
		addCapability(new CapabilityEnergyStorage(ENERGY_STORAGE, true, false).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.WEST, Direction.EAST }, null));
		setMenuProvider(new SimpleMenuProvider(
				(id, inv, play) -> new InventoryMicrowave(id, play.getInventory(),
						exposeCapability(CapabilityType.Item), getCoordsData()),
				getContainerName(TypeMachine.MICROWAVE.id())));
		setMenuPacketHandler(
				new PacketHandler(this, true).packetReader(this::clientMenuLoad).packetWriter(this::clientMenuSave));
		setRenderPacketHandler(
				new PacketHandler(this, false).packetReader(this::clientTileLoad).packetWriter(this::clientTileSave));
		setTicker(new Ticker(this).tickServer(this::tickServer).tickClient(this::tickClient));
	}

	private void tickServer(Ticker ticker) {
		if (canRun()) {
			UtilsTile.drainElectricSlot(this);
			CapabilityInventory inv = exposeCapability(CapabilityType.Item);
			ItemStack input = inv.getInputs().get(0);
			if (!input.isEmpty()) {
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
				if (matched) {
					CapabilityEnergyStorage energy = exposeCapability(CapabilityType.Energy);
					ItemStack output = inv.getOutputs().get(0);
					ItemStack result = cachedRecipe.getResultItem();
					if (energy.getEnergyStored() >= usage
							&& (output.isEmpty() || (UtilsItem.compareItems(output.getItem(), result.getItem())
									&& (output.getCount() + result.getCount() <= result.getMaxStackSize())))) {
						running = true;
						currProgress += currSpeed;
						energy.removeEnergy(usage);
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
					} else {
						running = false;
					}
				} else {
					running = false;
					currProgress = 0;
				}
			} else {
				running = false;
				currProgress = 0;
			}
		} else {
			running = false;
			currProgress = 0;
		}
		boolean currState = getLevel().getBlockState(getBlockPos()).getValue(BlockLightableMachine.LIT);
		if (currState && !running) {
			UtilsTile.updateLit(this, Boolean.FALSE);
		} else if (!currState && running) {
			UtilsTile.updateLit(this, Boolean.TRUE);
		}
	}

	private void tickClient(Ticker ticker) {
		if (shouldPlaySound() && !clientSoundPlaying) {
			clientSoundPlaying = true;
			Minecraft.getInstance().getSoundManager()
					.play(new TickableSoundTile(SoundRegister.SOUND_MACHINE.get(), this, 1.0F, 1.0F));
		}
	}

	private void clientMenuSave(CompoundTag tag) {
		CapabilityInventory inv = exposeCapability(CapabilityType.Item);
		tag.put(inv.getSaveKey(), inv.serializeNBT());
		CapabilityEnergyStorage energy = exposeCapability(CapabilityType.Energy);
		tag.put(energy.getSaveKey(), energy.serializeNBT());

		tag.putInt("redstone", currRedstoneMode);
		tag.putInt("usage", usage);
		tag.putDouble("progress", currProgress);
		tag.putDouble("speed", currSpeed);
	}

	private void clientMenuLoad(CompoundTag tag) {
		clientInventory = new CapabilityInventory();
		clientInventory.deserializeNBT(tag.getCompound(clientInventory.getSaveKey()));
		clientEnergy = new CapabilityEnergyStorage(0, false, false);
		clientEnergy.deserializeNBT(tag.getCompound(clientEnergy.getSaveKey()));

		clientRedstoneMode = tag.getInt("redstone");
		clientEnergyUsage = tag.getInt("usage");
		clientProgress = tag.getDouble("progress");
		clientSpeed = tag.getDouble("speed");
	}

	private void clientTileSave(CompoundTag tag) {
		tag.putBoolean("running", running);
		tag.putBoolean("muffled", isMuffled);
	}

	private void clientTileLoad(CompoundTag tag) {
		clientRunning = tag.getBoolean("running");
		clientMuffled = tag.getBoolean("muffled");
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
	public int getMaxMode() {
		return 2;
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
		return clientSide ? clientSpeed : currSpeed;
	}

	@Override
	public double getCurrentPowerStorage(boolean clientSide) {
		return clientSide ? clientEnergy.getMaxEnergyStored()
				: this.<CapabilityEnergyStorage>exposeCapability(CapabilityType.Energy).getMaxEnergyStored();
	}

	@Override
	public double getCurrentPowerUsage(boolean clientSide) {
		return clientSide ? clientEnergyUsage : usage;
	}

	@Override
	public void setSpeed(double speed) {
		currSpeed = speed;
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
	public int getProcessingTime() {
		return OPERATING_TIME;
	}

}
