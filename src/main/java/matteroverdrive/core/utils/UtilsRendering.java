package matteroverdrive.core.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;

import matteroverdrive.References;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

public class UtilsRendering {

	public static final int TEXT_BLUE = getRGBA(255, 169, 226, 251);
	public static final int TITLE_BLUE = getRGBA(255, 191, 228, 230);
	public static final int GUI_STANDARD = getRGBA(255, 62, 81, 84);
	public static final int GUI_DARK = getRGBA(255, 44, 54, 52);
	public static final int RED = getRGBA(255, 255, 0, 0);
	public static final int GREEN = getRGBA(255, 0, 255, 0);
	public static final int WHITE = getRGBA(255, 255, 255, 255);
	public static final int HOLO_RED = getRGBA(255, 230, 80, 20);
	public static final int HOLO_GREEN = getRGBA(255, 24, 207, 0);

	public static final float[] FLOAT_TEXT_BLUE = UtilsRendering.getColorArray(UtilsRendering.TEXT_BLUE);
	public static final float[] FLOAT_HOLO_RED = UtilsRendering.getColorArray(UtilsRendering.HOLO_RED);
	public static final float[] FLOAT_HOLO_GREEN = UtilsRendering.getColorArray(UtilsRendering.HOLO_GREEN);

	public static final AABB AABB_BLOCK = new AABB(0, 0, 0, 1, 1, 1);

	public static void bindTexture(ResourceLocation resource) {
		RenderSystem.setShaderTexture(0, resource);
	}

	public static float getRed(int color) {
		return (color >> 16 & 0xFF) / 255.0F;
	}

	public static float getGreen(int color) {
		return (color >> 8 & 0xFF) / 255.0F;
	}

	public static float getBlue(int color) {
		return (color & 0xFF) / 255.0F;
	}

	public static float getAlpha(int color) {
		return (color >> 24 & 0xFF) / 255.0F;
	}

	public static int getRGBA(int a, int r, int g, int b) {
		return (a << 24) + (r << 16) + (g << 8) + b;
	}

	public static float[] getColorArray(int color) {
		return new float[] { getRed(color), getGreen(color), getBlue(color), getAlpha(color) };
	}

	public static void color(int color) {
		RenderSystem.setShaderColor(getRed(color), getGreen(color), getBlue(color), getAlpha(color));
	}

	public static void resetColor() {
		color(WHITE);
	}

	public static BakedModel getBakedModel(ResourceLocation model) {
		return Minecraft.getInstance().getModelManager().getModel(model);
	}

	public static void renderModel(BakedModel model, BlockEntity tile, RenderType type, PoseStack stack,
			MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
		Minecraft.getInstance().getItemRenderer().render(
				new ItemStack(type == RenderType.translucent() ? Items.BLACK_STAINED_GLASS : Blocks.STONE),
				TransformType.NONE, false, stack, buffer, combinedLightIn, combinedOverlayIn, model);

	}

	public static void renderItem(ItemStack stack, int light, int overlay, PoseStack pose, MultiBufferSource buffer) {
		Minecraft.getInstance().getItemRenderer().renderStatic(stack, TransformType.NONE, light, overlay, pose, buffer,
				0);
	}

	public static void renderFilledBoxNoOverlay(PoseStack stack, VertexConsumer builder, AABB box, float[] color,
			float[] uv, int light) {
		renderFilledBox(stack, builder, box, color, uv, light, OverlayTexture.NO_OVERLAY);
	}

	public static void renderFilledBox(PoseStack stack, VertexConsumer builder, AABB box, float[] color, float[] uv,
			int light, int overlay) {
		Matrix4f matrix4f = stack.last().pose();
		Matrix3f matrix3f = stack.last().normal();

		float[] coords = getCoordsFromAABB(box);

		renderBottomOfBox(builder, coords, color, uv, matrix4f, matrix3f, light, overlay);

		renderTopOfBox(builder, coords, color, uv, matrix4f, matrix3f, light, overlay);

		renderNorthOfBox(builder, coords, color, uv, matrix4f, matrix3f, light, overlay);

		renderSouthOfBox(builder, coords, color, uv, matrix4f, matrix3f, light, overlay);

		renderEastOfBox(builder, coords, color, uv, matrix4f, matrix3f, light, overlay);

		renderWestOfBox(builder, coords, color, uv, matrix4f, matrix3f, light, overlay);

	}

	/*
	 * 
	 * float[] color = rgba float[] uv = uMin uMax vMin vMax float[] coords = xMin
	 * xMax yMin yMax zMin zMax
	 * 
	 */

	public static void renderBottomOfBox(VertexConsumer builder, float[] coords, float[] color, float[] uv,
			Matrix4f matrix4f, Matrix3f matrix3f, int light, int overlay) {
		builder.vertex(matrix4f, coords[0], coords[2], coords[4]).color(color[0], color[1], color[2], color[3])
				.uv(uv[0], uv[2]).overlayCoords(overlay).uv2(light).normal(matrix3f, 0, -1, 0).endVertex();
		builder.vertex(matrix4f, coords[1], coords[2], coords[4]).color(color[0], color[1], color[2], color[3])
				.uv(uv[1], uv[2]).overlayCoords(overlay).uv2(light).normal(matrix3f, 0, -1, 0).endVertex();
		builder.vertex(matrix4f, coords[1], coords[2], coords[5]).color(color[0], color[1], color[2], color[3])
				.uv(uv[1], uv[3]).overlayCoords(overlay).uv2(light).normal(matrix3f, 0, -1, 0).endVertex();
		builder.vertex(matrix4f, coords[0], coords[2], coords[5]).color(color[0], color[1], color[2], color[3])
				.uv(uv[0], uv[3]).overlayCoords(overlay).uv2(light).normal(matrix3f, 0, -1, 0).endVertex();
	}

	public static void renderTopOfBox(VertexConsumer builder, float[] coords, float[] color, float[] uv,
			Matrix4f matrix4f, Matrix3f matrix3f, int light, int overlay) {
		builder.vertex(matrix4f, coords[1], coords[3], coords[4]).color(color[0], color[1], color[2], color[3])
				.uv(uv[0], uv[2]).overlayCoords(overlay).uv2(light).normal(matrix3f, 0, 1, 0).endVertex();
		builder.vertex(matrix4f, coords[0], coords[3], coords[4]).color(color[0], color[1], color[2], color[3])
				.uv(uv[1], uv[2]).overlayCoords(overlay).uv2(light).normal(matrix3f, 0, 1, 0).endVertex();
		builder.vertex(matrix4f, coords[0], coords[3], coords[5]).color(color[0], color[1], color[2], color[3])
				.uv(uv[1], uv[3]).overlayCoords(overlay).uv2(light).normal(matrix3f, 0, 1, 0).endVertex();
		builder.vertex(matrix4f, coords[1], coords[3], coords[5]).color(color[0], color[1], color[2], color[3])
				.uv(uv[0], uv[3]).overlayCoords(overlay).uv2(light).normal(matrix3f, 0, 1, 0).endVertex();
	}

	public static void renderNorthOfBox(VertexConsumer builder, float[] coords, float[] color, float[] uv,
			Matrix4f matrix4f, Matrix3f matrix3f, int light, int overlay) {
		builder.vertex(matrix4f, coords[0], coords[3], coords[4]).color(color[0], color[1], color[2], color[3])
				.uv(uv[0], uv[2]).overlayCoords(overlay).uv2(light).normal(matrix3f, 0, 0, -1).endVertex();
		builder.vertex(matrix4f, coords[1], coords[3], coords[4]).color(color[0], color[1], color[2], color[3])
				.uv(uv[1], uv[2]).overlayCoords(overlay).uv2(light).normal(matrix3f, 0, 0, -1).endVertex();
		builder.vertex(matrix4f, coords[1], coords[2], coords[4]).color(color[0], color[1], color[2], color[3])
				.uv(uv[1], uv[3]).overlayCoords(overlay).uv2(light).normal(matrix3f, 0, 0, -1).endVertex();
		builder.vertex(matrix4f, coords[0], coords[2], coords[4]).color(color[0], color[1], color[2], color[3])
				.uv(uv[0], uv[3]).overlayCoords(overlay).uv2(light).normal(matrix3f, 0, 0, -1).endVertex();
	}

	public static void renderSouthOfBox(VertexConsumer builder, float[] coords, float[] color, float[] uv,
			Matrix4f matrix4f, Matrix3f matrix3f, int light, int overlay) {
		builder.vertex(matrix4f, coords[1], coords[3], coords[5]).color(color[0], color[1], color[2], color[3])
				.uv(uv[0], uv[2]).overlayCoords(overlay).uv2(light).normal(matrix3f, 0, 0, 1).endVertex();
		builder.vertex(matrix4f, coords[0], coords[3], coords[5]).color(color[0], color[1], color[2], color[3])
				.uv(uv[1], uv[2]).overlayCoords(overlay).uv2(light).normal(matrix3f, 0, 0, 1).endVertex();
		builder.vertex(matrix4f, coords[0], coords[2], coords[5]).color(color[0], color[1], color[2], color[3])
				.uv(uv[1], uv[3]).overlayCoords(overlay).uv2(light).normal(matrix3f, 0, 0, 1).endVertex();
		builder.vertex(matrix4f, coords[1], coords[2], coords[5]).color(color[0], color[1], color[2], color[3])
				.uv(uv[0], uv[3]).overlayCoords(overlay).uv2(light).normal(matrix3f, 0, 0, 1).endVertex();
	}

	public static void renderEastOfBox(VertexConsumer builder, float[] coords, float[] color, float[] uv,
			Matrix4f matrix4f, Matrix3f matrix3f, int light, int overlay) {
		builder.vertex(matrix4f, coords[1], coords[3], coords[4]).color(color[0], color[1], color[2], color[3])
				.uv(uv[0], uv[2]).overlayCoords(overlay).uv2(light).normal(matrix3f, 1, 0, 0).endVertex();
		builder.vertex(matrix4f, coords[1], coords[3], coords[5]).color(color[0], color[1], color[2], color[3])
				.uv(uv[1], uv[2]).overlayCoords(overlay).uv2(light).normal(matrix3f, 1, 0, 0).endVertex();
		builder.vertex(matrix4f, coords[1], coords[2], coords[5]).color(color[0], color[1], color[2], color[3])
				.uv(uv[1], uv[3]).overlayCoords(overlay).uv2(light).normal(matrix3f, 1, 0, 0).endVertex();
		builder.vertex(matrix4f, coords[1], coords[2], coords[4]).color(color[0], color[1], color[2], color[3])
				.uv(uv[0], uv[3]).overlayCoords(overlay).uv2(light).normal(matrix3f, 1, 0, 0).endVertex();
	}

	public static void renderWestOfBox(VertexConsumer builder, float[] coords, float[] color, float[] uv,
			Matrix4f matrix4f, Matrix3f matrix3f, int light, int overlay) {
		builder.vertex(matrix4f, coords[0], coords[3], coords[5]).color(color[0], color[1], color[2], color[3])
				.uv(uv[0], uv[2]).overlayCoords(overlay).uv2(light).normal(matrix3f, -1, 0, 0).endVertex();
		builder.vertex(matrix4f, coords[0], coords[3], coords[4]).color(color[0], color[1], color[2], color[3])
				.uv(uv[1], uv[2]).overlayCoords(overlay).uv2(light).normal(matrix3f, -1, 0, 0).endVertex();
		builder.vertex(matrix4f, coords[0], coords[2], coords[4]).color(color[0], color[1], color[2], color[3])
				.uv(uv[1], uv[3]).overlayCoords(overlay).uv2(light).normal(matrix3f, -1, 0, 0).endVertex();
		builder.vertex(matrix4f, coords[0], coords[2], coords[5]).color(color[0], color[1], color[2], color[3])
				.uv(uv[0], uv[3]).overlayCoords(overlay).uv2(light).normal(matrix3f, -1, 0, 0).endVertex();
	}

	public static float[] getCoordsFromAABB(AABB box) {
		return new float[] { (float) box.minX, (float) box.maxX, (float) box.minY, (float) box.maxY, (float) box.minZ,
				(float) box.maxZ };
	}

	public static RenderType beaconType() {
		return RenderType.beaconBeam(new ResourceLocation("textures/entity/beacon_beam.png"), true);
	}
	
	public static class BlockTextures {
		
		public static final ResourceLocation BASE = moBlock("base");
		
		private static ResourceLocation moLoc(String texture) {
			return new ResourceLocation(References.ID, texture);
		}
		
		private static ResourceLocation moBlock(String texture) {
			return moLoc("block/" + texture);
		}
		
	}

}
