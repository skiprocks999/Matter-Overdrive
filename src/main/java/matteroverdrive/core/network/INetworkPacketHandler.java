package matteroverdrive.core.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface INetworkPacketHandler<T> {

	void encode(FriendlyByteBuf outBuffer);

	boolean handle(Supplier<NetworkEvent.Context> ctx);
}
