package matteroverdrive.core.tile.types;

import matteroverdrive.core.property.Property;
import matteroverdrive.core.property.PropertyTypes;
import matteroverdrive.core.tile.utils.IRedstoneModeTile;
import matteroverdrive.core.utils.UtilsTile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class GenericRedstoneTile extends GenericTickingTile implements IRedstoneModeTile {

	private int currRedstoneMode = 0;

	public final Property<Integer> currRedstoneModeProp;

	protected GenericRedstoneTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.currRedstoneModeProp = this.getPropertyManager()
				.addTrackedProperty(PropertyTypes.INTEGER.create(() -> currRedstoneMode, mode -> {
					if (mode > getMaxMode()) {
						currRedstoneMode = 0;
					} else {
						currRedstoneMode = mode;
					}
				}));
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
		currRedstoneModeProp.set(mode);
		onRedstoneUpdate();
	}

	@Override
	public int getCurrMode() {
		return currRedstoneModeProp.get();
	}

	@Override
	public boolean canRun() {
		boolean hasSignal = UtilsTile.adjacentRedstoneSignal(this);
		return currRedstoneMode == 0 && !hasSignal || currRedstoneMode == 1 && hasSignal || currRedstoneMode == 2;
	}

	@Override
	public int getMaxMode() {
		return 2;
	}
	
	@Override
	public void onRedstoneUpdate() {
		setShouldSaveData(updateTickable(true));
	}

}
