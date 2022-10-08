package matteroverdrive.core.tile.types;

import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.capability.types.matter.CapabilityMatterStorage;
import matteroverdrive.core.property.Property;
import matteroverdrive.core.property.PropertyTypes;
import matteroverdrive.core.tile.GenericTile;
import matteroverdrive.core.tile.utils.ITickableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class GenericTickingTile extends GenericTile implements ITickableTile {

	private boolean isTickable = false;
	protected long ticks = 0;
	
	private boolean shouldSaveData = false;
	
	private final Property<Boolean> tickingProperty;
	
	protected GenericTickingTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		tickingProperty = getPropertyManager().addTrackedProperty(PropertyTypes.BOOLEAN.create(() -> isTickable, tick -> isTickable = tick));
	}
	
	public boolean shouldSaveData() {
		return shouldSaveData;
	}

	/**
	 * This is a less efficient solution than just using the bitwise boolean
	 * operator |
	 * 
	 * However, being forced to use a specific boolean operator will ultimately lead
	 * to someone forgetting and causing a bug that takes forever to track down.
	 * 
	 * Maybe we can revisit the concept when we are looking at making performance
	 * improvements
	 * 
	 * @param bool
	 */
	public void setShouldSaveData(boolean... changes) {
		for (boolean bool : changes) {
			if (bool) {
				shouldSaveData = true;
				break;
			}
		}
	}

	protected void resetShouldSaveData() {
		shouldSaveData = false;
	}
	
	@Override
	public void tick(Level world) {
		tickCommon();
		incrementTicks();
		if (world.isClientSide) {
			tickClient();
		} else {
			tickServer();
			if(shouldSaveData()) {
				setChanged();
				resetShouldSaveData();
			}
		}
	}
	
	@Override
	public long getTicks() {
		return ticks;
	}

	@Override
	public void incrementTicks() {
		ticks++;
	}

	@Override
	public boolean canTick() {
		return tickingProperty.get();
	}
	
	public void setTickable() {
		updateTickable(true);
	}
	
	@Override
	public boolean updateTickable(boolean canTick) {
		tickingProperty.set(canTick);
		return tickingProperty.isDirtyNoUpdate();
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putBoolean("tickable", tickingProperty.get());
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		tickingProperty.set(tag.getBoolean("tickable"));
	}
	
	@Override
	public void onInventoryChange(int slot, CapabilityInventory inv) {
		setShouldSaveData(updateTickable(true));
	}
	
	@Override
	public void onMatterStorageChange(CapabilityMatterStorage matter) {
		setShouldSaveData(updateTickable(true));
	}
	
	@Override
	public void onEnergyStorageChange(CapabilityEnergyStorage energy) {
		setShouldSaveData(updateTickable(true));
	}
	
	@Override
	public void onNeighborChange(BlockState state, BlockPos neighbor) {
		if(!level.isClientSide) {
			setShouldSaveData(updateTickable(true));
		}
	}
	
	@Override
	public void onBlockStateChange(BlockState oldState, BlockState newState, boolean moving) {
		super.onBlockStateChange(oldState, newState, moving);
		if(!level.isClientSide && !newState.isAir()) {
			setShouldSaveData(updateTickable(true));
		}
	}

}
