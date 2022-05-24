package matteroverdrive;

import java.util.Random;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import matteroverdrive.client.ClientRegister;
import matteroverdrive.common.recipe.RecipeInit;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.config.MatterOverdriveConfig;
import matteroverdrive.core.matter.DefaultGeneratorConsumers;
import matteroverdrive.core.matter.MatterRegister;
import matteroverdrive.core.packet.NetworkHandler;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
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
		SoundRegister.SOUNDS.register(bus);
		DeferredRegisters.BLOCKS.register(bus);
		DeferredRegisters.ITEMS.register(bus);
		DeferredRegisters.TILES.register(bus);
		DeferredRegisters.CONTAINERS.register(bus);
		DeferredRegisters.FLUIDS.register(bus);
		DeferredRegisters.ENTITIES.register(bus);
		DeferredRegisters.PARTICLES.register(bus);
		RecipeInit.RECIPE_TYPES.register(bus);
		RecipeInit.RECIPE_SERIALIZER.register(bus);

		ModLoadingContext.get().registerConfig(Type.COMMON, MatterOverdriveConfig.COMMON_CONFIG,
				"matteroverdrive/matteroverdrive.common.toml");
		ModLoadingContext.get().registerConfig(Type.CLIENT, MatterOverdriveConfig.CLIENT_CONFIG,
				"matteroverdrive/matteroverdrive.client.toml");

		MatterRegister.INSTANCE = new MatterRegister().subscribeAsSyncable(NetworkHandler.CHANNEL);
		DefaultGeneratorConsumers.init();

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
	public static void registerRecipeSerialziers(RegistryEvent.Register<RecipeSerializer<?>> event) {

	}

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		ClientRegister.init();
	}
}
