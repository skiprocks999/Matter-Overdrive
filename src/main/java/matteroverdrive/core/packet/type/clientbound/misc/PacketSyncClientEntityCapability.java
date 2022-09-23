package matteroverdrive.core.packet.type.clientbound.misc;

import java.util.UUID;
import java.util.function.Supplier;

import matteroverdrive.core.capability.types.entity_data.CapabilityEntityData;
import matteroverdrive.core.packet.type.AbstractOverdrivePacket;
import matteroverdrive.core.packet.type.clientbound.PacketBarrierMethods;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class PacketSyncClientEntityCapability extends AbstractOverdrivePacket<PacketSyncClientEntityCapability> {

	private final CapabilityEntityData clientCapability;
	private final UUID id;

	public PacketSyncClientEntityCapability(CapabilityEntityData cap, UUID id) {
		clientCapability = cap;
		this.id = id;
	}

	@Override
	public boolean handle(Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			PacketBarrierMethods.handlePacketSyncClientEntityCapability(id, clientCapability);
		});
		return true;
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		clientCapability.writeToByteBuffer(buf);
		buf.writeUUID(id);
	}

	public static  PacketSyncClientEntityCapability decode(FriendlyByteBuf buf) {
		CapabilityEntityData capability = new CapabilityEntityData();
		capability.readFromByteBuffer(buf);
		return new PacketSyncClientEntityCapability(capability, buf.readUUID());
	}

}
