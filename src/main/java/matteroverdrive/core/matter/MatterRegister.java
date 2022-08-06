/*
Based off of example by Commable under MIT License

See https://github.com/Commoble/databuddy/blob/1.18.x/src/main/java/commoble/databuddy/data/MergeableCodecDataManager.java
for full details

 */

package matteroverdrive.core.matter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;

import matteroverdrive.MatterOverdrive;
import matteroverdrive.References;
import matteroverdrive.core.config.MatterOverdriveConfig;
import matteroverdrive.core.packet.type.clientbound.PacketClientMatterValues;
import matteroverdrive.core.utils.UtilsMatter;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PacketDistributor.PacketTarget;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;

public class MatterRegister extends SimplePreparableReloadListener<Map<ResourceLocation, JsonObject>> {

	protected static final String JSON_EXTENSION = ".json";
	protected static final int JSON_EXTENSION_LENGTH = JSON_EXTENSION.length();

	private static final List<BiConsumer<HashMap<Item, Double>, RecipeManager>> GEN_MATTER_CONSUMERS = new ArrayList<>();

	private HashMap<Item, Double> SERVER_VALUES = new HashMap<>();
	private HashMap<TagKey<Item>, Double> parsedTags = new HashMap<>();
	private static final Gson GSON = new Gson();
	public static MatterRegister INSTANCE = null;

	private final String folderName;
	private final Logger logger;

	private HashMap<Item, Double> CLIENT_VALUES = new HashMap<>();

	public MatterRegister() {
		folderName = "matter";
		logger = MatterOverdrive.LOGGER;
	}

	@Nullable
	public Double getServerMatterValue(ItemStack item) {
		if (MatterOverdriveConfig.validate_matter_items.get()) {
			return UtilsMatter.validateItem(item) ? SERVER_VALUES.get(item.getItem()) : null;
		}
		return SERVER_VALUES.get(item.getItem());
	}

	@Nullable
	public Double getClientMatterValue(ItemStack item) {
		if (MatterOverdriveConfig.validate_matter_items.get()) {
			return UtilsMatter.validateItem(item) ? CLIENT_VALUES.get(item.getItem()) : null;
		}
		return CLIENT_VALUES.get(item.getItem());
	}

	@Override
	protected Map<ResourceLocation, JsonObject> prepare(final ResourceManager resourceManager,
			final ProfilerFiller profiler) {
		final List<Pair<ResourceLocation, List<JsonObject>>> map = new ArrayList<>();
		List<Entry<ResourceLocation, Resource>> resources = new ArrayList<>(
				resourceManager.listResources(this.folderName, MatterRegister::isStringJsonFile).entrySet());
		Collections.reverse(resources);
		// we go in reverse, as higher priority data packs are found later in the list
		for (Entry<ResourceLocation, Resource> entry : resources) {
			ResourceLocation resourceLocation = entry.getKey();
			final String namespace = resourceLocation.getNamespace();
			final String filePath = resourceLocation.getPath();
			final String dataPath = filePath.substring(this.folderName.length() + 1,
					filePath.length() - JSON_EXTENSION_LENGTH);

			final ResourceLocation jsonIdentifier = new ResourceLocation(namespace, dataPath);
			final List<JsonObject> unmergedRaws = new ArrayList<>();

			Resource resource = entry.getValue();
			try (final InputStream inputStream = resource.open();
					final Reader reader = new BufferedReader(
							new InputStreamReader(inputStream, StandardCharsets.UTF_8));) {
				final JsonObject jsonElement = (JsonObject) GsonHelper.fromJson(GSON, reader, JsonElement.class);
				unmergedRaws.add(jsonElement);
			} catch (RuntimeException | IOException exception) {
				this.logger.error("Data loader for {} could not read data {} from file {} in data pack {}",
						this.folderName, jsonIdentifier, resourceLocation, resource.sourcePackId(), exception);
			}

			map.add(Pair.of(jsonIdentifier, unmergedRaws));
		}
		/*
		 * for (ResourceLocation resourceLocation : resources) { final String namespace
		 * = resourceLocation.getNamespace(); final String filePath =
		 * resourceLocation.getPath(); final String dataPath =
		 * filePath.substring(this.folderName.length() + 1, filePath.length() -
		 * JSON_EXTENSION_LENGTH);
		 * 
		 * final ResourceLocation jsonIdentifier = new ResourceLocation(namespace,
		 * dataPath); final List<JsonObject> unmergedRaws = new ArrayList<>();
		 * 
		 * try { for (Resource resource :
		 * resourceManager.getResources(resourceLocation)) { try (final InputStream
		 * inputStream = resource.getInputStream(); final Reader reader = new
		 * BufferedReader( new InputStreamReader(inputStream, StandardCharsets.UTF_8));)
		 * { final JsonObject jsonElement = (JsonObject) GsonHelper.fromJson(GSON,
		 * reader, JsonElement.class); unmergedRaws.add(jsonElement); } catch
		 * (RuntimeException | IOException exception) { this.logger.
		 * error("Data loader for {} could not read data {} from file {} in data pack {}"
		 * , this.folderName, jsonIdentifier, resourceLocation,
		 * resource.getSourceName(), exception); } finally {
		 * IOUtils.closeQuietly(resource); } } } catch (IOException exception) {
		 * this.logger.error("Data loader for {} could not read data {} from file {}",
		 * this.folderName, jsonIdentifier, resourceLocation, exception); }
		 * map.add(Pair.of(jsonIdentifier, unmergedRaws)); }
		 */
		JsonObject merged = new JsonObject();
		map.forEach(pair -> {
			pair.getSecond().forEach(list -> {
				list.entrySet().forEach(json -> {
					String key = json.getKey();
					if (!merged.has(key)) {
						merged.addProperty(key, json.getValue().getAsDouble());
					}
				});
			});
		});
		Map<ResourceLocation, JsonObject> combined = new HashMap<>();
		combined.put(new ResourceLocation(References.ID, "combinedmattervals"), merged);

		return combined;
	}

	private static boolean isStringJsonFile(final ResourceLocation filename) {
		return filename.toString().endsWith(JSON_EXTENSION);
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonObject> object, ResourceManager manager, ProfilerFiller profiler) {
		SERVER_VALUES.clear();
		parsedTags.clear();
		object.forEach((location, element) -> {
			element.entrySet().forEach(h -> {
				String key = h.getKey();
				if (key.charAt(0) == '#') {
					key = key.substring(1);
					String[] split = key.split(":");
					parsedTags.put(TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(split[0], split[1])),
							h.getValue().getAsDouble());
				} else {
					ResourceLocation loc = new ResourceLocation(key);
					Item item = null;
					try {
						item = ForgeRegistries.ITEMS.getHolder(loc).get().value();
					} catch (Exception e) {
						MatterOverdrive.LOGGER.info(loc.toString() + " does not exist!");
					}
					double value = h.getValue().getAsDouble();
					if (!SERVER_VALUES.containsKey(item) && value > 0) {
						SERVER_VALUES.put(item, value);
					}
				}
			});
		});
	}

	public void generateTagValues() {
		parsedTags.forEach((key, val) -> {
			Ingredient ing = Ingredient.of(key);
			for (ItemStack stack : ing.getItems()) {
				Item itm = stack.getItem();
				if (!SERVER_VALUES.containsKey(itm) && val > 0) {
					SERVER_VALUES.put(itm, val);
				}
			}
		});
		parsedTags.clear();
	}

	public MatterRegister subscribeAsSyncable(final SimpleChannel channel) {
		MinecraftForge.EVENT_BUS.addListener(this.getDatapackSyncListener(channel));
		return this;
	}

	private Consumer<OnDatapackSyncEvent> getDatapackSyncListener(final SimpleChannel channel) {
		return event -> {
			generateTagValues();
			ServerPlayer player = event.getPlayer();
			PacketClientMatterValues packet = new PacketClientMatterValues(SERVER_VALUES);
			PacketTarget target = player == null ? PacketDistributor.ALL.noArg()
					: PacketDistributor.PLAYER.with(() -> player);
			channel.send(target, packet);
		};
	}

	public void setClientValues(HashMap<Item, Double> valueMap) {
		this.CLIENT_VALUES = valueMap;
	}

	public static void addGeneratorConsumer(BiConsumer<HashMap<Item, Double>, RecipeManager> consumer) {
		GEN_MATTER_CONSUMERS.add(consumer);
	}

	public static List<BiConsumer<HashMap<Item, Double>, RecipeManager>> getConsumers() {
		return GEN_MATTER_CONSUMERS;
	}

}
