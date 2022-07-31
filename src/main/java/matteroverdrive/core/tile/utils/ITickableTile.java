package matteroverdrive.core.tile.utils;

import matteroverdrive.core.packet.NetworkHandler;
import matteroverdrive.core.packet.type.clientbound.PacketUpdateTile;
import matteroverdrive.core.tile.types.old.GenericTile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;

public interface ITickableTile {
	
	long getTicks();
	
	void incrementTicks();
	
	//DO NOT OVERRIDE
	default void tick(Level world, GenericTile tile) {
		tickCommon();
		incrementTicks();
		if(world.isClientSide) {
			tickClient();
		} else {
			tickServer();
			if(getTicks() % 2 == 0 && tile.hasRenderData) {
				Level level = tile.getLevel();
				BlockPos pos = tile.getBlockPos();
				if (level instanceof ServerLevel server) {
					PacketUpdateTile packet = new PacketUpdateTile(pos, tile, false);
					server.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false).forEach(p -> {
						NetworkHandler.CHANNEL.sendTo(packet, p.connection.getConnection(),
								NetworkDirection.PLAY_TO_CLIENT);
					});
				}
			}
		}
	}
	
	default void tickServer() {
		
	}
	
	default void tickCommon() {
		
	}
	
	default void tickClient() {
		
	}

}
