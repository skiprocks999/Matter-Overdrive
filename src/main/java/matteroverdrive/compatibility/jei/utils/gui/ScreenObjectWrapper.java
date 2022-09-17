package matteroverdrive.compatibility.jei.utils.gui;

import matteroverdrive.References;
import matteroverdrive.core.screen.component.utils.ITexture;
import net.minecraft.resources.ResourceLocation;

public class ScreenObjectWrapper {
	
	public static final String FLUID_GAUGES = "fluidgauges";

	private ITexture texture;

	private int xPos;
	private int yPos;

	private int textX;
	private int textY;
	private int length;
	private int width;

	public ScreenObjectWrapper(ITexture texture, int xStart, int yStart, int textX, int textY, int height, int width) {
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
		return texture.getTextureWidth();
	}
	
	public int getTextureHeight() {
		return texture.getTextureHeight();
	}

	public ResourceLocation getTexture() {
		return texture.getTexture();
	}
	
	public static enum JeiTexture implements ITexture {
		
		VANILLA_BACKGROUND(new ResourceLocation(References.ID, "textures/jei/vanilla_background.png"), 256, 256),
		OVERDRIVE_BACKGROUND(new ResourceLocation(References.ID, "textures/jei/overdrive_background.png"), 132, 19),
		OVERDRIVE_BACKGROUND_BAR(new ResourceLocation(References.ID, "textures/jei/overdrive_background_bar.png"), 132, 19),
		PROGRESS_BARS(new ResourceLocation(References.ID, "textures/gui/progress/progress.png"), 256, 256),
		MISC_TEXTURES(new ResourceLocation(References.ID, "textures/jei/misc_resources.png"), 256, 256);
		
		private final int textWidth;
		private final int textHeight;
		private final ResourceLocation texture;
		
		private JeiTexture(ResourceLocation texture, int textWidth, int textHeight) {
			this.texture = texture;
			this.textWidth = textWidth;
			this.textHeight = textHeight;
		}

		@Override
		public ResourceLocation getTexture() {
			return texture;
		}

		@Override
		public int getTextureWidth() {
			return textWidth;
		}

		@Override
		public int getTextureHeight() {
			return textHeight;
		}
	}

}
