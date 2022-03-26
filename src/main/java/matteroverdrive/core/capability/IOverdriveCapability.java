package matteroverdrive.core.capability;

import matteroverdrive.core.capability.types.CapabilityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public interface IOverdriveCapability extends ICapabilitySerializable<CompoundTag> {
	
	public <T> LazyOptional<T> castHolder();
	
	public void onLoad(BlockEntity tile);
	
	public CapabilityType getCapabilityType();
	
	default boolean isType(CapabilityType type) {
		return getCapabilityType() == type;
	}
	
}
