package matteroverdrive.core.packet.type;

import java.util.UUID;
import java.util.function.Supplier;

import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.entity_data.CapabilityEntityData;
import matteroverdrive.core.capability.types.entity_data.ICapabilityEntityData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
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
			ClientLevel world = Minecraft.getInstance().level;
			if (world != null) {
				Player player = Minecraft.getInstance().player;
				if(player.getUUID().equals(message.id) && player.getCapability(MatterOverdriveCapabilities.ENTITY_DATA).isPresent()) {
					LazyOptional<ICapabilityEntityData> lazy = player.getCapability(MatterOverdriveCapabilities.ENTITY_DATA).cast();
					CapabilityEntityData cap = (CapabilityEntityData) lazy.resolve().get();
					cap.copyFromOther(message.clientCapability);
				}
			}
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
