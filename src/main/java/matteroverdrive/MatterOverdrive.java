package matteroverdrive;

import com.mojang.logging.LogUtils;

import matteroverdrive.client.ClientRegister;
import matteroverdrive.core.packet.NetworkHandler;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(References.ID)
@EventBusSubscriber(modid = References.ID, bus = Bus.MOD)
public class MatterOverdrive {

    public static final Logger LOGGER = LogUtils.getLogger();

    public MatterOverdrive() {
    	IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    	SoundRegister.SOUNDS.register(bus);
    	DeferredRegisters.BLOCKS.register(bus);
		DeferredRegisters.ITEMS.register(bus);
		DeferredRegisters.TILES.register(bus);
		DeferredRegisters.CONTAINERS.register(bus);
		DeferredRegisters.FLUIDS.register(bus);
		DeferredRegisters.ENTITIES.register(bus);
    }

    @SubscribeEvent
	public static void onCommonSetup(FMLCommonSetupEvent event) {
    	NetworkHandler.init();
    }

    @SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        
    }

    @SubscribeEvent
	public static void registerRecipeSerialziers(RegistryEvent.Register<RecipeSerializer<?>> event) {
    	
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
    	ClientRegister.init();
    }
}
