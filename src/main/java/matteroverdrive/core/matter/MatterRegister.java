package matteroverdrive.core.matter;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.gson.Gson;
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

	private HashMap<Item, Integer> VALUES = new HashMap<>();
	private HashMap<TagKey<Item>, Integer> parsedTags = new HashMap<>();
	private boolean haveNoTagsParsed = false;
	private static final Gson GSON = new Gson();
	public static MatterRegister INSTANCE;
	
	public MatterRegister() {
		super(GSON, "matter");
	}
	
	@Nullable
	public Integer getMatterValue(Item item) {
		if(haveNoTagsParsed) {
			parsedTags.forEach((key, val) -> {
				Ingredient ing = Ingredient.of(key);
				for(ItemStack stack : ing.getItems()) {
					Item itm = stack.getItem();
					if(!VALUES.containsKey(itm)) {
						VALUES.put(itm, val);
					}
				}
			});
			parsedTags.clear();
			haveNoTagsParsed = false;
			return VALUES.get(item);
		}	
		MatterOverdrive.LOGGER.info(VALUES.toString());
		return VALUES.get(item);
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager manager, ProfilerFiller profiler) {
		VALUES.clear();
		parsedTags.clear();
		haveNoTagsParsed = true;
		object.forEach((location, element) -> {
			JsonObject obj = (JsonObject) element;
			obj.entrySet().forEach(h -> {
				String key = h.getKey();
				if(key.charAt(0) == '#') {
					key = key.substring(1);
					String[] split = key.split(":");
					parsedTags.put(TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(split[0], split[1])), h.getValue().getAsInt());
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
