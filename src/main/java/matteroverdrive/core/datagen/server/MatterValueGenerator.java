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
		addHardcodedValues(json);
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
	
	private static void addHardcodedValues(JsonObject json){
		json.addProperty("#" + ItemTags.DIRT.location().toString(), 1);
		json.addProperty("#" + ItemTags.LOGS.location().toString(), 16);
		json.addProperty("#" + ItemTags.WOOL.location().toString(), 2);
	}

	@Override
	public String getName() {
		return "Matter Generator";
	}
	
	
}
