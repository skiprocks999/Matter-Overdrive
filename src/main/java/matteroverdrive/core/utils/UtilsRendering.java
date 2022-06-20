package matteroverdrive.core.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;

public class UtilsRendering {

	public static final int TEXT_BLUE = getRGBA(255, 169, 226, 251);
	public static final int TITLE_BLUE = getRGBA(255, 191, 228, 230);
	public static final int GUI_STANDARD = getRGBA(255, 62, 81, 84);
	public static final int GUI_DARK = getRGBA(255, 44, 54, 52);
	public static final int RED = getRGBA(255, 255, 0, 0);
	public static final int GREEN = getRGBA(255, 0, 255, 0);
	public static final int WHITE = getRGBA(255, 255, 255, 255);

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

}
