package matteroverdrive.core.packet.type.clientbound.misc;

import matteroverdrive.core.packet.type.AbstractOverdrivePacket;
import matteroverdrive.core.packet.type.clientbound.PacketBarrierMethods;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketClientUpdateMNScreen extends AbstractOverdrivePacket<PacketClientUpdateMNScreen> {
	BlockPos pos;

	public PacketClientUpdateMNScreen(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public void encode(FriendlyByteBuf outBuffer) {
		outBuffer.writeBlockPos(pos);
	}

	@Override
	public boolean handle(Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		ctx.enqueueWork(() -> PacketBarrierMethods.handlePacketUpdateMNScreen(pos));
		return true;
	}

	public static PacketClientUpdateMNScreen decode(FriendlyByteBuf buf) {
		return new PacketClientUpdateMNScreen(buf.readBlockPos());
	}

}
