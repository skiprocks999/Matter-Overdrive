package matteroverdrive.client;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.client.screen.ScreenSolarPanel;
import matteroverdrive.client.screen.ScreenTritaniumCrate;
import net.minecraft.client.gui.screens.MenuScreens;

public class ClientRegister {

	public static void init() {

		MenuScreens.register(DeferredRegisters.MENU_TRITANIUMCRATE.get(), ScreenTritaniumCrate::new);
		MenuScreens.register(DeferredRegisters.MENU_SOLARPANEL.get(), ScreenSolarPanel::new);

	}

}
