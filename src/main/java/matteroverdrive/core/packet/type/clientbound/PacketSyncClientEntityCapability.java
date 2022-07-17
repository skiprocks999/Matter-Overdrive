package matteroverdrive.core.packet.type.clientbound;

import java.util.UUID;
import java.util.function.Supplier;

import matteroverdrive.core.capability.types.entity_data.CapabilityEntityData;
import matteroverdrive.core.packet.PacketBarrierMethods;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class PacketSyncClientEntityCapability {

	private final CapabilityEntityData clientCapability;
	private final UUID id;
	
	public PacketSyncClientEntityCapability(CapabilityEntityData cap, UUID id) {
		clientCapability = cap;
		this.id = id;
	}
	
	public static void handle(PacketSyncClientEntityCapability message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			PacketBarrierMethods.handlePacketSyncClientEntityCapability(message.id, message.clientCapability);
		});
		ctx.setPacketHandled(true);
	}
	
	public static void encode(PacketSyncClientEntityCapability pkt, FriendlyByteBuf buf) {
		pkt.clientCapability.writeToByteBuffer(buf);
		buf.writeUUID(pkt.id);
	}

	public static PacketSyncClientEntityCapability decode(FriendlyByteBuf buf) {
		CapabilityEntityData capability = new CapabilityEntityData();
		capability.readFromByteBuffer(buf);
		return new PacketSyncClientEntityCapability(capability, buf.readUUID());
	}
	
}
