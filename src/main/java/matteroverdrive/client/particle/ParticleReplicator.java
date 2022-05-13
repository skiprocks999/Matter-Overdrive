package matteroverdrive.client.particle;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3d;

import matteroverdrive.MatterOverdrive;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

public class ParticleReplicator extends TextureSheetParticle {

	private float initialScale;
	private double centerX, centerY, centerZ;
	
	public ParticleReplicator(ClientLevel world, double posX, double posY, double posZ, double xSpeed, double ySpeed,
			double zSpeed) {
		super(world, posX, posY, posZ, xSpeed, ySpeed, zSpeed);
		this.xd = this.xd * 0.009999999776482582D + xSpeed;
		this.yd = this.yd * 0.009999999776482582D + ySpeed;
		this.zd = this.zd * 0.009999999776482582D + zSpeed;
		this.initialScale = quadSize;
		this.rCol = this.gCol = this.bCol = 1.0F;
		this.lifetime = (int) (8.0D / (Math.random() * 0.8D + 0.2D)) + 4;
		setSprite(Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(new ResourceLocation("minecraft:block/glass")));
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		if (this.age++ >= this.lifetime) {
			this.remove();
		}

		//this.moveEntity(this.xd, this.yd, this.zd);
		Vector3d motion = new Vector3d(this.xd, this.yd, this.zd);
		Vector3d center = new Vector3d(this.centerX, this.centerY, this.centerZ);
		Vector3d position = new Vector3d(this.x, this.y, this.z);
		position.scale(-1);
		center.add(position);
		center.scale(gravity);
		center.add(motion);

		this.xd = center.x;
		this.yd = center.y;
		this.zd = center.z;

		//this.getBoundingBox().move(this.xd, this.yd, this.zd);
		this.x = (this.getBoundingBox().minX + this.getBoundingBox().maxX) / 2.0D;
		this.y = this.getBoundingBox().minY - (double) this.bbHeight;
		this.z = (this.getBoundingBox().minZ + this.getBoundingBox().maxZ) / 2.0D;

		//double speedOverTime = 1D;
		//this.xd *= speedOverTime;
		//this.yd *= speedOverTime;
		//this.zd *= speedOverTime;
		move(this.zd, this.yd, this.zd);
		MatterOverdrive.LOGGER.info(this.toString());
	}
	
	@Override
	public void render(VertexConsumer pBuffer, Camera pRenderInfo, float partialTicks) {
		float f6 = ((float) this.age + partialTicks) / (float) this.lifetime;
		this.quadSize = this.initialScale * (1.0F - f6 * f6 * 0.5F);
		super.render(pBuffer, pRenderInfo, partialTicks);
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}
	
	
	@Override
	protected int getLightColor(float pPartialTick) {
		float f1 = ((float) this.age + pPartialTick) / (float) this.lifetime;

		if (f1 < 0.0F) {
			f1 = 0.0F;
		}

		if (f1 > 1.0F) {
			f1 = 1.0F;
		}

		int i = getBrightness(pPartialTick);
		int j = i & 255;
		int k = i >> 16 & 255;
		j += (int) (f1 * 15.0F * 16.0F);

		if (j > 240) {
			j = 240;
		}

		return j | k << 16;
	}
	
	public int getBrightness(float light) {
		float f1 = ((float) this.age + light) / (float) this.lifetime;

		if (f1 < 0.0F) {
			f1 = 0.0F;
		}

		if (f1 > 1.0F) {
			f1 = 1.0F;
		}

		int f2 = super.getLightColor(light);
		return (int) (f2 * f1 + (1.0F - f1));
	}
	
	public void setCenter(double x, double y, double z) {
        this.centerX = x;
        this.centerY = y;
        this.centerZ = z;
    }
	
	public void setParticleMaxAge(int age) {
        this.lifetime = age;
    }
	
	public void setGravity(float gravity) {
        this.gravity = gravity;
    }
	
	public static class Factory implements ParticleProvider<SimpleParticleType> {

		private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(@Nonnull SimpleParticleType type, @Nonnull ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ParticleReplicator particle = new ParticleReplicator(world, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(this.spriteSet);
            return particle;
        }
		
	}

}
