package matteroverdrive.client.keys;

import com.mojang.blaze3d.platform.InputConstants;

import matteroverdrive.References;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = References.ID, bus = Bus.MOD, value = Dist.CLIENT)
public class KeyBinds {

	// Category
	private static final String CATEGORY_MAIN = "keycategory.matteroverdrive.main";

	// KEYS
	public static final KeyMapping TOGGLE_MATTER_SCANNER = registerKey("togglematterscanner", CATEGORY_MAIN, InputConstants.KEY_O);

	private KeyBinds() {
	}

	private static KeyMapping registerKey(String name, String category, int keyCode) {
		return new KeyMapping("key." + References.ID + "." + name, keyCode, category);
	}
	
	@SubscribeEvent
	public static void registerKeys(RegisterKeyMappingsEvent event) {
		event.register(TOGGLE_MATTER_SCANNER);
	}

}
