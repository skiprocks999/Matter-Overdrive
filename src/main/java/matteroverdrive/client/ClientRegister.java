package matteroverdrive.client;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.client.screen.ScreenMatterDecomposer;
import matteroverdrive.client.screen.ScreenSolarPanel;
import matteroverdrive.client.screen.ScreenTritaniumCrate;
import net.minecraft.client.gui.screens.MenuScreens;

public class ClientRegister {

	public static void init() {

		MenuScreens.register(DeferredRegisters.MENU_TRITANIUM_CRATE.get(), ScreenTritaniumCrate::new);
		MenuScreens.register(DeferredRegisters.MENU_SOLAR_PANEL.get(), ScreenSolarPanel::new);
		MenuScreens.register(DeferredRegisters.MENU_MATTER_DECOMPOSER.get(), ScreenMatterDecomposer::new);

	}

}
