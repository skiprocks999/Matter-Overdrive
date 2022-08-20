package matteroverdrive.core.capability.types.item_pattern;

import javax.annotation.Nullable;

import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class CapabilityItemPatternStorage
		implements ICapabilityItemPatternStorage, ICapabilitySerializable<CompoundTag> {

	private final LazyOptional<ICapabilityItemPatternStorage> lazyOptional = LazyOptional.of(() -> this);

	public CapabilityItemPatternStorage() {
		patterns = new ItemPatternWrapper[] { ItemPatternWrapper.EMPTY, ItemPatternWrapper.EMPTY,
				ItemPatternWrapper.EMPTY };
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == MatterOverdriveCapabilities.STORED_PATTERNS) {
			return lazyOptional.cast();
		}
		return LazyOptional.empty();
	}

	@Nullable
	private ItemPatternWrapper[] patterns;

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag data = new CompoundTag();
		for (int i = 0; i < 3; i++) {
			patterns[i].writeToNbt(data, "pattern" + i);
		}
		return data;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		patterns = new ItemPatternWrapper[3];
		for (int i = 0; i < 3; i++) {
			patterns[i] = ItemPatternWrapper.readFromNbt(nbt.getCompound("pattern" + i));
		}
	}

	@Override
	public ItemPatternWrapper[] getStoredPatterns() {
		return patterns;
	}

}
