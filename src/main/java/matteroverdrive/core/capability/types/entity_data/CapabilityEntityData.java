package matteroverdrive.core.capability.types.entity_data;

import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class CapabilityEntityData implements ICapabilityEntityData, ICapabilitySerializable<CompoundTag> {

	private final LazyOptional<ICapabilityEntityData> lazyOptional = LazyOptional.of(() -> this);

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == MatterOverdriveCapabilities.ENTITY_DATA) {
			return lazyOptional.cast();
		}
		return LazyOptional.empty();
	}

	private int transporterTimer;

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		tag.putInt("timer", transporterTimer);
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		transporterTimer = nbt.getInt("timer");
	}

	public void writeToByteBuffer(FriendlyByteBuf buf) {
		buf.writeInt(transporterTimer);
	}

	public void readFromByteBuffer(FriendlyByteBuf buf) {
		transporterTimer = buf.readInt();
	}

	public void copyFromOther(ICapabilityEntityData other) {
		transporterTimer = other.getTransporterTimer();
	}

	@Override
	public int getTransporterTimer() {
		return transporterTimer;
	}

	@Override
	public void setTransporterTimer(int time) {
		transporterTimer = time;
	}

}
