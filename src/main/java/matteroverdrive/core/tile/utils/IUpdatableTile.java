package matteroverdrive.core.tile.utils;

import net.minecraft.nbt.CompoundTag;

public interface IUpdatableTile {
	
	default void getRenderData(CompoundTag tag) {
		
	}
	
	default void readRenderData(CompoundTag tag) {
		
	}
	
	default void getMenuData(CompoundTag tag) {
		
	}
	
	default void readMenuData(CompoundTag tag) {
		
	}

}
