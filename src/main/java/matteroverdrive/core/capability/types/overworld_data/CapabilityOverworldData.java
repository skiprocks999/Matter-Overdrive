package matteroverdrive.core.capability.types.overworld_data;

import java.util.ArrayList;
import java.util.List;

import matteroverdrive.common.tile.transporter.utils.ActiveTransportDataWrapper;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class CapabilityOverworldData implements ICapabilityOverworldData, ICapabilitySerializable<CompoundTag> {

	private final LazyOptional<ICapabilityOverworldData> lazyOptional = LazyOptional.of(() -> this);

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == MatterOverdriveCapabilities.OVERWORLD_DATA) {
			return lazyOptional.cast();
		}
		return LazyOptional.empty();
	}

	private List<ActiveTransportDataWrapper> transportData = new ArrayList<>();

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		tag.putInt("transportsize", transportData.size());
		for (int i = 0; i < transportData.size(); i++) {
			transportData.get(i).serializeNbt(tag, "transportdata" + i);
		}
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		int size = nbt.getInt("transportsize");
		for (int i = 0; i < size; i++) {
			transportData.add(ActiveTransportDataWrapper.deserializeNbt(nbt.getCompound("transportdata" + i)));
		}
	}

	@Override
	public List<ActiveTransportDataWrapper> getTransporterData() {
		return transportData;
	}

	@Override
	public void addActiveTransport(ActiveTransportDataWrapper wrapper) {
		transportData.add(wrapper);
	}

	@Override
	public void removeTransportData(ActiveTransportDataWrapper wrapper) {
		transportData.remove(wrapper);
	}

}
