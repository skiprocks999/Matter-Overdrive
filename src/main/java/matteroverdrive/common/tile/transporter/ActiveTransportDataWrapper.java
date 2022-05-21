package matteroverdrive.common.tile.transporter;

import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

public class ActiveTransportDataWrapper {
	
	public int timeRemaining;
	public UUID entityID;
	public BlockPos destination;
	
	public ActiveTransportDataWrapper(UUID id, int timeRemaining) {
		this.timeRemaining = timeRemaining;
		entityID = id;
	}
	
	public void serializeNbt(CompoundTag tag, String key) {
		CompoundTag data = new CompoundTag();
		data.putUUID("uuid", entityID);
		data.putInt("timer", timeRemaining);
		tag.put(key, data);
	}
	
	public static ActiveTransportDataWrapper deserializeNbt(CompoundTag tag) {
		return new ActiveTransportDataWrapper(tag.getUUID("uuid"), tag.getInt("timer"));
	}

}
