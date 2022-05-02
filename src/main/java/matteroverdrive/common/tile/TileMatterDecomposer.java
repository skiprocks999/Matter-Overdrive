package matteroverdrive.common.tile;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.MatterOverdrive;
import matteroverdrive.SoundRegister;
import matteroverdrive.common.block.BlockLightableMachine;
import matteroverdrive.common.inventory.InventoryMatterDecomposer;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.capability.types.matter.CapabilityMatterStorage;
import matteroverdrive.core.capability.types.matter.ICapabilityMatterStorage;
import matteroverdrive.core.matter.MatterRegister;
import matteroverdrive.core.matter.MatterUtils;
import matteroverdrive.core.sound.TickableSoundTile;
import matteroverdrive.core.tile.GenericSoundTile;
import matteroverdrive.core.tile.IRedstoneModeTile;
import matteroverdrive.core.tile.IUpgradableTile;
import matteroverdrive.core.tile.utils.PacketHandler;
import matteroverdrive.core.tile.utils.Ticker;
import matteroverdrive.core.utils.UtilsNbt;
import matteroverdrive.core.utils.UtilsTile;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class TileMatterDecomposer extends GenericSoundTile implements IRedstoneModeTile, IUpgradableTile {

	public static final int SLOT_COUNT = 8;

	public static final int OPERATING_TIME = 500;
	private static final int USAGE_PER_TICK = 80;
	private static final float FAILURE_CHANCE = 0.005F;
	private static final int MATTER_STORAGE = 1024;
	private static final int ENERGY_STORAGE = 512000;
	private static final int DEFAULT_SPEED = 1;

	private int currRedstoneMode;
	private boolean running = false;
	private double currRecipeValue = 0;
	private double currProgress = 0;
	private double currSpeed = DEFAULT_SPEED;
	private float currFailureChance = FAILURE_CHANCE;
	private int usage = USAGE_PER_TICK;
	private boolean isMuffled = false;

	public int clientRedstoneMode;
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
		super(DeferredRegisters.TILE_MATTER_DECOMPOSER.get(), pos, state);

		addCapability(new CapabilityInventory(SLOT_COUNT, true, true).setInputs(1).setOutputs(1).setEnergySlots(1)
				.setMatterSlots(1).setUpgrades(4).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.UP }, new Direction[] { Direction.DOWN })
				.setValidator(machineValidator()).setValidUpgrades(InventoryMatterDecomposer.UPGRADES));
		addCapability(new CapabilityEnergyStorage(ENERGY_STORAGE, true, false).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.WEST, Direction.EAST }, null));
		addCapability(new CapabilityMatterStorage(MATTER_STORAGE, false, true).setOwner(this)
				.setDefaultDirections(state, null, new Direction[] { Direction.SOUTH }));
		setMenuProvider(new SimpleMenuProvider(
				(id, inv, play) -> new InventoryMatterDecomposer(id, play.getInventory(),
						exposeCapability(CapabilityType.Item), getCoordsData()),
				getContainerName("matter_decomposer")));
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
				Double matterVal = currRecipeValue > 0 ? Double.valueOf(currRecipeValue)
						: MatterRegister.INSTANCE.getServerMatterValue(input);
				if (matterVal != null || (MatterUtils.isRefinedDust(input) && UtilsNbt.readMatterVal(input) > 0)) {
					CapabilityEnergyStorage energy = exposeCapability(CapabilityType.Energy);
					if (energy.getEnergyStored() >= USAGE_PER_TICK) {
						currRecipeValue = matterVal.doubleValue();
						currRecipeValue += input.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE)
								.map(ICapabilityMatterStorage::getMatterStored).orElse(0.0);
						CapabilityMatterStorage storage = exposeCapability(CapabilityType.Matter);
						boolean room = (storage.getMaxMatterStored() - storage.getMatterStored()) >= currRecipeValue;
						ItemStack output = inv.getOutputs().get(0);
						boolean outputRoom = output.isEmpty() || (UtilsNbt.readMatterVal(output) == currRecipeValue
								&& (output.getCount() + 1 <= output.getMaxStackSize()));
						if (room && outputRoom) {
							running = true;
							currProgress += currSpeed;
							energy.removeEnergy(usage);
							if (currProgress >= OPERATING_TIME) {
								if (roll() > currFailureChance) {
									storage.giveMatter(currRecipeValue);
									input.shrink(1);
								} else {
									if (output.isEmpty()) {
										ItemStack dust = new ItemStack(DeferredRegisters.ITEM_RAW_MATTER_DUST.get());
										UtilsNbt.writeMatterVal(dust, currRecipeValue);
										inv.setStackInSlot(1, dust.copy());
									} else {
										output.grow(1);
									}
									input.shrink(1);
								}
								currProgress = 0;
							}
							setChanged();
						} else {
							running = false;
						}
					} else {
						running = false;
					}
				} else {
					running = false;
					currRecipeValue = 0;
					currProgress = 0;
				}
			} else {
				running = false;
				currRecipeValue = 0;
				currProgress = 0;
			}
		} else {
			currRecipeValue = 0;
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
					.play(new TickableSoundTile(SoundRegister.SOUND_DECOMPOSER.get(), this));
		}
	}

	private void clientMenuSave(CompoundTag tag) {
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

	private void clientMenuLoad(CompoundTag tag) {
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
		saveMode(additional);
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
		loadMode(additional);
		currProgress = additional.getDouble("progress");
		currSpeed = additional.getDouble("speed");
		currFailureChance = additional.getFloat("failure");
		usage = additional.getInt("usage");
		isMuffled = additional.getBoolean("muffled");
	}

	@Override
	public void setMode(int mode) {
		currRedstoneMode = mode;
	}

	@Override
	public int getCurrMod() {
		return currRedstoneMode;
	}

	@Override
	public int getMaxMode() {
		return 2;
	}

	@Override
	public boolean canRun() {
		boolean hasSignal = UtilsTile.adjacentRedstoneSignal(this);
		return currRedstoneMode == 0 && !hasSignal || currRedstoneMode == 1 && hasSignal || currRedstoneMode == 2;
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
		return clientSide ? clientSpeed : currSpeed;
	}

	@Override
	public float getCurrentFailure(boolean clientSide) {
		return clientSide ? clientFailure : currFailureChance;
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
		return clientSide ? clientEnergyUsage : usage;
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
	public int getProcessingTime() {
		return OPERATING_TIME;
	}

	private float roll() {
		return MatterOverdrive.RANDOM.nextFloat();
	}

}
