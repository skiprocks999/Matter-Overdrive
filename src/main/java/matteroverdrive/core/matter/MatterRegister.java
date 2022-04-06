package matteroverdrive.core.matter;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import matteroverdrive.MatterOverdrive;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

public class MatterRegister extends SimpleJsonResourceReloadListener {

	private static final HashMap<Item, Integer> VALUES = new HashMap<>();
	private static final Gson GSON = new Gson();
	
	public MatterRegister() {
		super(GSON, "matter");
	}
	
	@Nullable
	public static Integer getMatterValue(Item item) {
		return VALUES.get(item);
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager manager, ProfilerFiller profiler) {
		VALUES.clear();
		object.forEach((location, element) -> {
			JsonObject obj = (JsonObject) element;
			
			obj.entrySet().forEach(h -> {
				String key = h.getKey();
				MatterOverdrive.LOGGER.info(key);
				if(key.charAt(0) == '#') {
					key = key.substring(1);
					String[] split = key.split(":");
					Ingredient ing = Ingredient.of(TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(split[0], split[1])));
					for(ItemStack stack : ing.getItems()) {
						if(!VALUES.containsKey(stack.getItem())) {
							VALUES.put(stack.getItem(), h.getValue().getAsInt());
						}
					}
				} else {
					Item item = ForgeRegistries.ITEMS.getHolder(new ResourceLocation(key)).get().value();
					if(!VALUES.containsKey(item)) {
						VALUES.put(item, h.getValue().getAsInt());
					}
				}
			});
		});
	}
	
}
