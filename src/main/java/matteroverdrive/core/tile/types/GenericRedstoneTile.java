package matteroverdrive.core.tile.types;

import matteroverdrive.core.tile.GenericTile;
import matteroverdrive.core.tile.utils.IRedstoneModeTile;
import matteroverdrive.core.utils.UtilsTile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class GenericRedstoneTile extends GenericTile implements IRedstoneModeTile {

	protected int currRedstoneMode;
	
	public int clientRedstoneMode;
	
	protected GenericRedstoneTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		CompoundTag redstone = new CompoundTag();
		saveMode(redstone);
		tag.put("redstone", redstone);
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		loadMode(tag.getCompound("redstone"));
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
	public boolean canRun() {
		boolean hasSignal = UtilsTile.adjacentRedstoneSignal(this);
		return currRedstoneMode == 0 && !hasSignal || currRedstoneMode == 1 && hasSignal || currRedstoneMode == 2;
	}

}
