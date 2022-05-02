package matteroverdrive.core.screen.component;

import java.awt.Rectangle;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.References;
import matteroverdrive.core.screen.IScreenWrapper;
import matteroverdrive.core.screen.component.utils.ScreenComponent;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.resources.ResourceLocation;

public class ScreenComponentIcon extends ScreenComponent {

	private IconType type;
	private int color = UtilsRendering.getRGBA(255, 255, 255, 255);
	private static final String BASE_TEXTURE_LOC = References.ID + ":textures/gui/icon/";

	public ScreenComponentIcon(final IconType type, final IScreenWrapper gui, final int x, final int y,
			final int[] screenNumbers) {
		super(new ResourceLocation(BASE_TEXTURE_LOC + type.getName()), gui, x, y, screenNumbers);
		this.type = type;
	}

	@Override
	public Rectangle getBounds(int guiWidth, int guiHeight) {
		return new Rectangle(guiWidth + xLocation, guiHeight + yLocation, type.getTextWidth(), type.getTextHeight());
	}

	@Override
	public void renderBackground(PoseStack stack, final int xAxis, final int yAxis, final int guiWidth,
			final int guiHeight) {
		UtilsRendering.bindTexture(resource);
		UtilsRendering.color(color);
		gui.drawTexturedRect(stack, guiWidth + xLocation, guiHeight + yLocation, type.getTextureX(), type.getTextureY(),
				type.getTextWidth(), type.getTextHeight(), type.getTextWidth(), type.getTextHeight());
		UtilsRendering.color(color);
	}

	public IconType getType() {
		return type;
	}

	public enum IconType {

		PAGE_WRENCH("page_icon_config"), PAGE_HOME(14, 14, "page_icon_home"),
		PAGE_UPGRADES(12, 12, "page_icon_upgrades"), PAGE_GEAR("page_icon_gear"),

		UPGRADE_DARK("upgrade"), MATTER_DARK("matter"), MATTER_LIGHT("matter_white"), MATTER_DUST_DARK("matter_dust"),
		ENERGY_DARK("energy"), ENERGY_LIGHT("energy_white"), BLOCK_DARK("decompose"), BLOCK_LIGHT("decompose_white");

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

		private IconType(int width, int height, String name) {
			this(width, height, 0, 0, name);
		}

		private IconType(String name) {
			this(16, 16, 0, 0, name);
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

		public String getTextureLoc() {
			return BASE_TEXTURE_LOC + getName();
		}
	}

}
