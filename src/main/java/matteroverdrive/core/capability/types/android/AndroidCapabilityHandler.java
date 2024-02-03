package matteroverdrive.core.capability.types.android;

import matteroverdrive.References;
import matteroverdrive.core.android.api.ICapabilityAndroid;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

@Mod.EventBusSubscriber(modid = References.ID)
public class AndroidCapabilityHandler {

  @SubscribeEvent
  public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
    if (event.getObject() instanceof Player) {
      event.addCapability(new ResourceLocation(References.ID, "android_data"), new AndroidData());
      event.addCapability(new ResourceLocation(References.ID, "android_energy"), new AndroidEnergyProvider());
    }
  }

  @SubscribeEvent
  public static void onPlayerClone(PlayerEvent.Clone event) {
    event.getOriginal().getCapability(MatterOverdriveCapabilities.ANDROID_DATA).ifPresent(original -> {
      event.getEntity().getCapability(MatterOverdriveCapabilities.ANDROID_DATA).ifPresent(future -> {
        future.deserializeNBT(original.serializeNBT());
        future.requestUpdate();
      });
    });
    event.getOriginal().getCapability(ForgeCapabilities.ENERGY).ifPresent(original -> {
      event.getEntity().getCapability(ForgeCapabilities.ENERGY).ifPresent(future -> {
        if (original instanceof AndroidEnergy && future instanceof AndroidEnergy) {
          ((AndroidEnergy) future).setEnergy(original.getEnergyStored());
          if (event.getEntity() instanceof ServerPlayer serverPlayer)
            AndroidEnergy.syncEnergy(serverPlayer);
        }
      });
    });
  }

  @SubscribeEvent
  public static void onServerTick(TickEvent.ServerTickEvent event) {
    if (event.phase == TickEvent.Phase.START) return;
    for (Player playerEntity : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
      playerEntity.getCapability(MatterOverdriveCapabilities.ANDROID_DATA).ifPresent(iAndroid -> iAndroid.tickServer(playerEntity));
    }
  }

  @SubscribeEvent
  public static void onClientTick(TickEvent.ClientTickEvent event) {
    if (event.phase == TickEvent.Phase.START) return;

    LocalPlayer player = Minecraft.getInstance().player;

    if (player == null) { return; }

    if (!player.getCapability(MatterOverdriveCapabilities.ANDROID_DATA).isPresent()) {
      return;
    }

    player.getCapability(MatterOverdriveCapabilities.ANDROID_DATA).ifPresent(iAndroid -> {
//      iAndroid.tickClient(player);
    });

//    if (Minecraft.getInstance().player != null) {
//      Minecraft.getInstance().player.getCapability(MatterOverdriveCapabilities.ANDROID_DATA).ifPresent(iAndroid -> iAndroid.tickClient(Minecraft.getInstance().player));
//    }
  }

  @SubscribeEvent
  public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
    event.getEntity().getCapability(MatterOverdriveCapabilities.ANDROID_DATA).ifPresent(ICapabilityAndroid::requestUpdate);
    if (event.getEntity() instanceof ServerPlayer serverPlayer)
      AndroidEnergy.syncEnergy(serverPlayer);
  }

}
