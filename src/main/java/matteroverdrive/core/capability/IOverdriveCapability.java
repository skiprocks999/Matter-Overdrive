package matteroverdrive.core.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public interface IOverdriveCapability extends ICapabilitySerializable<CompoundTag> {

	public void onLoad(BlockEntity tile);

	public <T> boolean matchesCapability(Capability<T> cap);

	void invalidateCapability();

	void refreshCapability();

	String getSaveKey();

}
