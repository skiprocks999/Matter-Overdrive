package matteroverdrive.core.datagen;

import matteroverdrive.References;
import matteroverdrive.core.datagen.client.OverdriveBlockStateProvider;
import matteroverdrive.core.datagen.client.OverdriveLangKeyProvider;
import matteroverdrive.core.datagen.client.models.OverdriveItemModelsProvider;
import matteroverdrive.core.datagen.server.MinableTags;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraft.data.DataGenerator;

/**
 * For all BlockStates and Models Generated with this, know they must remain in the "generated" 
 * directory where they were saved
 * 
 * For all Lang Key and Minable Tags made, they must be copied over to the main folder
 * @author skip999
 *
 */
@Mod.EventBusSubscriber(modid = References.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		
		DataGenerator generator = event.getGenerator();
		
		if(event.includeServer()) {
			generator.addProvider(new MinableTags(generator, event.getExistingFileHelper()));
		}
		if(event.includeClient()) {
			generator.addProvider(new OverdriveBlockStateProvider(generator, event.getExistingFileHelper()));
			generator.addProvider(new OverdriveItemModelsProvider(generator, event.getExistingFileHelper()));
			generator.addProvider(new OverdriveLangKeyProvider(generator, "en_us"));
		}
	}
	
}
