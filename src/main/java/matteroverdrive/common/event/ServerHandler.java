package matteroverdrive.common.event;

import matteroverdrive.References;
import matteroverdrive.core.command.CommandGenerateMatterValues;
import matteroverdrive.core.matter.MatterRegister;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = References.ID, bus = Bus.FORGE)
public class ServerHandler {

	@SubscribeEvent
	public static void reloadListeners(AddReloadListenerEvent event) {
		event.addListener(MatterRegister.INSTANCE);
	}
	
	@SubscribeEvent
	public static void serverStartedHandler(ServerStartedEvent event) {
		MatterRegister.INSTANCE.generateTagValues();
	}
	
	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent event) {
		CommandGenerateMatterValues.register(event.getDispatcher());
	}
	
}
