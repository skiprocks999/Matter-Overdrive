package matteroverdrive.core.android.packet.c2s;

import matteroverdrive.core.android.api.perk.AndroidPerkManager;
import matteroverdrive.core.android.api.perk.IAndroidPerk;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.network.INetworkPacketHandler;
import matteroverdrive.registry.SoundRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketAndroidPerkAttemptBuy {

  public String perk;


  public PacketAndroidPerkAttemptBuy() {
  }

  public PacketAndroidPerkAttemptBuy(String perk) {
    this.perk = perk;
  }

  public static PacketAndroidPerkAttemptBuy.Handler HANDLER = new PacketAndroidPerkAttemptBuy.Handler();

  private static class Handler implements INetworkPacketHandler<PacketAndroidPerkAttemptBuy> {
    public void encode(PacketAndroidPerkAttemptBuy msg, FriendlyByteBuf outBuffer) {
      outBuffer.writeUtf(msg.perk);
    }

    public PacketAndroidPerkAttemptBuy decode(FriendlyByteBuf inBuffer) {
      return new PacketAndroidPerkAttemptBuy(inBuffer.readUtf());
    }

    public void handle(PacketAndroidPerkAttemptBuy msg, Supplier<NetworkEvent.Context> ctx) {
      ctx.get().enqueueWork(() -> {
        ServerPlayer entity = ctx.get().getSender();
        IAndroidPerk androidPerk = IAndroidPerk.PERKS.get(msg.perk);
        entity.getCapability(MatterOverdriveCapabilities.ANDROID_DATA).ifPresent(iAndroid -> {
          AndroidPerkManager perkManager = iAndroid.getPerkManager();
          if (!perkManager.hasPerk(androidPerk) || perkManager.getOwned().get(androidPerk.getName()) < androidPerk.getMaxLevel()){
            int requiredXP = androidPerk.getRequiredXP(iAndroid, perkManager.hasPerk(androidPerk) ? perkManager.getOwned().get(androidPerk.getName()) + 1 : 1);
            if (entity.experienceLevel >= requiredXP){
              entity.giveExperienceLevels(-requiredXP);
              perkManager.buyPerk(androidPerk);
              entity.level.playSound(entity, entity.blockPosition(), SoundRegistry.PERK_UNLOCK.get(), SoundSource.PLAYERS, 0.5f, 1f);
              iAndroid.requestUpdate();
            }
          }
        });
      });
      ctx.get().setPacketHandled(true);
    }
  }

}