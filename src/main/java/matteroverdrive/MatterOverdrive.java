package matteroverdrive;

import java.util.Random;

import matteroverdrive.common.block.OverdriveBlockStates;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import matteroverdrive.client.ClientRegister;
import matteroverdrive.common.event.ServerEventHandler;
import matteroverdrive.common.recipe.RecipeInit;
import matteroverdrive.core.block.OverdriveBlockProperties;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.config.MatterOverdriveConfig;
import matteroverdrive.core.matter.DefaultGeneratorConsumers;
import matteroverdrive.core.matter.MatterRegister;
import matteroverdrive.core.packet.NetworkHandler;
import matteroverdrive.registry.BlockRegistry;
import matteroverdrive.registry.EntityRegistry;
import matteroverdrive.registry.FluidRegistry;
import matteroverdrive.registry.ItemRegistry;
import matteroverdrive.registry.MenuRegistry;
import matteroverdrive.registry.ParticleRegistry;
import matteroverdrive.registry.TileRegistry;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(References.ID)
@EventBusSubscriber(modid = References.ID, bus = Bus.MOD)
public class MatterOverdrive {

	public MatterRegister register;

	public static final Logger LOGGER = LogUtils.getLogger();

	public static final Random RANDOM = new Random();

	public MatterOverdrive() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		OverdriveBlockProperties.Defaults.init();
		SoundRegister.SOUNDS.register(bus);
		OverdriveBlockStates.init();
		BlockRegistry.BLOCKS.register(bus);
		ItemRegistry.ITEMS.register(bus);
		TileRegistry.TILES.register(bus);
		MenuRegistry.MENUS.register(bus);
		FluidRegistry.FLUIDS.register(bus);
		EntityRegistry.ENTITIES.register(bus);
		ParticleRegistry.PARTICLES.register(bus);
		RecipeInit.RECIPE_TYPES.register(bus);
		RecipeInit.RECIPE_SERIALIZER.register(bus);
		MatterRegister.init();
		ModLoadingContext.get().registerConfig(Type.COMMON, MatterOverdriveConfig.COMMON_CONFIG,
				"matteroverdrive/matteroverdrive.common.toml");
		ModLoadingContext.get().registerConfig(Type.CLIENT, MatterOverdriveConfig.CLIENT_CONFIG,
				"matteroverdrive/matteroverdrive.client.toml");

		MatterRegister.INSTANCE = new MatterRegister().subscribeAsSyncable(NetworkHandler.CHANNEL);
		DefaultGeneratorConsumers.init();

		ServerEventHandler.init();

	}

	@SubscribeEvent
	public static void onCommonSetup(FMLCommonSetupEvent event) {
		NetworkHandler.init();
	}

	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		MatterOverdriveCapabilities.register(event);
	}

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		ClientRegister.init();
	}
}
