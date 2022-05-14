package matteroverdrive.core.tile.utils;

import matteroverdrive.core.utils.UtilsText;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class TransporterLocationWrapper {

	private static final TranslatableComponent DEFUALT_NAME = UtilsText.gui("unknown");

	private TextComponent customName;
	private boolean hasCustomName;

	private BlockPos destination = new BlockPos(0, -60, 0);

	public TransporterLocationWrapper() {

	}

	public Component getName() {
		if (hasCustomName) {
			return customName;
		}
		return DEFUALT_NAME;
	}

	public BlockPos getDestination() {
		return destination;
	}

	public void setName(String name) {
		if (name.length() <= 0) {
			hasCustomName = false;
		}
		hasCustomName = true;
		customName = new TextComponent(name);
	}

	public void serializeNbt(CompoundTag parent, String key) {
		CompoundTag tag = new CompoundTag();
		tag.put("destination", NbtUtils.writeBlockPos(destination));
		tag.putBoolean("hasCustom", hasCustomName);
		if (hasCustomName) {
			tag.putString("custom", customName.getContents());
		}
		parent.put(key, tag);
	}

	public void deserializeNbt(CompoundTag data) {
		destination = NbtUtils.readBlockPos(data.getCompound("destination"));
		hasCustomName = data.getBoolean("hasCustom");
		if (hasCustomName) {
			customName = new TextComponent(data.getString("custom"));
		}
	}

}
