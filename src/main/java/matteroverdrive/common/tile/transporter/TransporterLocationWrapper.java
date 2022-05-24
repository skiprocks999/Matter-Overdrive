package matteroverdrive.common.tile.transporter;

import matteroverdrive.core.utils.UtilsNbt;
import matteroverdrive.core.utils.UtilsText;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class TransporterLocationWrapper {

	public static final TranslatableComponent DEFAULT_NAME = UtilsText.gui("unknown");

	private TextComponent customName;
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
		customName = new TextComponent(name);
	}
	
	public ResourceKey<Level> getDimension() {
		return dimension;
	}
	
	public void setDimension(ResourceKey<Level> dimension) {
		this.dimension = dimension;
	}

	public void serializeNbt(CompoundTag parent, String key) {
		CompoundTag tag = new CompoundTag();
		tag.put("destination", NbtUtils.writeBlockPos(destination));
		tag.putBoolean("hasCustom", hasCustomName);
		if (hasCustomName) {
			tag.putString("custom", customName.getContents());
		}
		if(dimension != null) {
			tag.put(UtilsNbt.DIMENSION, UtilsNbt.writeDimensionToTag(dimension));
		}
		parent.put(key, tag);
	}

	public void deserializeNbt(CompoundTag data) {
		destination = NbtUtils.readBlockPos(data.getCompound("destination"));
		hasCustomName = data.getBoolean("hasCustom");
		if (hasCustomName) {
			customName = new TextComponent(data.getString("custom"));
		}
		if(data.contains("ownerid")) {
			dimension = UtilsNbt.readDimensionFromTag(data.getCompound(UtilsNbt.DIMENSION));
		}
	}

}
