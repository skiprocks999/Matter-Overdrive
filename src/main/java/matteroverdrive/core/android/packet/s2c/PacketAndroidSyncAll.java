package matteroverdrive.core.android.packet.s2c;

import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.network.INetworkPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketAndroidSyncAll {

  private final CompoundTag compoundTag;

  public PacketAndroidSyncAll(CompoundTag compoundTag) {
    this.compoundTag = compoundTag;
  }

  public static PacketAndroidSyncAll.Handler HANDLER = new PacketAndroidSyncAll.Handler();

  private static class Handler implements INetworkPacketHandler<PacketAndroidSyncAll> {
    public void encode(PacketAndroidSyncAll msg, FriendlyByteBuf outBuffer) {
      outBuffer.writeNbt(msg.compoundTag);
    }

    public PacketAndroidSyncAll decode(FriendlyByteBuf inBuffer) {
      return new PacketAndroidSyncAll(inBuffer.readNbt());
    }

    public void handle(PacketAndroidSyncAll msg, Supplier<NetworkEvent.Context> ctx) {
      ctx.get().enqueueWork(() -> {
        Minecraft.getInstance().player.getCapability(MatterOverdriveCapabilities.ANDROID_DATA).ifPresent(iAndroid -> iAndroid.deserializeNBT(msg.compoundTag));
      });
      ctx.get().setPacketHandled(true);
    }
  }
}
