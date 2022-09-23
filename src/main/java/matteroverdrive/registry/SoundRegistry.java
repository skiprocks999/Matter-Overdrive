package matteroverdrive.registry;

import matteroverdrive.References;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundRegistry {

	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS,
			References.ID);

	public static final RegistryObject<SoundEvent> SOUND_CRATE_OPEN = sound("crate_open");
	public static final RegistryObject<SoundEvent> SOUND_CRATE_CLOSE = sound("crate_close");
	public static final RegistryObject<SoundEvent> SOUND_DECOMPOSER = sound("matter_decomposer");
	public static final RegistryObject<SoundEvent> SOUND_MACHINE = sound("machine");
	public static final RegistryObject<SoundEvent> SOUND_MACHINE_ELECTRIC = sound("machine_electric");
	public static final RegistryObject<SoundEvent> SOUND_TRANSPORTER = sound("transporter");
	public static final RegistryObject<SoundEvent> SOUND_TRANSPORTER_ARRIVE = sound("transporter_arrive");
	public static final RegistryObject<SoundEvent> SOUND_MATTER_REPLICATOR = sound("matter_replicator");
	public static final RegistryObject<SoundEvent> SOUND_MATTER_ANALYZER = sound("matter_analyzer");

	public static final RegistryObject<SoundEvent> SOUND_BUTTON_EXPAND = sound("button_expand");
	public static final RegistryObject<SoundEvent> SOUND_BUTTON_SOFT0 = sound("button_soft0");
	public static final RegistryObject<SoundEvent> SOUND_BUTTON_SOFT1 = sound("button_soft1");
	public static final RegistryObject<SoundEvent> SOUND_BUTTON_LOUD3 = sound("button_loud3");

	public static final RegistryObject<SoundEvent> SOUND_MATTER_SCANNER_RUNNING = sound("matter_scanner_running");
	public static final RegistryObject<SoundEvent> SOUND_MATTER_SCANNER_BEEP = sound("matter_scanner_beep");
	public static final RegistryObject<SoundEvent> SOUND_MATTER_SCANNER_FAIL = sound("matter_scanner_fail");
	public static final RegistryObject<SoundEvent> SOUND_MATTER_SCANNER_SUCCESS = sound("matter_scanner_success");

	public static final RegistryObject<SoundEvent> KUNAI_THUD = sound("kunai_thud");
	public static final RegistryObject<SoundEvent> ANDROID_TELEPORT = sound("android_teleport");
	public static final RegistryObject<SoundEvent> GLITCH = sound("gui.glitch");
	public static final RegistryObject<SoundEvent> PERK_UNLOCK = sound("perk_unlock");
	public static final RegistryObject<SoundEvent> NIGHT_VISION = sound("night_vision");

	private static RegistryObject<SoundEvent> sound(String name) {
		return SOUNDS.register(name, () -> new SoundEvent(new ResourceLocation(References.ID + ":" + name)));
	}

}
