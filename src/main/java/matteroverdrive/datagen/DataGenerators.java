package matteroverdrive.datagen;

import matteroverdrive.References;
import matteroverdrive.datagen.client.OverdriveBlockModelsProvider;
import matteroverdrive.datagen.client.OverdriveBlockStateProvider;
import matteroverdrive.datagen.client.OverdriveItemModelsProvider;
import matteroverdrive.datagen.client.OverdriveLangKeyProvider;
import matteroverdrive.datagen.server.OverdriveBlockTagsProvider;
import matteroverdrive.datagen.server.OverdriveItemTagsProvider;
import matteroverdrive.datagen.server.OverdriveLootTablesProvider;
import matteroverdrive.datagen.server.OverdriveMatterValueGenerator;
import matteroverdrive.datagen.server.OverdriveRecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.data.event.GatherDataEvent;

@Mod.EventBusSubscriber(modid = References.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {

		DataGenerator generator = event.getGenerator();
		if (event.includeServer()) {
			OverdriveBlockTagsProvider blockProvider = new OverdriveBlockTagsProvider(generator, event.getExistingFileHelper());
			generator.addProvider(true, blockProvider);
			generator.addProvider(true, new OverdriveItemTagsProvider(generator, blockProvider, event.getExistingFileHelper()));
			generator.addProvider(true, new OverdriveLootTablesProvider(generator));
			generator.addProvider(true, new OverdriveMatterValueGenerator(generator));
			generator.addProvider(true, new OverdriveRecipeProvider(generator));
		}
		if (event.includeClient()) {
			generator.addProvider(true, new OverdriveBlockStateProvider(generator, event.getExistingFileHelper()));
			generator.addProvider(true, new OverdriveBlockModelsProvider(generator, event.getExistingFileHelper()));
			generator.addProvider(true, new OverdriveItemModelsProvider(generator, event.getExistingFileHelper()));
			generator.addProvider(true, new OverdriveLangKeyProvider(generator, "en_us"));
		}
	}

}
