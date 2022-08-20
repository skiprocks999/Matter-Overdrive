package matteroverdrive.core.packet;

import java.util.Optional;

import matteroverdrive.References;
import matteroverdrive.core.packet.type.clientbound.PacketClientMNData;
import matteroverdrive.core.packet.type.clientbound.PacketClientMatterValues;
import matteroverdrive.core.packet.type.clientbound.PacketPlayMatterScannerSound;
import matteroverdrive.core.packet.type.clientbound.PacketSyncClientEntityCapability;
import matteroverdrive.core.packet.type.clientbound.PacketUpdateTile;
import matteroverdrive.core.packet.type.serverbound.PacketCancelReplication;
import matteroverdrive.core.packet.type.serverbound.PacketQueueReplication;
import matteroverdrive.core.packet.type.serverbound.PacketToggleMatterScanner;
import matteroverdrive.core.packet.type.serverbound.PacketUpdateCapabilitySides;
import matteroverdrive.core.packet.type.serverbound.PacketUpdateRedstoneMode;
import matteroverdrive.core.packet.type.serverbound.PacketUpdateTransporterLocationInfo;
import matteroverdrive.core.property.message.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {

	private static final String PROTOCOL_VERSION = "1";
	private static int disc = 0;
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(References.ID, "main_channel"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals);

	public static void init() {

		/* SERVER-BOUND */

		CHANNEL.registerMessage(disc++, PacketUpdateRedstoneMode.class, PacketUpdateRedstoneMode::encode,
				PacketUpdateRedstoneMode::decode, PacketUpdateRedstoneMode::handle,
				Optional.of(NetworkDirection.PLAY_TO_SERVER));
		CHANNEL.registerMessage(disc++, PacketUpdateCapabilitySides.class, PacketUpdateCapabilitySides::encode,
				PacketUpdateCapabilitySides::decode, PacketUpdateCapabilitySides::handle,
				Optional.of(NetworkDirection.PLAY_TO_SERVER));
		CHANNEL.registerMessage(disc++, PacketUpdateTransporterLocationInfo.class,
				PacketUpdateTransporterLocationInfo::encode, PacketUpdateTransporterLocationInfo::decode,
				PacketUpdateTransporterLocationInfo::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		CHANNEL.registerMessage(disc++, PacketToggleMatterScanner.class, PacketToggleMatterScanner::encode,
				PacketToggleMatterScanner::decode, PacketToggleMatterScanner::handle,
				Optional.of(NetworkDirection.PLAY_TO_SERVER));
		CHANNEL.registerMessage(disc++, PacketQueueReplication.class, PacketQueueReplication::encode,
				PacketQueueReplication::decode, PacketQueueReplication::handle,
				Optional.of(NetworkDirection.PLAY_TO_SERVER));
		CHANNEL.registerMessage(disc++, PacketCancelReplication.class, PacketCancelReplication::encode,
				PacketCancelReplication::decode, PacketCancelReplication::handle,
				Optional.of(NetworkDirection.PLAY_TO_SERVER));
		
		CHANNEL.messageBuilder(UpdateServerContainerPropertyMessage.class, disc++, NetworkDirection.PLAY_TO_SERVER)
				.encoder(UpdateServerContainerPropertyMessage::encode)
				.decoder(UpdateServerContainerPropertyMessage::decode)
				.consumerNetworkThread(UpdateServerContainerPropertyMessage::consume).add();
		
		CHANNEL.messageBuilder(UpdateServerEntityPropertyMessage.class, disc++, NetworkDirection.PLAY_TO_SERVER)
				.encoder(UpdateServerEntityPropertyMessage::encode).decoder(UpdateServerEntityPropertyMessage::decode)
				.consumerNetworkThread(UpdateServerEntityPropertyMessage::consume).add();
		
		CHANNEL.messageBuilder(UpdateServerBlockEntityPropertyMessage.class, disc++, NetworkDirection.PLAY_TO_SERVER)
				.encoder(UpdateServerBlockEntityPropertyMessage::encode)
				.decoder(UpdateServerBlockEntityPropertyMessage::decode)
				.consumerNetworkThread(UpdateServerBlockEntityPropertyMessage::consume).add();

		/* CLIENT-BOUND */

		CHANNEL.registerMessage(disc++, PacketUpdateTile.class, PacketUpdateTile::encode, PacketUpdateTile::decode,
				PacketUpdateTile::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		CHANNEL.registerMessage(disc++, PacketClientMatterValues.class, PacketClientMatterValues::encode,
				PacketClientMatterValues::decode, PacketClientMatterValues::handle,
				Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		CHANNEL.registerMessage(disc++, PacketSyncClientEntityCapability.class,
				PacketSyncClientEntityCapability::encode, PacketSyncClientEntityCapability::decode,
				PacketSyncClientEntityCapability::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		CHANNEL.registerMessage(disc++, PacketClientMNData.class, PacketClientMNData::encode,
				PacketClientMNData::decode, PacketClientMNData::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		CHANNEL.registerMessage(disc++, PacketPlayMatterScannerSound.class, PacketPlayMatterScannerSound::encode,
				PacketPlayMatterScannerSound::decode, PacketPlayMatterScannerSound::handle,
				Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		
		CHANNEL.messageBuilder(UpdateClientContainerPropertyMessage.class, disc++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(UpdateClientContainerPropertyMessage::encode)
				.decoder(UpdateClientContainerPropertyMessage::decode)
				.consumerNetworkThread(UpdateClientContainerPropertyMessage::consume).add();
		
		CHANNEL.messageBuilder(UpdateClientEntityPropertyMessage.class, disc++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(UpdateClientEntityPropertyMessage::encode).decoder(UpdateClientEntityPropertyMessage::decode)
				.consumerNetworkThread(UpdateClientEntityPropertyMessage::consume).add();
		
		CHANNEL.messageBuilder(UpdateClientBlockEntityPropertyMessage.class, disc++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(UpdateClientBlockEntityPropertyMessage::encode)
				.decoder(UpdateClientBlockEntityPropertyMessage::decode)
				.consumerNetworkThread(UpdateClientBlockEntityPropertyMessage::consume).add();
	}

	public static void sendUpdateClientContainerProperties(ServerPlayer player,
			UpdateClientContainerPropertyMessage message) {
		CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
	}

	public static void sendUpdateServerContainerProperties(UpdateServerContainerPropertyMessage message) {
		CHANNEL.send(PacketDistributor.SERVER.noArg(), message);
	}

	public static void sendUpdateClientEntityProperties(Entity entity, UpdateClientEntityPropertyMessage message) {
		CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), message);
	}

	public static void sendUpdateServerEntityProperties(UpdateServerEntityPropertyMessage message) {
		CHANNEL.send(PacketDistributor.TRACKING_ENTITY.noArg(), message);
	}

	public static void sendUpdateClientBlockEntityProperties(LevelChunk chunk,
			UpdateClientBlockEntityPropertyMessage message) {
		CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), message);
	}

	public static void sendUpdateServerBlockEntityProperties(LevelChunk chunk,
			UpdateServerBlockEntityPropertyMessage message) {
		CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), message);
	}

}
