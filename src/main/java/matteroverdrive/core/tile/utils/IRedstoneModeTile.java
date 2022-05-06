package matteroverdrive.core.tile.utils;

import net.minecraft.nbt.CompoundTag;

/**
 * Indexing starts at 0!
 */

public interface IRedstoneModeTile {

	void setMode(int mode);

	int getCurrMod();

	int getMaxMode();

	boolean canRun();

	default void saveMode(CompoundTag tag) {
		tag.putInt("redmode", getCurrMod());
	}

	default void loadMode(CompoundTag tag) {
		setMode(tag.getInt("redmode"));
	}

}
