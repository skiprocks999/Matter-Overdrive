package matteroverdrive.core.datagen;

import matteroverdrive.References;
import matteroverdrive.core.datagen.client.blockstates.OverdriveBlockStateProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraft.data.DataGenerator;

@Mod.EventBusSubscriber(modid = References.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		
		DataGenerator generator = event.getGenerator();
		
		if(event.includeServer()) {
			
		}
		if(event.includeClient()) {
			generator.addProvider(new OverdriveBlockStateProvider(generator, event.getExistingFileHelper()));
		}
	}
	
}
