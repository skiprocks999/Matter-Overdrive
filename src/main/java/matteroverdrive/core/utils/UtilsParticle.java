package matteroverdrive.core.utils;

import com.mojang.math.Vector3f;

import matteroverdrive.MatterOverdrive;
import matteroverdrive.client.particle.vent.ParticleOptionVent;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

public class UtilsParticle {

	private static final float SCALE = 0.1F;

	public static void spawnVentParticlesSphere(Vector3f pos, float speed, Direction side, int count) {
		for (int i = 0; i < count; i++) {

			Vec3i dirVec = side.getNormal();

			Vector3f circle = UtilsMath.randomSpherePoint(MatterOverdrive.RANDOM.nextFloat(), MatterOverdrive.RANDOM);
			circle.mul(0.4f);
			float rot = side.toYRot();

			if (rot == 90.0F) {
				circle.set(-circle.z(), circle.y(), circle.x());
			} else if (rot == 180.0F) {
				circle.set(-circle.x(), circle.y(), -circle.z());
			} else if (rot == 270.0F) {
				circle.set(circle.z(), circle.y(), -circle.x());
			}

			pos.add(circle);
			pos.add(dirVec.getX() * 0.5F, dirVec.getY() * 0.5F, dirVec.getZ() * 0.5F);

			Minecraft minecraft = Minecraft.getInstance();

			minecraft.level.addParticle(new ParticleOptionVent().setScale(SCALE).setAlpha(0.15F), pos.x(), pos.y(),
					pos.z(), dirVec.getX() * speed, dirVec.getY() * speed, dirVec.getZ() * speed);
		}
	}
	
	public static void spawnVentParticlesAtFace(Vector3f pos, float speed, Direction side, int count) {
		for (int i = 0; i < count; i++) {

			Vec3i dirVec = side.getNormal();

			Vector3f circle = UtilsMath.randomSpherePoint(MatterOverdrive.RANDOM.nextFloat(), MatterOverdrive.RANDOM);
			circle.mul(0.4f);
			float rot = side.toYRot();

			if (rot == 90.0F) {
				circle.set(-circle.z(), circle.y(), circle.x());
			} else if (rot == 180.0F) {
				circle.set(-circle.x(), circle.y(), -circle.z());
			} else if (rot == 270.0F) {
				circle.set(circle.z(), circle.y(), -circle.x());
			}
			
			switch(side) {
			case NORTH,SOUTH:
				pos.add(circle.x(), circle.y(), 0.0F);
				pos.add(dirVec.getX() * 0.5F, dirVec.getY() * 0.5F, 0.0F);
				break;
			case EAST, WEST:
				pos.add(0.0F, circle.y(), circle.z());
				pos.add(0.0F, dirVec.getY() * 0.5F, dirVec.getZ() * 0.5F);
				break;
			case UP, DOWN:
				pos.add(circle.x(), 0.0F, circle.z());
				pos.add(dirVec.getX() * 0.5F, 0.0F, dirVec.getZ() * 0.5F);
				break;
			}

			//pos.add(circle);
			//pos.add(dirVec.getX() * 0.5F, dirVec.getY() * 0.5F, dirVec.getZ() * 0.5F);

			Minecraft minecraft = Minecraft.getInstance();

			minecraft.level.addParticle(new ParticleOptionVent().setScale(SCALE).setAlpha(0.15F), pos.x(), pos.y(),
					pos.z(), dirVec.getX() * speed, dirVec.getY() * speed, dirVec.getZ() * speed);
		}
	}

}
