package matteroverdrive.core.packet;

import java.util.Optional;

import matteroverdrive.References;
import matteroverdrive.core.packet.type.serverbound.*;
import matteroverdrive.core.property.packet.serverbound.*;
import matteroverdrive.core.packet.type.clientbound.*;
import matteroverdrive.core.property.packet.clientbound.*;
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

		CHANNEL.registerMessage(disc++, PacketUpdateCapabilitySides.class, PacketUpdateCapabilitySides::encode,
				PacketUpdateCapabilitySides::decode, PacketUpdateCapabilitySides::handle,
				Optional.of(NetworkDirection.PLAY_TO_SERVER));
		CHANNEL.registerMessage(disc++, PacketToggleMatterScanner.class, PacketToggleMatterScanner::encode,
				PacketToggleMatterScanner::decode, PacketToggleMatterScanner::handle,
				Optional.of(NetworkDirection.PLAY_TO_SERVER));
		CHANNEL.registerMessage(disc++, PacketQueueReplication.class, PacketQueueReplication::encode,
				PacketQueueReplication::decode, PacketQueueReplication::handle,
				Optional.of(NetworkDirection.PLAY_TO_SERVER));
		CHANNEL.registerMessage(disc++, PacketCancelReplication.class, PacketCancelReplication::encode,
				PacketCancelReplication::decode, PacketCancelReplication::handle,
				Optional.of(NetworkDirection.PLAY_TO_SERVER));

		CHANNEL.messageBuilder(PacketUpdateServerContainerProperty.class, disc++, NetworkDirection.PLAY_TO_SERVER)
				.encoder(PacketUpdateServerContainerProperty::encode)
				.decoder(PacketUpdateServerContainerProperty::decode)
				.consumerNetworkThread(PacketUpdateServerContainerProperty::consume).add();

		CHANNEL.messageBuilder(PacketUpdateServerEntityProperty.class, disc++, NetworkDirection.PLAY_TO_SERVER)
				.encoder(PacketUpdateServerEntityProperty::encode).decoder(PacketUpdateServerEntityProperty::decode)
				.consumerNetworkThread(PacketUpdateServerEntityProperty::consume).add();

		CHANNEL.messageBuilder(PacketUpdateServerTileProperty.class, disc++, NetworkDirection.PLAY_TO_SERVER)
				.encoder(PacketUpdateServerTileProperty::encode)
				.decoder(PacketUpdateServerTileProperty::decode)
				.consumerNetworkThread(PacketUpdateServerTileProperty::consume).add();

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

		CHANNEL.messageBuilder(PacketUpdateClientContainerProperty.class, disc++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(PacketUpdateClientContainerProperty::encode)
				.decoder(PacketUpdateClientContainerProperty::decode)
				.consumerNetworkThread(PacketUpdateClientContainerProperty::consume).add();

		CHANNEL.messageBuilder(PacketUpdateClientEntityProperty.class, disc++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(PacketUpdateClientEntityProperty::encode).decoder(PacketUpdateClientEntityProperty::decode)
				.consumerNetworkThread(PacketUpdateClientEntityProperty::consume).add();

		CHANNEL.messageBuilder(PacketUpdateClientTileProperty.class, disc++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(PacketUpdateClientTileProperty::encode)
				.decoder(PacketUpdateClientTileProperty::decode)
				.consumerNetworkThread(PacketUpdateClientTileProperty::consume).add();
	}

	public static void sendUpdateClientContainerProperties(ServerPlayer player,
			PacketUpdateClientContainerProperty message) {
		CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
	}

	public static void sendUpdateServerContainerProperties(PacketUpdateServerContainerProperty message) {
		CHANNEL.send(PacketDistributor.SERVER.noArg(), message);
	}

	public static void sendUpdateClientEntityProperties(Entity entity, PacketUpdateClientEntityProperty message) {
		CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), message);
	}

	public static void sendUpdateServerEntityProperties(PacketUpdateServerEntityProperty message) {
		CHANNEL.send(PacketDistributor.SERVER.noArg(), message);
	}

	public static void sendUpdateClientBlockEntityProperties(LevelChunk chunk,
			PacketUpdateClientTileProperty message) {
		CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), message);
	}

	public static void sendUpdateServerBlockEntityProperties(LevelChunk chunk,
			PacketUpdateServerTileProperty message) {
		CHANNEL.send(PacketDistributor.SERVER.noArg(), message);
	}

	public static void sendToPlayer(Object obj, ServerPlayer player) {
		CHANNEL.sendTo(obj, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
	}

	public static void sendToServer(Object obj) {
		CHANNEL.sendToServer(obj);
	}

}
