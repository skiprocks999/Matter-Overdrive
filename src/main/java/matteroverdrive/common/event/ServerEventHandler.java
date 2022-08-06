package matteroverdrive.common.event;

import java.util.ArrayList;
import java.util.List;

import matteroverdrive.References;
import matteroverdrive.common.event.handler.TeleporterArrivalHandler;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.entity_data.CapabilityEntityData;
import matteroverdrive.core.capability.types.overworld_data.CapabilityOverworldData;
import matteroverdrive.core.command.CommandGenerateMatterValues;
import matteroverdrive.core.command.CommandManualMatterValue;
import matteroverdrive.core.eventhandler.server.AbstractServerTickHandler;
import matteroverdrive.core.matter.MatterRegister;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.server.ServerLifecycleHooks;

@EventBusSubscriber(modid = References.ID, bus = Bus.FORGE)
public class ServerEventHandler {

	private static final List<AbstractServerTickHandler> TICK_HANDLERS = new ArrayList<>();

	public static void init() {
		TICK_HANDLERS.add(new TeleporterArrivalHandler());
	}

	@SubscribeEvent
	public static void reloadListeners(AddReloadListenerEvent event) {
		event.addListener(MatterRegister.INSTANCE);
	}

	@SubscribeEvent
	public static void serverStartedHandler(ServerStartedEvent event) {
		MatterRegister.INSTANCE.generateTagValues();
	}

	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent event) {
		CommandGenerateMatterValues.register(event.getDispatcher());
		CommandManualMatterValue.register(event.getDispatcher());
	}

	@SubscribeEvent
	public static void attachOverworldData(AttachCapabilitiesEvent<Level> event) {
		Level world = event.getObject();
		if (!world.getCapability(MatterOverdriveCapabilities.OVERWORLD_DATA).isPresent()
				&& world.dimension().equals(Level.OVERWORLD)) {
			event.addCapability(new ResourceLocation(References.ID, "overworld_data"), new CapabilityOverworldData());
		}
	}

	@SubscribeEvent
	public static void attachEntityCaps(AttachCapabilitiesEvent<Entity> event) {
		if (!event.getObject().getCapability(MatterOverdriveCapabilities.ENTITY_DATA).isPresent()) {
			event.addCapability(new ResourceLocation(References.ID, "entity_data"), new CapabilityEntityData());
		}
	}

	@SubscribeEvent
	public static void handlerServerTickEvents(ServerTickEvent event) {

		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		Phase phase = event.phase;
		boolean enoughTime = event.haveTime();

		for (AbstractServerTickHandler handler : TICK_HANDLERS) {
			handler.handleTick(server, phase, enoughTime);
		}

	}

}
