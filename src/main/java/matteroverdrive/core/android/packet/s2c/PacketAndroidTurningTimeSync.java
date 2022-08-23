package matteroverdrive.core.android.packet.s2c;

import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.network.INetworkPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketAndroidTurningTimeSync {

  private final int time;

  public PacketAndroidTurningTimeSync(int time) {
    this.time = time;
  }

  public static Handler HANDLER = new Handler();

  private static class Handler implements INetworkPacketHandler<PacketAndroidTurningTimeSync> {
    public void encode(PacketAndroidTurningTimeSync msg, FriendlyByteBuf outBuffer) {
      outBuffer.writeInt(msg.time);
    }

    public PacketAndroidTurningTimeSync decode(FriendlyByteBuf inBuffer) {
      return new PacketAndroidTurningTimeSync(inBuffer.readInt());
    }

    public void handle(PacketAndroidTurningTimeSync msg, Supplier<NetworkEvent.Context> ctx) {
      ctx.get().enqueueWork(() -> {
        Minecraft.getInstance().player.getCapability(MatterOverdriveCapabilities.ANDROID_DATA).ifPresent(iAndroid -> iAndroid.setTurningTime(msg.time));
      });
      ctx.get().setPacketHandled(true);
    }
  }
}
