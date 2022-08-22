package matteroverdrive.core.datagen.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.GsonHelper;

public class MatterValueGenerator implements DataProvider {

	private static final String DATA_LOC = "data/matteroverdrive/matter/values.json";
	private DataGenerator gen;

	public MatterValueGenerator(DataGenerator gen) {
		this.gen = gen;
	}

	@Override
	public void run(CachedOutput cache) throws IOException {
		JsonObject json = new JsonObject();
		addValues(json);
		Path path = gen.getOutputFolder().resolve(DATA_LOC);
		try {
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			HashingOutputStream hash = new HashingOutputStream(Hashing.sha1(), byteArray);
			Writer writer = new OutputStreamWriter(hash, StandardCharsets.UTF_8);
			JsonWriter jsonWriter = new JsonWriter(writer);
			jsonWriter.setSerializeNulls(false);
			jsonWriter.setIndent("  ");
			GsonHelper.writeValue(jsonWriter, json, KEY_COMPARATOR);
			jsonWriter.close();
			cache.writeIfNeeded(path, byteArray.toByteArray(), hash.hash());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void addValues(JsonObject json) {
		json.addProperty("minecraft:basalt", 4);
		json.addProperty("minecraft:bamboo", 1);
		json.addProperty("minecraft:bedrock", 1024);
		json.addProperty("minecraft:bell", 10);
		json.addProperty("minecraft:bee_nest", 10);
		json.addProperty("minecraft:big_dripleaf", 2);
		json.addProperty("minecraft:blackstone", 4);
		json.addProperty("minecraft:brown_mushroom_block", 1);
		json.addProperty("minecraft:budding_amethyst", 1);
		json.addProperty("minecraft:cactus", 4);
		json.addProperty("minecraft:calcite", 1);
		json.addProperty("minecraft:carved_pumpkin", 2);
		json.addProperty("minecraft:chorus_plant", 1);
		json.addProperty("minecraft:chorus_flower", 1);
		json.addProperty("minecraft:chorus_fruit", 1);
		json.addProperty("minecraft:clay_ball", 1);
		json.addProperty("#" + ItemTags.COAL_ORES.location().toString(), 16);
		json.addProperty("minecraft:cocoa_beans", 1);
		json.addProperty("minecraft:cobweb", 1);
		json.addProperty("#" + ItemTags.COPPER_ORES.location().toString(), 16);
		json.addProperty("minecraft:crimson_fungus", 1);
		json.addProperty("minecraft:crimson_nylium", 1);
		json.addProperty("minecraft:crimson_roots", 1);
		json.addProperty("minecraft:crying_obsidian", 16);
		json.addProperty("minecraft:dead_bush", 1);
		json.addProperty("#" + ItemTags.DIAMOND_ORES.location().toString(), 512);
		json.addProperty("minecraft:diamond_horse_armor", 1280);
		json.addProperty("#" + ItemTags.DIRT.location().toString(), 1);
		json.addProperty("minecraft:dirt_path", 1);
		json.addProperty("#" + ItemTags.EMERALD_ORES.location().toString(), 512);
		json.addProperty("minecraft:enchanted_golden_apple", 5000);
		json.addProperty("minecraft:end_portal_frame", 1024);
		json.addProperty("minecraft:elytra", 100);
		json.addProperty("minecraft:farmland", 1);
		json.addProperty("minecraft:fern", 2);
		json.addProperty("minecraft:flint", 1);
		json.addProperty("#" + ItemTags.FLOWERS.location().toString(), 1);
		json.addProperty("minecraft:glow_lichen", 1);
		json.addProperty("#" + ItemTags.GOLD_ORES.location().toString(), 84);
		json.addProperty("minecraft:grass", 1);
		json.addProperty("minecraft:ghast_tear", 8);
		json.addProperty("minecraft:gilded_blackstone", 4);
		json.addProperty("minecraft:glow_ink_sac", 1);
		json.addProperty("minecraft:gold_horse_armor", 210);
		json.addProperty("minecraft:hanging_roots", 1);
		json.addProperty("minecraft:heart_of_the_sea", 1000);
		json.addProperty("minecraft:honeycomb", 1);
		json.addProperty("minecraft:ice", 1);
		json.addProperty("minecraft:iron_horse_armor", 160);
		json.addProperty("minecraft:ink_sac", 1);
		json.addProperty("#" + ItemTags.IRON_ORES.location().toString(), 64);
		json.addProperty("minecraft:kelp", 1);
		json.addProperty("#" + ItemTags.LAPIS_ORES.location().toString(), 16);
		json.addProperty("minecraft:large_fern", 1);
		json.addProperty("minecraft:lava_bucket", 250);
		json.addProperty("#" + ItemTags.LEAVES.location().toString(), 1);
		json.addProperty("minecraft:lily_pad", 1);
		json.addProperty("#" + ItemTags.LOGS.location().toString(), 16);
		json.addProperty("minecraft:milk_bucket", 193);
		json.addProperty("#" + ItemTags.MUSIC_DISCS.location().toString(), 1);
		json.addProperty("minecraft:mushroom_stem", 1);
		json.addProperty("minecraft:nautilus_shell", 4);
		json.addProperty("minecraft:nether_sprouts", 1);
		json.addProperty("minecraft:name_tag", 10);
		json.addProperty("minecraft:pointed_dripstone", 1);
		json.addProperty("minecraft:powder_snow_bucket", 193);
		json.addProperty("minecraft:pumpkin", 2);
		json.addProperty("minecraft:rabbit_hide", 1);
		json.addProperty("minecraft:rabbit_foot", 10);
		json.addProperty("#" + ItemTags.REDSTONE_ORES.location().toString(), 16);
		json.addProperty("minecraft:red_mushroom_block", 1);
		json.addProperty("minecraft:phantom_membrane", 10);
		json.addProperty("#" + ItemTags.SAND.location().toString(), 2);
		json.addProperty("#" + ItemTags.SAPLINGS.location().toString(), 2);
		json.addProperty("minecraft:scute", 10);
		json.addProperty("minecraft:seagrass", 1);
		json.addProperty("minecraft:sea_pickle", 1);
		json.addProperty("minecraft:shulker_shell", 20);
		json.addProperty("minecraft:shroomlight", 4);
		json.addProperty("minecraft:small_dripleaf", 2);
		json.addProperty("minecraft:snowball", 1);
		json.addProperty("minecraft:soul_sand", 4);
		json.addProperty("minecraft:soul_soil", 4);
		json.addProperty("minecraft:sugar_cane", 1);
		json.addProperty("minecraft:spore_blossom", 2);
		json.addProperty("minecraft:tall_grass", 1);
		json.addProperty("minecraft:twisting_vines", 1);
		json.addProperty("minecraft:vine", 1);
		json.addProperty("minecraft:warped_fungus", 1);
		json.addProperty("minecraft:warped_nylium", 1);
		json.addProperty("minecraft:warped_roots", 1);
		json.addProperty("minecraft:water_bottle", 3);
		json.addProperty("minecraft:water_bucket", 193);
		json.addProperty("minecraft:weeping_vines", 1);
		json.addProperty("minecraft:wet_sponge", 8);
		json.addProperty("#forge:amethyst_buds", 4);
		json.addProperty("#forge:bones", 2);
		json.addProperty("#forge:cobblestone", 1);
		json.addProperty("#forge:coral", 1);
		json.addProperty("#forge:crops", 1);
		json.addProperty("#forge:dusts/prismarine", 4);
		json.addProperty("#forge:dusts/glowstone", 2);
		json.addProperty("#forge:eggs", 1);
		json.addProperty("#forge:end_stones", 6);
		json.addProperty("#forge:ender_pearls", 8);
		json.addProperty("#forge:feathers", 1);
		json.addProperty("#forge:gravel", 2);
		json.addProperty("#forge:gems/amethyst", 4);
		json.addProperty("#forge:gems/prismarine", 4);
		json.addProperty("#forge:gunpowder", 2);
		json.addProperty("#forge:heads", 12);
		json.addProperty("#forge:leather", 3);
		json.addProperty("#forge:mushrooms", 1);
		json.addProperty("#forge:nether_stars", 1012);
		json.addProperty("#forge:rods/blaze", 4);
		json.addProperty("#forge:raw_food", 2);
		json.addProperty("#forge:raw_materials/copper", 16);
		json.addProperty("#forge:raw_materials/gold", 84);
		json.addProperty("#forge:raw_materials/iron", 64);
		json.addProperty("#forge:netherrack", 1);
		json.addProperty("#forge:obsidian", 16);
		json.addProperty("#forge:ores/quartz", 48);
		json.addProperty("#forge:ores/netherite_scrap", 1024);
		json.addProperty("#forge:stone", 1);
		json.addProperty("#forge:string", 1);
		json.addProperty("#forge:seeds", 1);
		json.addProperty("#forge:slimeballs", 2);
		json.addProperty("#forge:weather_copper_blocks", 144);
	}

	@Override
	public String getName() {
		return "Matter Generator";
	}

}
