package matteroverdrive.core.packet.type.clientbound.misc;

import java.util.function.Supplier;

import matteroverdrive.core.packet.type.AbstractOverdrivePacket;
import matteroverdrive.core.packet.type.clientbound.PacketBarrierMethods;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class PacketClientMNData extends AbstractOverdrivePacket<PacketClientMNData> {

	private final CompoundTag data;
	private final BlockPos monitorPos;

	public PacketClientMNData(CompoundTag data, BlockPos pos) {
		this.data = data;
		monitorPos = pos;
	}

	@Override
	public boolean handle(Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			PacketBarrierMethods.handlePacketClientMNData(data, monitorPos);
		});
		return true;
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeNbt(data);
		buf.writeBlockPos(monitorPos);
	}

	public static  PacketClientMNData decode(FriendlyByteBuf buf) {
		return new PacketClientMNData(buf.readNbt(), buf.readBlockPos());
	}

}
