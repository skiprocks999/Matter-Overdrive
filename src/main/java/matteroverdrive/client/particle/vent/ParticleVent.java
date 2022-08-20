package matteroverdrive.client.particle.vent;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;

public class ParticleVent extends TextureSheetParticle {

	private SpriteSet sprites;

	public ParticleVent(ClientLevel world, double x, double y, double z, double xVel, double yVel, double zVel,
			float scale, float alpha, SpriteSet sprites) {
		super(world, x, y, z, 0, 0, 0);
		this.xd = xVel;
		this.yd = yVel;
		this.zd = zVel;
		this.rCol = this.gCol = this.bCol = 1 - random.nextFloat() * 0.3f;
		this.alpha = alpha;
		this.quadSize = scale;
		this.lifetime = (int) (8.0D / (random.nextDouble() * 0.8D + 0.2D));
		this.sprites = sprites;
		this.setSpriteFromAge(sprites);
		this.hasPhysics = false;
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		if (this.age++ >= this.lifetime) {
			this.remove();
		}

		setSpriteFromAge(sprites);
		this.move(this.xd, this.yd, this.zd);

		this.xd *= 0.9599999785423279D;
		this.yd *= 0.9599999785423279D;
		this.zd *= 0.9599999785423279D;

	}

	public float getQuadSize(float scale) {
		return this.quadSize * Mth.clamp(((float) this.age + scale) / (float) this.lifetime * 32.0F, 0.0F, 1.0F);
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	public static class Factory implements ParticleProvider<ParticleOptionVent> {

		private final SpriteSet spriteSet;

		public Factory(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public Particle createParticle(ParticleOptionVent type, ClientLevel world, double x, double y, double z,
				double xVel, double yVel, double zVel) {
			return new ParticleVent(world, x, y, z, xVel, yVel, zVel, type.scale, type.alpha, spriteSet);
		}

	}

}
