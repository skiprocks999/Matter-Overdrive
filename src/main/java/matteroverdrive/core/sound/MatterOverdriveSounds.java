package matteroverdrive.core.sound;

import matteroverdrive.References;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MatterOverdriveSounds {
  public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, References.ID);

  public static final RegistryObject<SoundEvent> KUNAI_THUD = register("kunai_thud");
  public static final RegistryObject<SoundEvent> ANDROID_TELEPORT = register("android_teleport");
  public static final RegistryObject<SoundEvent> GLITCH = register("gui.glitch");
  public static final RegistryObject<SoundEvent> PERK_UNLOCK = register("perk_unlock");
  public static final RegistryObject<SoundEvent> NIGHT_VISION = register("night_vision");

  private static RegistryObject<SoundEvent> register(String key) {
    return SOUNDS.register(key, () -> new SoundEvent(new ResourceLocation(References.ID, key)));
  }

  public static void register(IEventBus eventBus) {
    SOUNDS.register(eventBus);
  }
}
