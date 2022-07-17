package matteroverdrive.core.packet.type.clientbound;

import java.util.function.Supplier;

import matteroverdrive.core.packet.PacketBarrierMethods;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class PacketClientMNData {

	private final CompoundTag data;
	private final BlockPos monitorPos;
	
	public PacketClientMNData(CompoundTag data, BlockPos pos) {
		this.data = data;
		monitorPos = pos;
	}
	
	public static void handle(PacketClientMNData message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			PacketBarrierMethods.handlePacketClientMNData(message.data, message.monitorPos);
		});
		ctx.setPacketHandled(true);
	}
	
	public static void encode(PacketClientMNData pkt, FriendlyByteBuf buf) {
		buf.writeNbt(pkt.data);
		buf.writeBlockPos(pkt.monitorPos);
	}
	
	public static PacketClientMNData decode(FriendlyByteBuf buf) {
		return new PacketClientMNData(buf.readNbt(), buf.readBlockPos());
	}
	
}
