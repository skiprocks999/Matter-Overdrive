package matteroverdrive.client.particle.shockwave;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.VertexConsumer;

import matteroverdrive.client.animation.AnimationUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.world.phys.AABB;

public class ParticleShockwave extends TextureSheetParticle {

	private float maxScale;

	public ParticleShockwave(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed,
			double pZSpeed) {
		super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
		this.xd = 0;
		this.yd = 0;
		this.zd = 0;
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		if (this.age++ >= this.lifetime) {
			this.remove();
		}

		this.quadSize = AnimationUtils.Quart.easeOut((float) this.age / (float) this.lifetime, 0, 1, 1) * maxScale;
		updateBoundingBox();
	}

	@Override
	public void move(double pX, double pY, double pZ) {

	}

	@Override
	public void render(VertexConsumer builder, Camera camera, float partialTicks) {
		float particleAge = 1f - (float) this.age / (float) this.lifetime;
		float r = this.rCol * particleAge;
		float g = this.gCol * particleAge;
		float b = this.bCol * particleAge;
		float a = this.alpha * particleAge;
		int i = this.getLightColor(partialTicks);
		int j = i >> 16 & 65535;
		int k = i & 65535;
		
		float vMin = getV0();
		float vMax = getV1();
		float uMin = getU0();
		float uMax = getU1();

		AABB box = getBoundingBox();
		
		float minX = (float) box.minX;
		float minY = (float) box.minY;
		float minZ = (float) box.minZ;
		float maxX = (float) box.maxX;
		float maxY = (float) box.maxY;
		float maxZ = (float) box.maxZ;
		
		int u = sprite.getX();
		int v = sprite.getY();

		// bottom
		builder.vertex(minX, minY, minZ).color(r, g, b, a).uv(uMin, vMin).uv2(j, k).normal(0, -1, 0).overlayCoords(u, v).endVertex();
		builder.vertex(maxX, minY, minZ).color(r, g, b, a).uv(uMax, vMin).uv2(j, k).normal(0, -1, 0).overlayCoords(u, v).endVertex();
		builder.vertex(maxX, minY, maxZ).color(r, g, b, a).uv(uMax, vMax).uv2(j, k).normal(0, -1, 0).overlayCoords(u, v).endVertex();
		builder.vertex(minX, minY, maxZ).color(r, g, b, a).uv(uMin, vMax).uv2(j, k).normal(0, -1, 0).overlayCoords(u, v).endVertex();

		// top
		builder.vertex(maxX, maxY, minZ).color(r, g, b, a).uv(uMin, vMin).uv2(j, k).normal(0, 1, 0).overlayCoords(u, v).endVertex();
		builder.vertex(minX, maxY, minZ).color(r, g, b, a).uv(uMax, vMin).uv2(j, k).normal(0, 1, 0).overlayCoords(u, v).endVertex();
		builder.vertex(minX, maxY, maxZ).color(r, g, b, a).uv(uMax, vMax).uv2(j, k).normal(0, 1, 0).overlayCoords(u, v).endVertex();
		builder.vertex(maxX, maxY, maxZ).color(r, g, b, a).uv(uMin, vMax).uv2(j, k).normal(0, 1, 0).overlayCoords(u, v).endVertex();

	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	public void setParticleMaxAge(int maxAge) {
		this.lifetime = maxAge;
	}

	public void setMaxScale(float maxScale) {
		this.maxScale = maxScale;
	}

	public void updateBoundingBox() {
		this.setBoundingBox(new AABB(x - quadSize, y - 0.5, z - quadSize, x + quadSize, y + 0.5, z + quadSize));
	}

	public static class Factory implements ParticleProvider<ParticleOptionShockwave> {

		private final SpriteSet spriteSet;

		public Factory(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public Particle createParticle(@Nonnull ParticleOptionShockwave type, @Nonnull ClientLevel world, double x,
				double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			ParticleShockwave particle = new ParticleShockwave(world, x, y, z, xSpeed, ySpeed, zSpeed);
			particle.pickSprite(this.spriteSet);
			particle.setMaxScale(type.maxScale);
			particle.setParticleMaxAge((int) (type.maxScale * 5.0F));
			particle.setColor(type.r, type.g, type.b);
			particle.setAlpha(type.a);
			particle.updateBoundingBox();
			return particle;
		}

	}

}
