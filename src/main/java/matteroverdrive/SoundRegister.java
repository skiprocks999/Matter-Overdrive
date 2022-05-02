package matteroverdrive;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundRegister {

	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS,
			References.ID);

	public static final RegistryObject<SoundEvent> SOUND_CRATE_OPEN = sound("crate_open");
	public static final RegistryObject<SoundEvent> SOUND_CRATE_CLOSE = sound("crate_close");
	public static final RegistryObject<SoundEvent> SOUND_DECOMPOSER = sound("matter_decomposer");
	public static final RegistryObject<SoundEvent> SOUND_MACHINE = sound("machine");
	public static final RegistryObject<SoundEvent> SOUND_MACHINE_ELECTRIC = sound("machine_electric");

	public static final RegistryObject<SoundEvent> SOUND_BUTTON_EXPAND = sound("button_expand");
	public static final RegistryObject<SoundEvent> SOUND_BUTTON_SOFT0 = sound("button_soft0");
	public static final RegistryObject<SoundEvent> SOUND_BUTTON_SOFT1 = sound("button_soft1");
	public static final RegistryObject<SoundEvent> SOUND_BUTTON_LOUD3 = sound("button_loud3");

	private static RegistryObject<SoundEvent> sound(String name) {
		return SOUNDS.register(name, () -> new SoundEvent(new ResourceLocation(References.ID + ":" + name)));
	}

}
