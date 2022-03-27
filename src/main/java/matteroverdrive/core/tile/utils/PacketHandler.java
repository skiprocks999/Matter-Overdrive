package matteroverdrive.core.tile.utils;

import java.util.function.Consumer;

import matteroverdrive.core.packet.NetworkHandler;
import matteroverdrive.core.packet.type.PacketUpdateTile;
import matteroverdrive.core.tile.GenericTile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;

// Credit goes to AurilisDev for this class idea
public class PacketHandler {

	private GenericTile owner;
	
	public PacketHandler(GenericTile owner) {
		this.owner = owner;
	}

	protected Consumer<CompoundTag> customPacketWriter;
	protected Consumer<CompoundTag> guiPacketWriter;
	protected Consumer<CompoundTag> customPacketReader;
	protected Consumer<CompoundTag> guiPacketReader;

	public PacketHandler customPacketWriter(Consumer<CompoundTag> consumer) {
		Consumer<CompoundTag> safe = consumer;
		if (customPacketWriter != null) {
			safe = safe.andThen(customPacketWriter);
		}
		customPacketWriter = safe;
		return this;
	}

	public PacketHandler guiPacketWriter(Consumer<CompoundTag> consumer) {
		Consumer<CompoundTag> safe = consumer;
		if (guiPacketWriter != null) {
			safe = safe.andThen(guiPacketWriter);
		}
		guiPacketWriter = safe;
		return this;
	}

	public PacketHandler customPacketReader(Consumer<CompoundTag> consumer) {
		Consumer<CompoundTag> safe = consumer;
		if (customPacketReader != null) {
			safe = safe.andThen(customPacketReader);
		}
		customPacketReader = safe;
		return this;
	}

	public PacketHandler guiPacketReader(Consumer<CompoundTag> consumer) {
		Consumer<CompoundTag> safe = consumer;
		if (guiPacketReader != null) {
			safe = safe.andThen(guiPacketReader);
		}
		guiPacketReader = safe;
		return this;
	}

	public Consumer<CompoundTag> getCustomPacketSupplier() {
		return customPacketWriter;
	}

	public Consumer<CompoundTag> getGuiPacketSupplier() {
		return guiPacketWriter;
	}

	public Consumer<CompoundTag> getCustomPacketConsumer() {
		return customPacketReader;
	}

	public Consumer<CompoundTag> getGuiPacketConsumer() {
		return guiPacketReader;
	}

	public void sendCustomPacket() {
		PacketUpdateTile packet = new PacketUpdateTile(this, owner.getBlockPos(), false, new CompoundTag());
		Level world = owner.getLevel();
		BlockPos pos = owner.getBlockPos();
		if (world instanceof ServerLevel level) {
			level.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false).forEach(p -> NetworkHandler.CHANNEL.sendTo(packet, p.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT));
		}
	}

	public void sendGuiPacketToTracking() {
		PacketUpdateTile packet = new PacketUpdateTile(this, owner.getBlockPos(), true, new CompoundTag());
		Level world = owner.getLevel();
		BlockPos pos = owner.getBlockPos();
		if (world instanceof ServerLevel level) {
			level.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false).forEach(p -> NetworkHandler.CHANNEL.sendTo(packet, p.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT));
		}
	}

	
}
