package matteroverdrive.common.event;

import matteroverdrive.core.event.RegisterMatterGeneratorsEvent;
import matteroverdrive.core.matter.DefaultGeneratorConsumers;
import matteroverdrive.core.matter.MatterRegister;
import net.minecraftforge.common.MinecraftForge;

public final class ServerEventPostManager {

	public ServerEventPostManager() {}
	
	//We only want this method being called by the ServerEventHandler
	protected void postRegisterMatterGeneratorsEvent() {
		DefaultGeneratorConsumers.init();
		RegisterMatterGeneratorsEvent event = new RegisterMatterGeneratorsEvent();
		MinecraftForge.EVENT_BUS.post(event);
		MatterRegister.INSTANCE.setGeneratorMap(event.getGenerators());
	}
	
}
