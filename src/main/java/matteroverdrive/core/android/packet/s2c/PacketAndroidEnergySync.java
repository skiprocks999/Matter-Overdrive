package matteroverdrive.core.android.packet.s2c;

import matteroverdrive.core.capability.types.android.AndroidEnergy;
import matteroverdrive.core.network.INetworkPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketAndroidEnergySync {

  private final int energy;
  private final int maxEnergy;

  public PacketAndroidEnergySync(int energy, int maxEnergy) {
    this.energy = energy;
    this.maxEnergy = maxEnergy;
  }

  public static PacketAndroidEnergySync.Handler HANDLER = new PacketAndroidEnergySync.Handler();

  private static class Handler implements INetworkPacketHandler<PacketAndroidEnergySync> {
    public void encode(PacketAndroidEnergySync msg, FriendlyByteBuf outBuffer) {
      outBuffer.writeInt(msg.energy);
      outBuffer.writeInt(msg.maxEnergy);
    }

    public PacketAndroidEnergySync decode(FriendlyByteBuf inBuffer) {
      return new PacketAndroidEnergySync(inBuffer.readInt(), inBuffer.readInt());
    }

    public void handle(PacketAndroidEnergySync msg, Supplier<NetworkEvent.Context> ctx) {
      ctx.get().enqueueWork(() -> {
        Minecraft.getInstance().player.getCapability(CapabilityEnergy.ENERGY).ifPresent(energyStorage -> {
          if (energyStorage instanceof AndroidEnergy) {
            ((AndroidEnergy) energyStorage).setEnergy(msg.energy);
          }
        });
      });
      ctx.get().setPacketHandled(true);
    }
  }
}
