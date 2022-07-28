package matteroverdrive.core.datagen;

import matteroverdrive.References;
import matteroverdrive.core.datagen.client.OverdriveBlockModelsProvider;
import matteroverdrive.core.datagen.client.OverdriveBlockStateProvider;
import matteroverdrive.core.datagen.client.OverdriveLangKeyProvider;
import matteroverdrive.core.datagen.client.OverdriveItemModelsProvider;
import matteroverdrive.core.datagen.server.LootTablesProvider;
import matteroverdrive.core.datagen.server.MatterValueGenerator;
import matteroverdrive.core.datagen.server.MinableTagsProvider;
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
			generator.addProvider(true, new MinableTagsProvider(generator, event.getExistingFileHelper()));
			generator.addProvider(true, new LootTablesProvider(generator));
			generator.addProvider(true, new MatterValueGenerator(generator));
		}
		if (event.includeClient()) {
			generator.addProvider(true, new OverdriveBlockStateProvider(generator, event.getExistingFileHelper()));
			generator.addProvider(true, new OverdriveBlockModelsProvider(generator, event.getExistingFileHelper()));
			generator.addProvider(true, new OverdriveItemModelsProvider(generator, event.getExistingFileHelper()));
			generator.addProvider(true, new OverdriveLangKeyProvider(generator, "en_us"));
		}
	}

}
