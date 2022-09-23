package matteroverdrive.core.packet.type.clientbound.android;

import matteroverdrive.core.packet.type.AbstractOverdrivePacket;
import matteroverdrive.core.packet.type.clientbound.PacketBarrierMethods;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketAndroidTurningTimeSync extends AbstractOverdrivePacket<PacketAndroidTurningTimeSync> {

	private final int time;

	public PacketAndroidTurningTimeSync(int time) {
		this.time = time;
	}

	@Override
	public void encode(FriendlyByteBuf outBuffer) {
		outBuffer.writeInt(time);
	}

	@Override
	public boolean handle(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			PacketBarrierMethods.handlePacketAndroidTurningTimeSync(time);
		});
		return true;
	}

	public static PacketAndroidTurningTimeSync decode(FriendlyByteBuf inBuffer) {
		return new PacketAndroidTurningTimeSync(inBuffer.readInt());
	}

}
