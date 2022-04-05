package matteroverdrive.core.datagen.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public class MatterValueGenerator implements DataProvider {

	private static final String DATA_LOC = "data/matteroverdrive/matter/values.json";
	DataGenerator gen;
	
	public MatterValueGenerator(DataGenerator gen) {	
		this.gen = gen;
	}
	
	@Override
	public void run(HashCache pCache) throws IOException {
		FileWriter blockWriter;
		try {
			File loc = gen.getOutputFolder().resolve(DATA_LOC).toFile();
			loc.getParentFile().mkdirs();
			blockWriter = new FileWriter(loc);
			List<Pair<String, Integer>> values = hardcodedValues();
			blockWriter.write("{");
			blockWriter.write("\n");
			for(Pair<String, Integer> value : values) {
				String entry = "    \"" + value.getFirst() + "\" : " + value.getSecond() + ",";
				blockWriter.write(entry);
				blockWriter.write("\n");
			}
			blockWriter.write("}");
			blockWriter.write("\n");
			blockWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static List<Pair<String, Integer>> hardcodedValues(){
		List<Pair<String, Integer>> values = new ArrayList<>();
		values.addAll(getFromItemTag(ItemTags.DIRT, 1));
		values.addAll(getFromItemTag(ItemTags.LOGS, 16));
		values.addAll(getFromItemTag(ItemTags.WOOL, 2));
		return values;
	}

	@Override
	public String getName() {
		return "Matter Generator";
	}
	
	private static List<Pair<String, Integer>> getFromItemTag(TagKey<Item> tag, int value){
		List<Pair<String, Integer>> values = new ArrayList<>();
		ForgeRegistries.ITEMS.tags().getTag(tag).forEach(h -> {
			values.add(Pair.of(h.getRegistryName().toString(), value));
		});
		return values;
	}
	
	/* Hard Coded
	 * 
	 * Tags:
	 * Logs: 16 kM
	 * 
	 * Granite: 7 kM
	 * Diorite: 4 kM
	 * Andesite: 3 kM
	 * 
	 */
	
	
}
