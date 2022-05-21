package matteroverdrive.common.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;

import matteroverdrive.MatterOverdrive;
import matteroverdrive.References;
import matteroverdrive.client.particle.replicator.ParticleOptionReplicator;
import matteroverdrive.common.tile.transporter.ActiveTransportDataWrapper;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.entity_data.CapabilityEntityData;
import matteroverdrive.core.capability.types.overworld_data.CapabilityOverworldData;
import matteroverdrive.core.capability.types.overworld_data.ICapabilityOverworldData;
import matteroverdrive.core.command.CommandGenerateMatterValues;
import matteroverdrive.core.command.CommandManualMatterValue;
import matteroverdrive.core.matter.MatterRegister;
import matteroverdrive.core.utils.UtilsMath;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
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

	public static List<Pair<Integer, Runnable>> ACTIVE_TELEPORTS = new ArrayList<>();;
	
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
		if(!world.getCapability(MatterOverdriveCapabilities.OVERWORLD_DATA).isPresent() && world.dimension().equals(Level.OVERWORLD)) {
			event.addCapability(new ResourceLocation(References.ID, "overworld_data"), new CapabilityOverworldData());
		}
	}
	
	@SubscribeEvent
	public static void attachEntityCaps(AttachCapabilitiesEvent<Entity> event) {
		if(event.getObject().getCapability(MatterOverdriveCapabilities.ENTITY_DATA).isPresent()) {
			event.addCapability(new ResourceLocation(References.ID, "entity_data"), new CapabilityEntityData());
		}
	}
	
	@SubscribeEvent
	public static void handlerTransporterTickTimer(ServerTickEvent event) {
		if(event.phase == Phase.START) {
			ServerLevel world = ServerLifecycleHooks.getCurrentServer().overworld();
			LazyOptional<ICapabilityOverworldData> overworldData = world.getCapability(MatterOverdriveCapabilities.OVERWORLD_DATA).cast();
			if(overworldData.isPresent()) {
				List<ActiveTransportDataWrapper> finished = new ArrayList<>();
				ICapabilityOverworldData data = overworldData.resolve().get();
				for(ActiveTransportDataWrapper wrapper : data.getTransporterData()) {
					Entity entity = world.getEntity(wrapper.entityID);
					if(entity == null || entity.isRemoved() || wrapper.timeRemaining == 0) {
						finished.add(wrapper);
					} else {
						//MatterOverdrive.LOGGER.info(entity.position().toString());
						double progress = (double) wrapper.timeRemaining / 80.0F;
						int particles = (int) (progress * 20);
						for(int i = 0; i < particles; i++) {
							//handleParticles(entity, world, progress);
						}
						wrapper.timeRemaining--;
						entity.getCapability(MatterOverdriveCapabilities.ENTITY_DATA).ifPresent(h -> {
							if(h.getTransporterTimer() > 0) {
								h.setTransporterTimer(h.getTransporterTimer() - 1);
							}
						});;
					}	
				}
				for(ActiveTransportDataWrapper wrapper : finished) {
					data.removeTransportData(wrapper);
				}
			}
		}
	}
	
	private static void handleParticles(Entity entity, ServerLevel world, double progress) {
		Vector3f vec = new Vector3f((float) entity.getX(), (float) entity.getY() - 1, (float) entity.getZ());
		double entityRadius = entity.getBbWidth();
		double entityArea = Math.max(entityRadius * entity.getBbHeight(), 0.3);
		Random random = MatterOverdrive.RANDOM;
		double radiusX = entityRadius + random.nextDouble() * 0.2f;
		double radiusZ = entityRadius + random.nextDouble() * 0.2f;
		double time = Math.min(progress, 1);
		float gravity = 0.015f;
		int age = (int) Math.round(UtilsMath.easeIn(time, 5, 15, 1));
		int count = (int) Math.round(UtilsMath.easeIn(time, 2, entityArea * 15, 1));

		for (int i = 0; i < count; i++) {
			float speed = random.nextFloat() * 0.05f + 0.15f;
			float height = vec.y() + random.nextFloat() * entity.getBbHeight();

			Vector3f origin = new Vector3f(vec.x(), height, vec.z());
			Vector3f pos = UtilsMath.randomSpherePoint(origin.x(), origin.y(), origin.z(),
					new Vector3d(radiusX, 0, radiusZ), random);
			origin.sub(pos);
			origin.mul(speed);
			
			world.sendParticles(new ParticleOptionReplicator().setCenter(origin.x(), origin.y(), origin.z())
					.setGravity(gravity).setAge(age), pos.x(), pos.y(), pos.z(), 0, origin.x(), origin.y(), origin.z(), 0);
		}

	}

}
