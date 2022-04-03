package matteroverdrive.core.screen.component;

import java.awt.Rectangle;

import matteroverdrive.References;
import matteroverdrive.core.screen.IScreenWrapper;
import matteroverdrive.core.screen.component.utils.ScreenComponent;
import net.minecraft.resources.ResourceLocation;

public class ScreenComponentIcon extends ScreenComponent {

	private IconType type;
	private static final String BASE_TEXTURE_LOC = References.ID + ":textures/gui/icon/";
	
	public ScreenComponentIcon(final IconType type, final IScreenWrapper gui, final int x, final int y) {
		super(new ResourceLocation(BASE_TEXTURE_LOC  + type.getName()), gui, x, y);
		this.type = type;
	}

	@Override
	public Rectangle getBounds(int guiWidth, int guiHeight) {
		return new Rectangle(guiWidth + xLocation, guiHeight + yLocation, type.getTextWidth(), type.getTextHeight());
	}
	
	public enum IconType {
		
		;
		
		private final int width;
		private final int height;
		private final int textureX;
		private final int textureY;
		private final String name;

		private IconType(int width, int height, int textureX, int textureY, String name) {
			this.width = width;
			this.height = height;
			this.textureX = textureX;
			this.textureY = textureY;
			this.name = name + ".png";
		}
		
		private IconType(String name) {
			this(18, 18, 0, 0, name);
		}

		public int getTextWidth() {
			return width;
		}

		public int getTextHeight() {
			return height;
		}

		public int getTextureX() {
			return textureX;
		}

		public int getTextureY() {
			return textureY;
		}
		
		public String getName() {
			return name;
		}
	}

}
