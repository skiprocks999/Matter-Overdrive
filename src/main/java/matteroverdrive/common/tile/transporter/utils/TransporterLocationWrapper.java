package matteroverdrive.common.tile.transporter.utils;

import matteroverdrive.core.utils.UtilsNbt;
import matteroverdrive.core.utils.UtilsText;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class TransporterLocationWrapper {

	public static final MutableComponent DEFAULT_NAME = UtilsText.gui("unknown");

	private MutableComponent customName;
	private boolean hasCustomName;

	private BlockPos destination = new BlockPos(0, -1000, 0);

	private ResourceKey<Level> dimension;

	public TransporterLocationWrapper() {

	}

	public Component getName() {
		if (hasCustomName) {
			return customName;
		}
		return DEFAULT_NAME;
	}

	public BlockPos getDestination() {
		return destination;
	}

	public void setDestination(BlockPos pos) {
		destination = pos;
	}

	public void setName(String name) {
		if (name.length() <= 0) {
			hasCustomName = false;
		}
		hasCustomName = true;
		customName = Component.literal(name);
	}

	public ResourceKey<Level> getDimension() {
		return dimension;
	}

	public void setDimension(ResourceKey<Level> dimension) {
		this.dimension = dimension;
	}

	public CompoundTag serializeNbt() {
		CompoundTag tag = new CompoundTag();
		tag.put("destination", NbtUtils.writeBlockPos(destination));
		tag.putBoolean("hasCustom", hasCustomName);
		if (hasCustomName) {
			tag.putString("custom", customName.getString());
		}
		if (dimension != null) {
			tag.put(UtilsNbt.DIMENSION, UtilsNbt.writeDimensionToTag(dimension));
		}
		return tag;
	}

	public void deserializeNbt(CompoundTag data) {
		destination = NbtUtils.readBlockPos(data.getCompound("destination"));
		hasCustomName = data.getBoolean("hasCustom");
		if (hasCustomName) {
			customName = Component.literal(data.getString("custom"));
		}
		if (data.contains("ownerid")) {
			dimension = UtilsNbt.readDimensionFromTag(data.getCompound(UtilsNbt.DIMENSION));
		}
	}

	public TransporterLocationWrapper copy() {
		TransporterLocationWrapper wrapper = new TransporterLocationWrapper();
		wrapper.customName = this.customName;
		wrapper.destination = this.destination;
		wrapper.dimension = this.dimension;
		wrapper.hasCustomName = this.hasCustomName;
		return wrapper;
	}

}
