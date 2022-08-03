package matteroverdrive.core.capability;

import matteroverdrive.core.capability.types.CapabilityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public interface IOverdriveCapability extends ICapabilitySerializable<CompoundTag> {

	void onLoad(BlockEntity tile);

	<T> boolean matchesCapability(Capability<T> cap);

	CapabilityType getCapabilityType();

	void invalidateCapability();

	void refreshCapability();

	String getSaveKey();

}
