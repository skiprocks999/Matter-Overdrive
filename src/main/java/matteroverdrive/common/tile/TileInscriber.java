package matteroverdrive.common.tile;

import java.util.List;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.SoundRegister;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryInscriber;
import matteroverdrive.common.recipe.RecipeInit;
import matteroverdrive.common.recipe.item2item.specific_machines.InscriberRecipe;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.recipe.CountableIngredient;
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
import net.minecraft.world.level.Level;
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
	private boolean isMuffled = false;

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
		super(DeferredRegisters.TILE_INSCRIBER.get(), pos, state);
		addCapability(new CapabilityInventory(SLOT_COUNT, true, true).setInputs(2).setOutputs(1).setEnergySlots(1)
				.setUpgrades(4).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.UP, Direction.NORTH },
						new Direction[] { Direction.DOWN })
				.setValidator(machineValidator()).setValidUpgrades(InventoryInscriber.UPGRADES));
		addCapability(new CapabilityEnergyStorage(ENERGY_STORAGE, true, false).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.WEST, Direction.EAST }, null));
		setMenuProvider(new SimpleMenuProvider(
				(id, inv, play) -> new InventoryInscriber(id, play.getInventory(),
						exposeCapability(CapabilityType.Item), getCoordsData()),
				getContainerName(TypeMachine.INSCRIBER.id())));
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
			List<ItemStack> inputs = inv.getInputs();
			ItemStack input1 = inputs.get(0);
			ItemStack input2 = inputs.get(1);
			if (!input1.isEmpty() && !input2.isEmpty()) {
				boolean matched = false;
				if (cachedRecipe == null) {
					Level world = getLevel();
					for (InscriberRecipe recipe : world.getRecipeManager()
							.getAllRecipesFor(RecipeInit.INSCRIBER_TYPE.get())) {
						if (recipe.matchesRecipe(inv, 0)) {
							cachedRecipe = recipe;
							matched = true;
						}
					}
				} else {
					matched = cachedRecipe.matchesRecipe(inv, 0);
				}
				if (matched) {
					CapabilityEnergyStorage energy = exposeCapability(CapabilityType.Energy);
					ItemStack output = inv.getOutputs().get(0);
					ItemStack result = cachedRecipe.getResultItem();
					if (energy.getEnergyStored() >= getCurrentPowerUsage(false)
							&& (output.isEmpty() || (UtilsItem.compareItems(output.getItem(), result.getItem())
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
							;
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
	}

	private void tickClient(Ticker ticker) {
		if (shouldPlaySound() && !clientSoundPlaying) {
			clientSoundPlaying = true;
			Minecraft.getInstance().getSoundManager()
					.play(new TickableSoundTile(SoundRegister.SOUND_MACHINE.get(), this, 1.0F, 1.0F));
		}
	}

	private void clientMenuSave(CompoundTag tag) {
		CapabilityEnergyStorage energy = exposeCapability(CapabilityType.Energy);
		tag.put(energy.getSaveKey(), energy.serializeNBT());

		tag.putInt("redstone", currRedstoneMode);
		tag.putInt("usage", usage);
		tag.putDouble("progress", currProgress);
		tag.putDouble("speed", currSpeed);
	}

	private void clientMenuLoad(CompoundTag tag) {
		clientEnergy = new CapabilityEnergyStorage(0, false, false);
		clientEnergy.deserializeNBT(tag.getCompound(clientEnergy.getSaveKey()));

		clientRedstoneMode = tag.getInt("redstone");
		clientEnergyUsage = tag.getInt("usage");
		clientProgress = tag.getDouble("progress");
		clientSpeed = tag.getDouble("speed");
	}

	private void clientTileSave(CompoundTag tag) {
		CapabilityInventory inv = exposeCapability(CapabilityType.Item);
		tag.put(inv.getSaveKey(), inv.serializeNBT());

		tag.putBoolean("running", running);
		tag.putBoolean("muffled", isMuffled);
		tag.putDouble("sabonus", saMultiplier);
	}

	private void clientTileLoad(CompoundTag tag) {
		clientInventory = new CapabilityInventory();
		clientInventory.deserializeNBT(tag.getCompound(clientInventory.getSaveKey()));

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
		return clientSide ? clientSpeed * clientSAMultipler : currSpeed * saMultiplier;
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
