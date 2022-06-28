package matteroverdrive.client.keys;

import com.mojang.blaze3d.platform.InputConstants;

import matteroverdrive.References;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;

public class KeyBinds {

	// Category
	private static final String CATEGORY_MAIN = "keycategory.matteroverdrive.main";

	// KEYS
	public static KeyMapping toggleMatterScanner;

	private KeyBinds() {
	}

	public static void registerKeys() {
		toggleMatterScanner = registerKey("togglematterscanner", CATEGORY_MAIN, InputConstants.KEY_O);
	}

	private static KeyMapping registerKey(String name, String category, int keyCode) {
		final var key = new KeyMapping("key." + References.ID + "." + name, keyCode, category);
		ClientRegistry.registerKeyBinding(key);
		return key;
	}

}
