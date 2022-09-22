package matteroverdrive.core.android.packet.c2s;

import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.network.INetworkPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketAndroidPerkToggle {

  private final String perk;

  public PacketAndroidPerkToggle(String perk) {
    this.perk = perk;
  }

  public static PacketAndroidPerkToggle.Handler HANDLER = new PacketAndroidPerkToggle.Handler();

  private static class Handler implements INetworkPacketHandler<PacketAndroidPerkToggle> {
    public void encode(PacketAndroidPerkToggle msg, FriendlyByteBuf outBuffer) {
      outBuffer.writeUtf(msg.perk);
    }

    public PacketAndroidPerkToggle decode(FriendlyByteBuf inBuffer) {
      return new PacketAndroidPerkToggle(inBuffer.readUtf());
    }

    public void handle(PacketAndroidPerkToggle msg, Supplier<NetworkEvent.Context> ctx) {
      ctx.get().enqueueWork(() -> {
        ctx.get().getSender().getCapability(MatterOverdriveCapabilities.ANDROID_DATA).ifPresent(iAndroid -> {
          iAndroid.getPerkManager().togglePerk(msg.perk);
          iAndroid.requestUpdate();
        });
      });
      ctx.get().setPacketHandled(true);
    }
  }
}
