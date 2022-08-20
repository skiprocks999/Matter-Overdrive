package matteroverdrive.core.packet.type.clientbound;

import java.util.UUID;
import java.util.function.Supplier;

import matteroverdrive.core.packet.PacketBarrierMethods;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent.Context;

public class PacketPlayMatterScannerSound {

	private final UUID id;
	private final InteractionHand hand;

	public PacketPlayMatterScannerSound(UUID id, InteractionHand hand) {
		this.id = id;
		this.hand = hand;
	}

	public static void handle(PacketPlayMatterScannerSound message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			PacketBarrierMethods.handlePacketPlayMatterScannerSound(message.id, message.hand);
		});
		ctx.setPacketHandled(true);
	}

	public static void encode(PacketPlayMatterScannerSound pkt, FriendlyByteBuf buf) {
		buf.writeUUID(pkt.id);
		buf.writeEnum(pkt.hand);
	}

	public static PacketPlayMatterScannerSound decode(FriendlyByteBuf buf) {
		return new PacketPlayMatterScannerSound(buf.readUUID(), buf.readEnum(InteractionHand.class));
	}

}
