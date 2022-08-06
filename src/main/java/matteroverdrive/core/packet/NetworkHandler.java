package matteroverdrive.core.packet;

import java.util.Optional;

import matteroverdrive.References;
import matteroverdrive.core.component.util.property.message.UpdateClientContainerPropertiesMessage;
import matteroverdrive.core.component.util.property.message.UpdateServerContainerPropertyMessage;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {

	private static final String PROTOCOL_VERSION = "1";
	private static int disc = 0;
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(References.ID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals,
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

	}

	public static void sendUpdateClientContainerProperties(ServerPlayer playerEntity,
			UpdateClientContainerPropertiesMessage message) {
		CHANNEL.send(PacketDistributor.PLAYER.with(() -> playerEntity), message);
	}

	public static void sendUpdateServerContainerProperties(UpdateServerContainerPropertyMessage message) {
		CHANNEL.send(PacketDistributor.SERVER.noArg(), message);
	}

}
