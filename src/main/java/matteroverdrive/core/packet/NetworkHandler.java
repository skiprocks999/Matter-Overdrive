package matteroverdrive.core.packet;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import matteroverdrive.References;
import matteroverdrive.core.packet.type.serverbound.android.*;
import matteroverdrive.core.packet.type.serverbound.misc.*;
import matteroverdrive.core.packet.type.serverbound.property.*;
import matteroverdrive.core.packet.type.AbstractOverdrivePacket;
import matteroverdrive.core.packet.type.clientbound.android.*;
import matteroverdrive.core.packet.type.clientbound.misc.*;
import matteroverdrive.core.packet.type.clientbound.property.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.simple.SimpleChannel.MessageBuilder;
import net.minecraftforge.network.simple.SimpleChannel.MessageBuilder.ToBooleanBiFunction;

public class NetworkHandler {
	private static final String PROTOCOL_VERSION = "1";
	private static int disc = 0;
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(References.ID, "main_channel"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals);

	public static void init() {
		/* SERVER-BOUND */

		serverMessage(PacketUpdateCapabilitySides.class, PacketUpdateCapabilitySides::encode,
				PacketUpdateCapabilitySides::decode, PacketUpdateCapabilitySides::handle);

		serverMessage(PacketToggleMatterScanner.class, PacketToggleMatterScanner::encode,
				PacketToggleMatterScanner::decode, PacketToggleMatterScanner::handle);

		serverMessage(PacketQueueReplication.class, PacketQueueReplication::encode, PacketQueueReplication::decode,
				PacketQueueReplication::handle);

		serverMessage(PacketCancelReplication.class, PacketCancelReplication::encode, PacketCancelReplication::decode,
				PacketCancelReplication::handle);

		serverMessage(PacketUpdateServerContainerProperty.class, PacketUpdateServerContainerProperty::encode,
				PacketUpdateServerContainerProperty::decode, PacketUpdateServerContainerProperty::handle);

		serverMessage(PacketUpdateServerEntityProperty.class, PacketUpdateServerEntityProperty::encode,
				PacketUpdateServerEntityProperty::decode, PacketUpdateServerEntityProperty::handle);

		serverMessage(PacketUpdateServerTileProperty.class, PacketUpdateServerTileProperty::encode,
				PacketUpdateServerTileProperty::decode, PacketUpdateServerTileProperty::handle);

		serverMessage(PacketUpdateServerTileProperty.class, PacketUpdateServerTileProperty::encode,
				PacketUpdateServerTileProperty::decode, PacketUpdateServerTileProperty::handle);

		serverMessage(PacketUpdateServerTileProperty.class, PacketUpdateServerTileProperty::encode,
				PacketUpdateServerTileProperty::decode, PacketUpdateServerTileProperty::handle);
		
		serverMessage(PacketAndroidPerkAttemptBuy.class, PacketAndroidPerkAttemptBuy::encode,
				PacketAndroidPerkAttemptBuy::decode, PacketAndroidPerkAttemptBuy::handle);
		
		serverMessage(PacketAndroidPerkToggle.class, PacketAndroidPerkToggle::encode,
				PacketAndroidPerkToggle::decode, PacketAndroidPerkToggle::handle);

		/* CLIENT-BOUND */

		clientMessage(PacketClientMatterValues.class, PacketClientMatterValues::encode,
				PacketClientMatterValues::decode, PacketClientMatterValues::handle);

		clientMessage(PacketSyncClientEntityCapability.class, PacketSyncClientEntityCapability::encode,
				PacketSyncClientEntityCapability::decode, PacketSyncClientEntityCapability::handle);

		clientMessage(PacketClientMNData.class, PacketClientMNData::encode, PacketClientMNData::decode,
				PacketClientMNData::handle);

		clientMessage(PacketPlayMatterScannerSound.class, PacketPlayMatterScannerSound::encode,
				PacketPlayMatterScannerSound::decode, PacketPlayMatterScannerSound::handle);

		clientMessage(PacketUpdateClientContainerProperty.class, PacketUpdateClientContainerProperty::encode,
				PacketUpdateClientContainerProperty::decode, PacketUpdateClientContainerProperty::handle);

		clientMessage(PacketUpdateClientEntityProperty.class, PacketUpdateClientEntityProperty::encode,
				PacketUpdateClientEntityProperty::decode, PacketUpdateClientEntityProperty::handle);

		clientMessage(PacketUpdateClientTileProperty.class, PacketUpdateClientTileProperty::encode,
				PacketUpdateClientTileProperty::decode, PacketUpdateClientTileProperty::handle);
		
		clientMessage(PacketAndroidEnergySync.class, PacketAndroidEnergySync::encode, PacketAndroidEnergySync::decode,
				PacketAndroidEnergySync::handle);
		
		clientMessage(PacketAndroidSyncAll.class, PacketAndroidSyncAll::encode, PacketAndroidSyncAll::decode,
				PacketAndroidSyncAll::handle);
		
		clientMessage(PacketAndroidTurningTimeSync.class, PacketAndroidTurningTimeSync::encode, PacketAndroidTurningTimeSync::decode,
				PacketAndroidTurningTimeSync::handle);

		clientMessage(PacketClientUpdateMNScreen.class, PacketClientUpdateMNScreen::encode,
			PacketClientUpdateMNScreen::decode,	PacketClientUpdateMNScreen::handle);
	}
	
	public static void sendToClientPlayer(ServerPlayer player, AbstractOverdrivePacket<?> packet) {
		CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
	}

	public static void sendToClientEntity(Entity entity, AbstractOverdrivePacket<?> packet) {
		CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), packet);
	}
	
	public static void sendToClientEntityAndSelf(Entity entity, AbstractOverdrivePacket<?> packet) {
		CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), packet);
	}
	
	public static void sendToClientChunk(LevelChunk chunk, AbstractOverdrivePacket<?> packet) {
		CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), packet);
	}
	
	public static void sendToServer(AbstractOverdrivePacket<?> message) {
		CHANNEL.send(PacketDistributor.SERVER.noArg(), message);
	}
	
	private static <T extends AbstractOverdrivePacket<T>> void clientMessage(Class<T> clazz, BiConsumer<T, FriendlyByteBuf> encoder,
			Function<FriendlyByteBuf, T> decoder, ToBooleanBiFunction<T, Supplier<NetworkEvent.Context>> handler) {
		message(clazz, NetworkDirection.PLAY_TO_CLIENT).encoder(encoder).decoder(decoder)
				.consumerNetworkThread(handler).add();
	}

	private static <T extends AbstractOverdrivePacket<T>> void serverMessage(Class<T> clazz, BiConsumer<T, FriendlyByteBuf> encoder,
			Function<FriendlyByteBuf, T> decoder, ToBooleanBiFunction<T, Supplier<NetworkEvent.Context>> handler) {
		message(clazz, NetworkDirection.PLAY_TO_SERVER).encoder(encoder).decoder(decoder)
				.consumerNetworkThread(handler).add();
	}

	private static <T extends AbstractOverdrivePacket<T>> MessageBuilder<T> message(Class<T> clazz, NetworkDirection dir) {
		return CHANNEL.messageBuilder(clazz, disc++, dir);
	}
}
