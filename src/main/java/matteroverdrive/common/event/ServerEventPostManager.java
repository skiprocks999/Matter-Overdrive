package matteroverdrive.common.event;

import matteroverdrive.core.config.MatterOverdriveConfig;
import matteroverdrive.core.event.RegisterMatterGeneratorsEvent;
import matteroverdrive.core.event.RegisterSpecialGeneratorsEvent;
import matteroverdrive.core.matter.MatterRegister;
import matteroverdrive.core.matter.generator.DefaultMatterGenerators;
import net.minecraftforge.common.MinecraftForge;

public final class ServerEventPostManager {

	public ServerEventPostManager() {}
	
	//We only want this method being called by the ServerEventHandler
	protected void postRegisterMatterGeneratorsEvent() {
		DefaultMatterGenerators.init();
		boolean postDefaultEvent = MatterOverdriveConfig.POST_DEFAULT_GENERATOR_EVENT.get();
		if(postDefaultEvent) {
			RegisterMatterGeneratorsEvent event = new RegisterMatterGeneratorsEvent();
			MinecraftForge.EVENT_BUS.post(event);
			MatterRegister.INSTANCE.setGeneratorMap(event.getGenerators());
		}
		if(!postDefaultEvent && MatterOverdriveConfig.POST_SPECIAL_GENERATOR_EVENT.get()) {
			RegisterSpecialGeneratorsEvent event = new RegisterSpecialGeneratorsEvent();
			MinecraftForge.EVENT_BUS.post(event);
			MatterRegister.INSTANCE.setGeneratorMap(event.getGenerators());
		}
		
	}
	
}
