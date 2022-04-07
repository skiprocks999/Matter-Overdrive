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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;

import matteroverdrive.MatterOverdrive;
import matteroverdrive.References;
import matteroverdrive.core.listeners.MergeableCodecDataManager;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PacketDistributor.PacketTarget;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;

public class MatterRegister extends SimplePreparableReloadListener<Map<ResourceLocation, JsonObject>> {

	protected static final String JSON_EXTENSION = ".json";
	protected static final int JSON_EXTENSION_LENGTH = JSON_EXTENSION.length();
	
	private HashMap<Item, Integer> VALUES = new HashMap<>();
	private HashMap<TagKey<Item>, Integer> parsedTags = new HashMap<>();
	private boolean haveNoTagsParsed = false;
	private static final Gson GSON = new Gson();
	public static MatterRegister INSTANCE;
	
	private final String folderName;
	private final Logger logger;
	
	public MatterRegister() {
		folderName = "matter";
		logger = MatterOverdrive.LOGGER;
	}
	
	@Nullable
	//TODO move the tag loading to ServerStartedEvent if possible
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
		return VALUES.get(item);
	}
	
	@Override
	protected Map<ResourceLocation, JsonObject> prepare(final ResourceManager resourceManager, final ProfilerFiller profiler)
	{
		final Map<ResourceLocation, List<JsonObject>> map = Maps.newHashMap();

		for (ResourceLocation resourceLocation : resourceManager.listResources(this.folderName, MatterRegister::isStringJsonFile))
		{
			final String namespace = resourceLocation.getNamespace();
			final String filePath = resourceLocation.getPath();
			final String dataPath = filePath.substring(this.folderName.length() + 1, filePath.length() - JSON_EXTENSION_LENGTH);
			
			final ResourceLocation jsonIdentifier = new ResourceLocation(namespace, dataPath);
			final List<JsonObject> unmergedRaws = new ArrayList<>();

			try
			{
				for (Resource resource : resourceManager.getResources(resourceLocation))
				{
					try 
					(
						final InputStream inputStream = resource.getInputStream();
						final Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
					)
					{
						final JsonObject jsonElement = (JsonObject) GsonHelper.fromJson(GSON, reader, JsonElement.class);
						unmergedRaws.add(jsonElement);
					}
					catch(RuntimeException | IOException exception)
					{
						this.logger.error("Data loader for {} could not read data {} from file {} in data pack {}", this.folderName, jsonIdentifier, resourceLocation, resource.getSourceName(), exception); 
					}
					finally
					{
						IOUtils.closeQuietly(resource);
					}
				}
			}
			catch (IOException exception)
			{
				this.logger.error("Data loader for {} could not read data {} from file {}", this.folderName, jsonIdentifier, resourceLocation, exception);
			}
			
			
			map.put(jsonIdentifier, unmergedRaws);
		}
		
		JsonObject merged = new JsonObject();
		map.forEach((resource, list) -> {
			list.forEach(object -> {
				object.entrySet().forEach(h -> {
					merged.addProperty(h.getKey(), h.getValue().getAsInt());
				});
			});
		});
		Map<ResourceLocation, JsonObject> combined = new HashMap<>();
		combined.put(new ResourceLocation(References.ID, ":combinedMattedVals"), merged);
		
		return combined;
	}
	
	static boolean isStringJsonFile(final String filename)
	{
		return filename.endsWith(JSON_EXTENSION);
	}
	
	static void throwJsonParseException(final String codecParseFailure)
	{
		throw new JsonParseException(codecParseFailure);
	}
	
	/**
	 * This should be called at most once, during construction of your mod (static init of your main mod class is fine)
	 * (FMLCommonSetupEvent *may* work as well)
	 * Calling this method automatically subscribes a packet-sender to {@link OnDatapackSyncEvent}.
	 * @param <PACKET> the packet type that will be sent on the given channel
	 * @param channel The networking channel of your mod
	 * @param packetFactory  A packet constructor or factory method that converts the given map to a packet object to send on the given channel
	 * @return this manager object
	 */
	public <PACKET> MatterRegister subscribeAsSyncable(final SimpleChannel channel,
		final Function<Map<ResourceLocation, FINE>, PACKET> packetFactory)
	{
		MinecraftForge.EVENT_BUS.addListener(this.getDatapackSyncListener(channel, packetFactory));
		return this;
	}
	
	/** Generate an event listener function for the on-datapack-sync event **/
	private <PACKET> Consumer<OnDatapackSyncEvent> getDatapackSyncListener(final SimpleChannel channel,
		final Function<Map<ResourceLocation, FINE>, PACKET> packetFactory)
	{
		return event -> {
			ServerPlayer player = event.getPlayer();
			PACKET packet = packetFactory.apply(this.data);
			PacketTarget target = player == null
				? PacketDistributor.ALL.noArg()
				: PacketDistributor.PLAYER.with(() -> player);
			channel.send(target, packet);
		};
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonObject> object, ResourceManager manager, ProfilerFiller profiler) {
		VALUES.clear();
		parsedTags.clear();
		haveNoTagsParsed = true;
		object.forEach((location, element) -> {
			element.entrySet().forEach(h -> {
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
