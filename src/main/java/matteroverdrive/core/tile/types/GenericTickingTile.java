package matteroverdrive.core.tile.types;

import matteroverdrive.core.tile.GenericTile;
import matteroverdrive.core.tile.utils.ITickableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class GenericTickingTile extends GenericTile implements ITickableTile {

	private boolean isTickable = false;
	protected long ticks = 0;
	
	protected GenericTickingTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
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
		return isTickable;
	}
	
	public void setTickable() {
		isTickable = true;
	}
	

}
