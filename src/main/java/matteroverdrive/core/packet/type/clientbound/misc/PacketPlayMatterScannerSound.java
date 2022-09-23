package matteroverdrive.core.packet.type.clientbound.misc;

import java.util.UUID;
import java.util.function.Supplier;

import matteroverdrive.core.packet.type.AbstractOverdrivePacket;
import matteroverdrive.core.packet.type.clientbound.PacketBarrierMethods;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent.Context;

public class PacketPlayMatterScannerSound extends AbstractOverdrivePacket<PacketPlayMatterScannerSound> {

	private final UUID id;
	private final InteractionHand hand;

	public PacketPlayMatterScannerSound(UUID id, InteractionHand hand) {
		this.id = id;
		this.hand = hand;
	}

	@Override
	public boolean handle(Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			PacketBarrierMethods.handlePacketPlayMatterScannerSound(id, hand);
		});
		return true;
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeUUID(id);
		buf.writeEnum(hand);
	}

	public static  PacketPlayMatterScannerSound decode(FriendlyByteBuf buf) {
		return new PacketPlayMatterScannerSound(buf.readUUID(), buf.readEnum(InteractionHand.class));
	}

}
