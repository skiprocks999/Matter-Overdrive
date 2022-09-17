package matteroverdrive.compatibility.jei.utils.gui;

import matteroverdrive.References;
import net.minecraft.resources.ResourceLocation;

public class ScreenObjectWrapper {

	public static final ResourceLocation VANILLA_BACKGROUND = 
			new ResourceLocation(References.ID, "textures/jei/vanilla_background.png");
	public static final ResourceLocation OVERDRIVE_BACKGROUND_BAR = 
			new ResourceLocation(References.ID, "textures/jei/overdrive_background_bar.png");
	
	public static final String FLUID_GAUGES = "fluidgauges";
	public static final ResourceLocation ARROWS = 
			new ResourceLocation(References.ID, "textures/gui/progress/progress.png");
	public static final ResourceLocation MISC = 
			new ResourceLocation(References.ID, "textures/jei/misc_resources.png");

	private ResourceLocation texture;

	private int xPos;
	private int yPos;

	private int textX;
	private int textY;
	private int length;
	private int width;

	public ScreenObjectWrapper(ResourceLocation texture, int xStart, int yStart, int textX, int textY, int height, int width) {
		this.texture = texture;

		xPos = xStart;
		yPos = yStart;

		this.textX = textX;
		this.textY = textY;
		length = height;
		this.width = width;
	}

	public int getXPos() {
		return xPos;
	}

	public int getYPos() {
		return yPos;
	}

	public int getTextX() {
		return textX;
	}

	public int getTextY() {
		return textY;
	}

	public int getLength() {
		return length;
	}

	public int getWidth() {
		return width;
	}
	
	public int getTextureWidth() {
		return 256;
	}
	
	public int getTextureHeight() {
		return 256;
	}

	public ResourceLocation getTexture() {
		return texture;
	}

}
