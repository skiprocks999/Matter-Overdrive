package matteroverdrive.core.utils;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.resources.ResourceLocation;

public class UtilsRendering {

	public static final int TEXT_BLUE = getRGBA(1, 169, 226, 251);
	public static final int TITLE_BLUE = getRGBA(1, 191, 228, 230);

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

}
