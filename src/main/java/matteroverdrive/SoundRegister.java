package matteroverdrive;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundRegister {

	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS,
			References.ID);

	public static final RegistryObject<SoundEvent> SOUND_CRATEOPEN = SOUNDS.register("crateopen",
			() -> new SoundEvent(new ResourceLocation(References.ID + ":crateopen")));
	public static final RegistryObject<SoundEvent> SOUND_CRATECLOSE = SOUNDS.register("crateclose",
			() -> new SoundEvent(new ResourceLocation(References.ID + ":crateclose")));
	public static final RegistryObject<SoundEvent> SOUND_BUTTONEXPAND = SOUNDS.register("buttonexpand",
			() -> new SoundEvent(new ResourceLocation(References.ID + ":buttonexpand")));
	public static final RegistryObject<SoundEvent> SOUND_BUTTONSOFT0 = SOUNDS.register("buttonsoft0",
			() -> new SoundEvent(new ResourceLocation(References.ID + ":buttonsoft0")));
	public static final RegistryObject<SoundEvent> SOUND_BUTTONSOFT1 = SOUNDS.register("buttonsoft1",
			() -> new SoundEvent(new ResourceLocation(References.ID + ":buttonsoft1")));

}
