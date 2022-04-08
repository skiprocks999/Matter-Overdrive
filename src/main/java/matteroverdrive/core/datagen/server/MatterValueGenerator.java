package matteroverdrive.core.datagen.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.tags.ItemTags;

public class MatterValueGenerator implements DataProvider {

	private static final String DATA_LOC = "data/matteroverdrive/matter/values.json";
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
	DataGenerator gen;
	
	public MatterValueGenerator(DataGenerator gen) {	
		this.gen = gen;
	}
	
	@Override
	public void run(HashCache pCache) throws IOException {
		JsonObject json = new JsonObject();
		addValues(json);
		Path path = gen.getOutputFolder().resolve(DATA_LOC);
		try {
			String s = GSON.toJson((JsonElement)json);
            
			String s1 = SHA1.hashUnencodedChars(s).toString();
            if (!Objects.equals(pCache.getHash(path), s1) || !Files.exists(path)) {
               Files.createDirectories(path.getParent());
               BufferedWriter bufferedwriter = Files.newBufferedWriter(path);

               try {
                  bufferedwriter.write(s);
               } catch (Throwable throwable1) {
                  if (bufferedwriter != null) {
                     try {
                        bufferedwriter.close();
                     } catch (Throwable throwable) {
                        throwable1.addSuppressed(throwable);
                     }
                  }

                  throw throwable1;
               }

               if (bufferedwriter != null) {
                  bufferedwriter.close();
               }
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void addValues(JsonObject json) {
		json.addProperty("minecraft:basalt", 4);
		json.addProperty("minecraft:bedrock", 1024);
		json.addProperty("minecraft:blackstone", 4);
		json.addProperty("minecraft:budding_amethyst", 1);
		json.addProperty("minecraft:calcite", 1);
		json.addProperty("minecraft:carved_pumpkin", 2);
		json.addProperty("#" + ItemTags.COAL_ORES.location().toString(), 16);
		json.addProperty("#" + ItemTags.COPPER_ORES.location().toString(), 16);
		json.addProperty("minecraft:crimson_nylium", 1);
		json.addProperty("minecraft:crying_obsidian", 16);
		json.addProperty("#" + ItemTags.DIAMOND_ORES.location().toString(), 512);
		json.addProperty("#" + ItemTags.DIRT.location().toString(), 1);
		json.addProperty("#" + ItemTags.EMERALD_ORES.location().toString(), 512);
		json.addProperty("#" + ItemTags.FLOWERS.location().toString(), 1);
		json.addProperty("#" + ItemTags.GOLD_ORES.location().toString(), 84);
		json.addProperty("minecraft:ice", 1);
		json.addProperty("#" + ItemTags.IRON_ORES.location().toString(), 64);
		json.addProperty("#" + ItemTags.LAPIS_ORES.location().toString(), 16);
		json.addProperty("#" + ItemTags.LEAVES.location().toString(), 1);
		json.addProperty("#" + ItemTags.LOGS.location().toString(), 16);
		json.addProperty("#" + ItemTags.MUSIC_DISCS.location().toString(), 1);
		json.addProperty("minecraft:pumpkin", 2);
		json.addProperty("#" + ItemTags.REDSTONE_ORES.location().toString(), 16);
		json.addProperty("#" + ItemTags.SAND.location().toString(), 2);
		json.addProperty("#" + ItemTags.SAPLINGS.location().toString(), 2);
		json.addProperty("minecraft:soul_sand", 4);
		json.addProperty("minecraft:soul_soil", 4);
		json.addProperty("minecraft:warped_nylium", 1);
		json.addProperty("minecraft:wet_sponge", 8);
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
