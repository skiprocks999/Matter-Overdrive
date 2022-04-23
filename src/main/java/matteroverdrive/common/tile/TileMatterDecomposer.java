package matteroverdrive.common.tile;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.MatterOverdrive;
import matteroverdrive.SoundRegister;
import matteroverdrive.common.block.BlockLightableMachine;
import matteroverdrive.common.inventory.InventoryMatterDecomposer;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.capability.types.matter.CapabilityMatterStorage;
import matteroverdrive.core.matter.MatterRegister;
import matteroverdrive.core.matter.MatterUtils;
import matteroverdrive.core.tile.GenericTile;
import matteroverdrive.core.tile.IRedstoneMode;
import matteroverdrive.core.tile.utils.PacketHandler;
import matteroverdrive.core.tile.utils.Ticker;
import matteroverdrive.core.utils.UtilsNbt;
import matteroverdrive.core.utils.UtilsTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class TileMatterDecomposer extends GenericTile implements IRedstoneMode {

	public static final int SLOT_COUNT = 8;

	private int currRedstoneMode;
	private boolean running = false;
	private int usage = 0;
	private double currRecipeValue = 0;
	
	public static final int OPERATING_TIME = 2000;
	private static final int USAGE_PER_TICK = 10;
	private int currProgress = 0;
	private static final float FAILURE_CHANCE = 0.005F;
	
	public int clientRedstoneMode;
	public boolean clientRunning;
	public int clientEnergyUsage;
	public double clientRecipeValue;
	public int clientProgress;

	public CapabilityInventory clientInventory;
	public CapabilityEnergyStorage clientEnergy;
	public CapabilityMatterStorage clientMatter;

	public TileMatterDecomposer(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_MATTER_DECOMPOSER.get(), pos, state);

		addCapability(new CapabilityInventory(SLOT_COUNT, true, true).setInputs(1).setOutputs(1).setEnergySlots(1)
				.setMatterSlots(1).setUpgrades(4).setOwner(this)
				.setDefaultDirections(state, new Direction[] { Direction.UP }, new Direction[] { Direction.DOWN }));
		addCapability(new CapabilityEnergyStorage(512000, true, false).setOwner(this).setDefaultDirections(state,
				new Direction[] { Direction.WEST, Direction.EAST }, null));
		addCapability(new CapabilityMatterStorage(1024, false, true).setOwner(this).setDefaultDirections(state, null,
				new Direction[] { Direction.SOUTH }));
		setMenuProvider(new SimpleMenuProvider(
				(id, inv, play) -> new InventoryMatterDecomposer(id, play.getInventory(),
						exposeCapability(CapabilityType.Item), getCoordsData()),
				getContainerName("matter_decomposer")));
		setMenuPacketHandler(
				new PacketHandler(this, true).packetReader(this::clientLoad).packetWriter(this::clientSave));
		setTicker(new Ticker(this).tickServer(this::tickServer).tickClient(this::tickClient));
	}
	
	private void tickServer(Ticker ticker) {
		if(canRun()) {
			CapabilityInventory inv = exposeCapability(CapabilityType.Item);
			ItemStack input = inv.getInputs().get(0);
			if(!input.isEmpty()) {
				Double matterVal = currRecipeValue > 0 ? currRecipeValue : MatterRegister.INSTANCE.getServerMatterValue(input);
				if(matterVal != null || (MatterUtils.isRefinedDust(input) && UtilsNbt.readMatterVal(input) > 0)) {
					CapabilityEnergyStorage energy = exposeCapability(CapabilityType.Energy);
					if(energy.getEnergyStored() >= USAGE_PER_TICK) {
						currRecipeValue = matterVal;
						CapabilityMatterStorage storage = exposeCapability(CapabilityType.Matter);
						boolean room = (storage.getMaxMatterStored() - storage.getMatterStored()) >= currRecipeValue;
						ItemStack output = inv.getOutputs().get(0);
						boolean outputRoom = output.isEmpty() || (UtilsNbt.readMatterVal(output) == currRecipeValue && (output.getCount() + 1 <= output.getMaxStackSize()));
						if(room && outputRoom) {
							running = true;
							currProgress++;
							usage = USAGE_PER_TICK;
							energy.removeEnergy(USAGE_PER_TICK);
							if(currProgress >= OPERATING_TIME) {
								if(roll() > FAILURE_CHANCE) {
									storage.giveMatter(currRecipeValue);
									input.shrink(1);
								} else {
									if(output.isEmpty()) {
										ItemStack dust = new ItemStack(DeferredRegisters.ITEM_RAW_MATTER_DUST.get());
										UtilsNbt.writeMatterVal(dust, currRecipeValue);
										inv.setStackInSlot(1, dust.copy());
									} else {
										output.grow(1);
									}
								}
								currProgress = 0;
								currRecipeValue = 0;
								running = false;
								updateState(Boolean.FALSE);
							}
							setChanged();
						} else {
							usage = 0;
							running = false;
						}
					} else {
						running = false;
						usage = 0;
					}
				} else {
					MatterOverdrive.LOGGER.info("called");
					running = false;
					currRecipeValue = 0;
					usage = 0;
				}
			} else {
				running = false;
				currRecipeValue = 0;
				usage = 0;
			}
		} else {
			currRecipeValue = 0;
			running = false;
			usage = 0;
		}
		boolean currState = getLevel().getBlockState(getBlockPos()).getValue(BlockLightableMachine.LIT);
		if(currState && !running) {
			updateState(Boolean.FALSE);
		} else if (!currState && running) {
			updateState(Boolean.TRUE);
		}
		
		if(running && ticker.getTicks() % 42 == 0) {
			getLevel().playSound(null, getBlockPos(), SoundRegister.SOUND_DECOMPOSER.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
		}
	}
	
	private float roll() {
		return MatterOverdrive.RANDOM.nextFloat();
	}
	
	private void updateState(Boolean value) {
		Level world = getLevel();
		BlockPos pos = getBlockPos();
		world.setBlockAndUpdate(pos, world.getBlockState(pos).setValue(BlockLightableMachine.LIT, value));
	}

	private void tickClient(Ticker ticker) {
		
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
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		saveMode(tag);
		tag.putInt("progress", currProgress);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		loadMode(tag);
		currProgress = tag.getInt("progress");
	}

	private void clientSave(CompoundTag tag) {
		tag.putInt("redstone", currRedstoneMode);
		tag.putBoolean("running", running);
		tag.putInt("usage", usage);
		tag.putDouble("recipe", currRecipeValue);
		CapabilityInventory inv = exposeCapability(CapabilityType.Item);
		tag.put(inv.getSaveKey(), inv.serializeNBT());
		CapabilityEnergyStorage energy = exposeCapability(CapabilityType.Energy);
		tag.put(energy.getSaveKey(), energy.serializeNBT());
		CapabilityMatterStorage matter = exposeCapability(CapabilityType.Matter);
		tag.put(matter.getSaveKey(), matter.serializeNBT());
		tag.putInt("progress", currProgress);
	}

	private void clientLoad(CompoundTag tag) {
		clientRedstoneMode = tag.getInt("redstone");
		clientRunning = tag.getBoolean("running");
		clientEnergyUsage = tag.getInt("usage");
		clientRecipeValue = tag.getDouble("recipe");
		clientInventory = new CapabilityInventory();
		clientInventory.deserializeNBT(tag.getCompound(clientInventory.getSaveKey()));
		clientEnergy = new CapabilityEnergyStorage(0, false, false);
		clientEnergy.deserializeNBT(tag.getCompound(clientEnergy.getSaveKey()));
		clientMatter = new CapabilityMatterStorage(0, false, false);
		clientMatter.deserializeNBT(tag.getCompound(clientMatter.getSaveKey()));
		clientProgress = tag.getInt("progress");
	}

	@Override
	public boolean canRun() {
		boolean hasSignal = UtilsTile.adjacentRedstoneSignal(this);
		return currRedstoneMode == 0 && !hasSignal || currRedstoneMode == 1 && hasSignal || currRedstoneMode == 2;
	}

}
