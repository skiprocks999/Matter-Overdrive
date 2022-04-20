package matteroverdrive;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundRegister {

	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS,
			References.ID);

	public static final RegistryObject<SoundEvent> SOUND_CRATE_OPEN = block("crate_open");
	public static final RegistryObject<SoundEvent> SOUND_CRATE_CLOSE = block("crate_close");
	
	public static final RegistryObject<SoundEvent> SOUND_BUTTON_EXPAND = button("button_expand");
	public static final RegistryObject<SoundEvent> SOUND_BUTTON_SOFT0 = button("button_soft0");
	public static final RegistryObject<SoundEvent> SOUND_BUTTON_SOFT1 = button("button_soft1");
	public static final RegistryObject<SoundEvent> SOUND_BUTTON_LOUD3 = button("button_loud3");
	
	
	private static RegistryObject<SoundEvent> button(String name) {
		return SOUNDS.register(name, () -> new SoundEvent(new ResourceLocation(References.ID + ":button/" + name)));
	}
	
	private static RegistryObject<SoundEvent> block(String name) {
		return SOUNDS.register(name, () -> new SoundEvent(new ResourceLocation(References.ID + ":block/" + name)));
	}
}
