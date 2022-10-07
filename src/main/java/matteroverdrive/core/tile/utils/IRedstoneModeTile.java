package matteroverdrive.core.tile.utils;

import net.minecraft.nbt.CompoundTag;

/**
 * Indexing starts at 0!
 */

public interface IRedstoneModeTile {

	void setMode(int mode);

	int getCurrMode();

	int getMaxMode();

	boolean canRun();
	
	void onRedstoneUpdate();

	default void saveMode(CompoundTag tag) {
		tag.putInt("redmode", getCurrMode());
	}

	default void loadMode(CompoundTag tag) {
		setMode(tag.getInt("redmode"));
	}

}
