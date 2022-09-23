package matteroverdrive.core.packet.type.clientbound.android;

import matteroverdrive.core.packet.type.AbstractOverdrivePacket;
import matteroverdrive.core.packet.type.clientbound.PacketBarrierMethods;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketAndroidSyncAll extends AbstractOverdrivePacket<PacketAndroidSyncAll> {

	private final CompoundTag compoundTag;

	public PacketAndroidSyncAll(CompoundTag compoundTag) {
		this.compoundTag = compoundTag;
	}

	@Override
	public void encode(FriendlyByteBuf outBuffer) {
		outBuffer.writeNbt(compoundTag);
	}

	@Override
	public boolean handle(PacketAndroidSyncAll pkt, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			PacketBarrierMethods.handlePacketAndroidSyncAll(pkt.compoundTag);
		});
		return true;
	}
	
	public static PacketAndroidSyncAll decode(FriendlyByteBuf inBuffer) {
		return new PacketAndroidSyncAll(inBuffer.readNbt());
	}

}
