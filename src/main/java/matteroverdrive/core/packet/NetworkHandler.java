package matteroverdrive.core.packet;

import java.util.Optional;

import matteroverdrive.References;
import matteroverdrive.core.packet.type.PacketClientMNData;
import matteroverdrive.core.packet.type.PacketClientMatterValues;
import matteroverdrive.core.packet.type.PacketSyncClientEntityCapability;
import matteroverdrive.core.packet.type.PacketUpdateTransporterLocationInfo;
import matteroverdrive.core.packet.type.PacketUpdateCapabilitySides;
import matteroverdrive.core.packet.type.PacketUpdateRedstoneMode;
import matteroverdrive.core.packet.type.PacketUpdateTile;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {

	private static final String PROTOCOL_VERSION = "1";
	private static int disc = 0;
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(References.ID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals);

	public static void init() {
		CHANNEL.registerMessage(disc++, PacketUpdateTile.class, PacketUpdateTile::encode, PacketUpdateTile::decode,
				PacketUpdateTile::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		CHANNEL.registerMessage(disc++, PacketClientMatterValues.class, PacketClientMatterValues::encode,
				PacketClientMatterValues::decode, PacketClientMatterValues::handle,
				Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		CHANNEL.registerMessage(disc++, PacketUpdateRedstoneMode.class, PacketUpdateRedstoneMode::encode,
				PacketUpdateRedstoneMode::decode, PacketUpdateRedstoneMode::handle,
				Optional.of(NetworkDirection.PLAY_TO_SERVER));
		CHANNEL.registerMessage(disc++, PacketUpdateCapabilitySides.class, PacketUpdateCapabilitySides::encode,
				PacketUpdateCapabilitySides::decode, PacketUpdateCapabilitySides::handle,
				Optional.of(NetworkDirection.PLAY_TO_SERVER));
		CHANNEL.registerMessage(disc++, PacketUpdateTransporterLocationInfo.class,
				PacketUpdateTransporterLocationInfo::encode, PacketUpdateTransporterLocationInfo::decode,
				PacketUpdateTransporterLocationInfo::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		CHANNEL.registerMessage(disc++, PacketSyncClientEntityCapability.class,
				PacketSyncClientEntityCapability::encode, PacketSyncClientEntityCapability::decode,
				PacketSyncClientEntityCapability::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		CHANNEL.registerMessage(disc++, PacketClientMNData.class,
				PacketClientMNData::encode, PacketClientMNData::decode,
				PacketClientMNData::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
	}

}
