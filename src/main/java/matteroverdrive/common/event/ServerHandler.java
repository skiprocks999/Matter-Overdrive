package matteroverdrive.common.event;

import matteroverdrive.References;
import matteroverdrive.core.matter.MatterRegister;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = References.ID, bus = Bus.FORGE)
public class ServerHandler {

	@SubscribeEvent
	public static void reloadListeners(AddReloadListenerEvent event) {
		MatterRegister.INSTANCE = new MatterRegister();
		event.addListener(MatterRegister.INSTANCE);
	}
	
}
