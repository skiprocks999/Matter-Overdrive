package matteroverdrive.core.matter;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;

public class MatterRegister extends SimpleJsonResourceReloadListener {

	private static final HashMap<Item, Integer> VALUES = new HashMap<>();
	private static final Gson GSON = new Gson();
	
	public MatterRegister() {
		super(GSON, "src/main/resources/data/matteroverdrive/matter/values");
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager manager, ProfilerFiller profiler) {
		System.out.println("called");
		object.forEach((location, element) -> {
			System.out.println(element.toString());
		});
	}
	
}
