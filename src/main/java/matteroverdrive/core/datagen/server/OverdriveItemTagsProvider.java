package matteroverdrive.core.datagen.server;

import matteroverdrive.References;
import matteroverdrive.common.item.type.TypeIsolinearCircuit;
import matteroverdrive.registry.ItemRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;

public class OverdriveItemTagsProvider extends ItemTagsProvider {
	
	public OverdriveItemTagsProvider(DataGenerator generator, BlockTagsProvider provider, ExistingFileHelper existingFileHelper) {
		super(generator, provider, References.ID, existingFileHelper);
	}
	
	@Override
	protected void addTags() {
		tag(forgeTag("circuits/basic")).add(ItemRegistry.ITEM_ISOLINEAR_CIRCUITS.get(TypeIsolinearCircuit.TIER1).get()).replace(false);
		tag(forgeTag("circuits/advanced")).add(ItemRegistry.ITEM_ISOLINEAR_CIRCUITS.get(TypeIsolinearCircuit.TIER2).get()).replace(false);
		tag(forgeTag("circuits/elite")).add(ItemRegistry.ITEM_ISOLINEAR_CIRCUITS.get(TypeIsolinearCircuit.TIER3).get()).replace(false);
		tag(forgeTag("circuits/ultimate")).add(ItemRegistry.ITEM_ISOLINEAR_CIRCUITS.get(TypeIsolinearCircuit.TIER4).get()).replace(false);
		tag(forgeTag("raw_food")).add(Items.APPLE, Items.PORKCHOP, Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.BEEF,
				Items.MELON_SLICE, Items.CHICKEN, Items.ROTTEN_FLESH, Items.SPIDER_EYE, Items.POISONOUS_POTATO, Items.RABBIT, Items.MUTTON,
				Items.SWEET_BERRIES, Items.GLOW_BERRIES, Items.HONEY_BOTTLE).replace(false);
		tag(forgeTag("amethyst_buds")).add(Items.SMALL_AMETHYST_BUD, Items.MEDIUM_AMETHYST_BUD, Items.LARGE_AMETHYST_BUD, Items.AMETHYST_CLUSTER).replace(false);
		tag(forgeTag("weather_copper_blocks")).add(Items.EXPOSED_COPPER, Items.WEATHERED_COPPER, Items.OXIDIZED_COPPER).replace(false);
	}
	
	private static TagKey<Item> forgeTag(String name){
		return ItemTags.create(new ResourceLocation("forge", name));
	}

}
