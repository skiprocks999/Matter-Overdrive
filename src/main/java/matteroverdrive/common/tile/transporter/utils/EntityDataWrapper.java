package matteroverdrive.common.tile.transporter.utils;

import net.minecraft.nbt.CompoundTag;

public record EntityDataWrapper(float bbHeight, float bbWidth, double xPos, double zPos) {

	public EntityDataWrapper {

	}

	public void serializeNbt(CompoundTag parent, String key) {
		CompoundTag data = new CompoundTag();
		data.putFloat("height", bbHeight);
		data.putFloat("width", bbWidth);
		data.putDouble("x", xPos);
		data.putDouble("z", zPos);

		parent.put(key, data);
	}

	public static EntityDataWrapper fromNbt(CompoundTag data) {
		float bbHeight = data.getFloat("height");
		float bbWidth = data.getFloat("width");
		double x = data.getDouble("x");
		double z = data.getDouble("z");
		return new EntityDataWrapper(bbHeight, bbWidth, x, z);
	}

}
