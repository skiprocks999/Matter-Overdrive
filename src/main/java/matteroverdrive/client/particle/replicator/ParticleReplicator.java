package matteroverdrive.client.particle.replicator;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;

public class ParticleReplicator extends TextureSheetParticle {

	private float initialScale;
	public ParticleReplicator(ClientLevel world, double posX, double posY, double posZ, double xSpeed, double ySpeed,
			double zSpeed) {
		super(world, posX, posY, posZ, xSpeed, ySpeed, zSpeed);
		this.xd = 0;
		this.zd = 0;
		this.gravity = 1.0F;
		this.quadSize = 0.1F;
		this.initialScale = quadSize;
		this.rCol = this.gCol = this.bCol = 1.0F;
		this.lifetime = (int) (8.0D / (Math.random() * 0.8D + 0.2D)) + 4;
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		if (this.age++ >= this.lifetime) {
			this.remove();
		}

		move(this.zd, this.yd, this.zd);
	}

	@Override
	public void render(VertexConsumer pBuffer, Camera pRenderInfo, float partialTicks) {
		float f6 = ((float) this.age + partialTicks) / (float) this.lifetime;
		this.quadSize = this.initialScale * (1.0F - f6 * f6 * 0.5F);
		super.render(pBuffer, pRenderInfo, partialTicks);
	}

	@Override
	public void move(double pX, double pY, double pZ) {
		this.setBoundingBox(this.getBoundingBox().move(pX, pY, pZ));
		this.setLocationFromBoundingbox();
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	protected int getLightColor(float pPartialTick) {
		float f1 = Mth.clamp(((float) this.age + pPartialTick) / (float) this.lifetime, 0.0F, 1.0F);

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

		float f1 = Mth.clamp(((float) this.age + light) / (float) this.lifetime, 0.0F, 1.0F);
		int f2 = super.getLightColor(light);

		return (int) (f2 * f1 + (1.0F - f1));
	}

	public void setCenter(double x, double y, double z) {

	}

	public void setParticleMaxAge(int age) {
		this.lifetime = age;
	}

	public void setGravity(float gravity) {
		this.gravity = gravity;
	}

	public static class Factory implements ParticleProvider<ParticleOptionReplicator> {

		private final SpriteSet spriteSet;

		public Factory(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public Particle createParticle(@Nonnull ParticleOptionReplicator type, @Nonnull ClientLevel world, double x,
				double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			ParticleReplicator particle = new ParticleReplicator(world, x, y, z, xSpeed, ySpeed, zSpeed);
			particle.pickSprite(this.spriteSet);
			particle.setCenter(type.xCenter, type.yCenter, type.zCenter);
			particle.setGravity(type.gravity);
			particle.setParticleMaxAge(type.age);
			return particle;
		}

	}

}
