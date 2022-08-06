package matteroverdrive.common.event.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;

import matteroverdrive.MatterOverdrive;
import matteroverdrive.client.particle.replicator.ParticleOptionReplicator;
import matteroverdrive.common.tile.transporter.ActiveTransportDataWrapper;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.entity_data.CapabilityEntityData;
import matteroverdrive.core.capability.types.entity_data.ICapabilityEntityData;
import matteroverdrive.core.capability.types.overworld_data.ICapabilityOverworldData;
import matteroverdrive.core.eventhandler.server.AbstractServerTickHandler;
import matteroverdrive.core.packet.NetworkHandler;
import matteroverdrive.core.packet.type.clientbound.PacketSyncClientEntityCapability;
import matteroverdrive.core.utils.UtilsMath;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.network.NetworkDirection;

public class TeleporterArrivalHandler extends AbstractServerTickHandler {

	@Override
	public void handleTick(MinecraftServer server, Phase phase, boolean enoughTime) {
		if (phase == Phase.START) {
			ServerLevel overworld = server.overworld();
			LazyOptional<ICapabilityOverworldData> overworldData = overworld
					.getCapability(MatterOverdriveCapabilities.OVERWORLD_DATA).cast();
			if (overworldData.isPresent()) {
				List<ActiveTransportDataWrapper> finished = new ArrayList<>();
				ICapabilityOverworldData data = overworldData.resolve().get();
				for (ActiveTransportDataWrapper wrapper : data.getTransporterData()) {
					if (wrapper.dimension != null && wrapper.entityID != null) {
						ServerLevel world = server.getLevel(wrapper.dimension);
						Entity entity = world.getEntity(wrapper.entityID);
						if (entity == null || entity.isRemoved() || wrapper.timeRemaining == 0) {
							finished.add(wrapper);
						} else {
							double progress = (double) wrapper.timeRemaining / 70.0F;
							int particles = (int) (progress * 20);
							for (int i = 0; i < particles; i++) {
								handleParticles(entity, world, progress);
							}
							wrapper.timeRemaining--;
							if (entity.getCapability(MatterOverdriveCapabilities.ENTITY_DATA).isPresent()) {
								LazyOptional<ICapabilityEntityData> lazy = entity
										.getCapability(MatterOverdriveCapabilities.ENTITY_DATA).cast();
								CapabilityEntityData capData = (CapabilityEntityData) lazy.resolve().get();
								if (capData.getTransporterTimer() > 0) {
									capData.setTransporterTimer(capData.getTransporterTimer() - 1);
									if (entity instanceof ServerPlayer player) {
										NetworkHandler.CHANNEL.sendTo(
												new PacketSyncClientEntityCapability(capData, entity.getUUID()),
												player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
									}
								}
							}
						}
					} else {
						finished.add(wrapper);
					}
				}
				for (ActiveTransportDataWrapper wrapper : finished) {
					data.removeTransportData(wrapper);
				}
			}
		}
	}

	private void handleParticles(Entity entity, ServerLevel world, double progress) {
		Vector3f vec = new Vector3f((float) entity.getX(), (float) entity.getY() - 1, (float) entity.getZ());
		double entityRadius = entity.getBbWidth();
		double entityArea = Math.max(entityRadius * entity.getBbHeight(), 0.3);
		Random random = MatterOverdrive.RANDOM;
		double radiusX = entityRadius + random.nextDouble() * 0.2f;
		double radiusZ = entityRadius + random.nextDouble() * 0.2f;
		double time = Math.min(progress, 1);
		float gravity = 0.015f;
		int count = (int) Math.round(UtilsMath.easeIn(time, 2, entityArea * 15, 1));
		time = 1 - time;
		int age = Math.max((int) Math.round(UtilsMath.easeIn(time, 5, 15, 1)), 2);

		for (int i = 0; i < count; i++) {
			float speed = 0.5F;
			float height = vec.y() + random.nextFloat() * entity.getBbHeight();

			Vector3f origin = new Vector3f(vec.x(), height, vec.z());
			Vector3f pos = UtilsMath.randomSpherePoint(origin.x(), origin.y(), origin.z(),
					new Vector3d(radiusX, 0, radiusZ), random);

			world.sendParticles(new ParticleOptionReplicator().setGravity(gravity).setScale(0.1F).setAge(age), pos.x(),
					pos.y(), pos.z(), 0, 0, speed, 0, 0);
		}

	}

}
