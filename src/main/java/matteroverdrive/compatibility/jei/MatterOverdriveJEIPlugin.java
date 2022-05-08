package matteroverdrive.compatibility.jei;

import matteroverdrive.References;
import matteroverdrive.client.screen.ScreenCharger;
import matteroverdrive.client.screen.ScreenInscriber;
import matteroverdrive.client.screen.ScreenMatterDecomposer;
import matteroverdrive.client.screen.ScreenMatterRecycler;
import matteroverdrive.client.screen.ScreenMicrowave;
import matteroverdrive.client.screen.ScreenSolarPanel;
import matteroverdrive.compatibility.jei.screen_handlers.ScreenHandlerCharger;
import matteroverdrive.compatibility.jei.screen_handlers.ScreenHandlerInscriber;
import matteroverdrive.compatibility.jei.screen_handlers.ScreenHandlerMatterDecomposer;
import matteroverdrive.compatibility.jei.screen_handlers.ScreenHandlerMatterRecycler;
import matteroverdrive.compatibility.jei.screen_handlers.ScreenHandlerMicrowave;
import matteroverdrive.compatibility.jei.screen_handlers.ScreenHandlerSolarPanel;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class MatterOverdriveJEIPlugin implements IModPlugin {

	private static final ResourceLocation ID = new ResourceLocation(References.ID, "jei");
	
	@Override
	public ResourceLocation getPluginUid() {
		return ID;
	}
	
	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registry) {
		registry.addGuiContainerHandler(ScreenCharger.class, new ScreenHandlerCharger());
		registry.addGuiContainerHandler(ScreenInscriber.class, new ScreenHandlerInscriber());
		registry.addGuiContainerHandler(ScreenMatterDecomposer.class, new ScreenHandlerMatterDecomposer());
		registry.addGuiContainerHandler(ScreenMatterRecycler.class, new ScreenHandlerMatterRecycler());
		registry.addGuiContainerHandler(ScreenMicrowave.class, new ScreenHandlerMicrowave());
		registry.addGuiContainerHandler(ScreenSolarPanel.class, new ScreenHandlerSolarPanel());
	}

}
