package matteroverdrive.core.datagen;

import matteroverdrive.References;
import matteroverdrive.core.datagen.client.OverdriveBlockModelsProvider;
import matteroverdrive.core.datagen.client.OverdriveBlockStateProvider;
import matteroverdrive.core.datagen.client.OverdriveLangKeyProvider;
import matteroverdrive.core.datagen.client.OverdriveItemModelsProvider;
import matteroverdrive.core.datagen.server.OverdriveLootTablesProvider;
import matteroverdrive.core.datagen.server.OverdriveMatterValueGenerator;
import matteroverdrive.core.datagen.server.OverdriveBlockTagsProvider;
import matteroverdrive.core.datagen.server.OverdriveItemTagsProvider;
import matteroverdrive.core.datagen.server.OverdriveRecipeProvider;
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
