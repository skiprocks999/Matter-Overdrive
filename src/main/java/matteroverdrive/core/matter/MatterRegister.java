package matteroverdrive.core.matter;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.minecraft.world.item.Item;

public class MatterRegister {

	private static final HashMap<Item, Integer> VALUES = new HashMap<>();
	private static final Gson GSON = new Gson();
	
	public static void init() {
		try (Reader reader = new FileReader("src/main/resources/data/matteroverdrive/matter/values.json")) {
			JsonObject json = GSON.fromJson(reader, JsonObject.class);
			System.out.println(json.toString());
		} catch (IOException io) {
			System.out.println("oof");
		}
	}
	
}
