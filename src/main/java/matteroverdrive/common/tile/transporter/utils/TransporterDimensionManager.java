package matteroverdrive.common.tile.transporter.utils;

import java.util.function.Function;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

public class TransporterDimensionManager implements ITeleporter {

	@Override
	public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw,
			Function<Boolean, Entity> repositionEntity) {
		return repositionEntity.apply(false);
	}

	@Override
	public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld,
			Function<ServerLevel, PortalInfo> defaultPortalInfo) {
		return new PortalInfo(entity.position(), Vec3.ZERO, entity.getYRot(), entity.getXRot());
	}

	@Override
	public boolean isVanilla() {
		return false;
	}

	@Override
	public boolean playTeleportSound(ServerPlayer player, ServerLevel sourceWorld, ServerLevel destWorld) {
		return false;
	}

}
