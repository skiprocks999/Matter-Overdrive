package matteroverdrive.core.tile.utils;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import matteroverdrive.core.packet.NetworkHandler;
import matteroverdrive.core.packet.type.PacketUpdateTile;
import matteroverdrive.core.tile.GenericTile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;

// Credit goes to AurilisDev for this class idea
public class PacketHandler {

	private GenericTile owner;
	private boolean isGuiPacket;

	public PacketHandler(GenericTile owner, boolean isGuiPacket) {
		this.owner = owner;
		this.isGuiPacket = isGuiPacket;
	}

	protected Consumer<CompoundTag> customPacketWriter;
	protected Consumer<CompoundTag> customPacketReader;

	public PacketHandler packetWriter(Consumer<CompoundTag> consumer) {
		Consumer<CompoundTag> safe = consumer;
		if (customPacketWriter != null) {
			safe = safe.andThen(customPacketWriter);
		}
		customPacketWriter = safe;
		return this;
	}

	public PacketHandler packetReader(Consumer<CompoundTag> consumer) {
		Consumer<CompoundTag> safe = consumer;
		if (customPacketReader != null) {
			safe = safe.andThen(customPacketReader);
		}
		customPacketReader = safe;
		return this;
	}

	public Consumer<CompoundTag> getPacketSupplier() {
		return customPacketWriter;
	}

	public Consumer<CompoundTag> getPacketConsumer() {
		return customPacketReader;
	}

	public void sendCustomPacket(@Nullable Player player) {
		PacketUpdateTile packet = new PacketUpdateTile(this, owner.getBlockPos(), new CompoundTag(), isGuiPacket);
		if (player != null && player instanceof ServerPlayer server) {
			NetworkHandler.CHANNEL.sendTo(packet, server.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
		} else {
			Level world = owner.getLevel();
			BlockPos pos = owner.getBlockPos();
			if (world instanceof ServerLevel level) {
				level.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false).forEach(p -> NetworkHandler.CHANNEL
						.sendTo(packet, p.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT));
			}
		}
	}

}
