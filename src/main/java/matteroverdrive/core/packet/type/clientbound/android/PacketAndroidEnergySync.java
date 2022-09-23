package matteroverdrive.core.packet.type.clientbound.android;

import matteroverdrive.core.packet.type.AbstractOverdrivePacket;
import matteroverdrive.core.packet.type.clientbound.PacketBarrierMethods;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketAndroidEnergySync extends AbstractOverdrivePacket<PacketAndroidEnergySync> {

	private final int energy;
	private final int maxEnergy;

	public PacketAndroidEnergySync(int energy, int maxEnergy) {
		this.energy = energy;
		this.maxEnergy = maxEnergy;
	}

	@Override
	public void encode(FriendlyByteBuf outBuffer) {
		outBuffer.writeInt(energy);
		outBuffer.writeInt(maxEnergy);
	}

	@Override
	public boolean handle(PacketAndroidEnergySync pkt, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			PacketBarrierMethods.handlePacketAndroidEnergySync(pkt.energy, pkt.maxEnergy);
		});
		return true;
	}
	
	public static PacketAndroidEnergySync decode(FriendlyByteBuf inBuffer) {
		return new PacketAndroidEnergySync(inBuffer.readInt(), inBuffer.readInt());
	}

}
