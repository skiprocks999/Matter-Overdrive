package matteroverdrive.common.tile.transporter.utils;

import java.util.UUID;

import matteroverdrive.core.utils.UtilsNbt;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class ActiveTransportDataWrapper {

	public int timeRemaining;
	public UUID entityID;
	public ResourceKey<Level> dimension;

	public ActiveTransportDataWrapper(UUID id, int timeRemaining, ResourceKey<Level> dimension) {
		this.timeRemaining = timeRemaining;
		entityID = id;
		this.dimension = dimension;
	}

	public void serializeNbt(CompoundTag tag, String key) {
		CompoundTag data = new CompoundTag();
		data.putUUID("uuid", entityID);
		data.putInt("timer", timeRemaining);
		tag.put(UtilsNbt.DIMENSION, UtilsNbt.writeDimensionToTag(dimension));
		tag.put(key, data);
	}

	public static ActiveTransportDataWrapper deserializeNbt(CompoundTag tag) {
		return new ActiveTransportDataWrapper(tag.getUUID("uuid"), tag.getInt("timer"),
				UtilsNbt.readDimensionFromTag(tag.getCompound(UtilsNbt.DIMENSION)));
	}

}
