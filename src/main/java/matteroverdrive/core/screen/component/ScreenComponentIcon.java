package matteroverdrive.core.screen.component;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.References;
import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.utils.OverdriveScreenComponent;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.resources.ResourceLocation;

public class ScreenComponentIcon extends OverdriveScreenComponent {

	private IconType type;
	private int color = UtilsRendering.getRGBA(255, 255, 255, 255);
	private static final String BASE_TEXTURE_LOC = References.ID + ":textures/gui/icon/";

	public ScreenComponentIcon(final IconType type, final GenericScreen<?> gui, final int x, final int y,
			final int[] screenNumbers) {
		super(new ResourceLocation(BASE_TEXTURE_LOC + type.getName()), gui, x, y, type.width, type.height, screenNumbers);
		this.type = type;
	}


	@Override
	public void renderBackground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		UtilsRendering.bindTexture(resource);
		UtilsRendering.color(color);
		blit(stack, this.x, this.y, type.getTextureX(), type.getTextureY(),
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
		ENERGY_DARK("energy"), ENERGY_LIGHT("energy_white"), BLOCK_DARK("decompose"), BLOCK_LIGHT("decompose_white"),
		FLASHDRIVE_DARK("flash_drive"), PENCIL_DARK("pencil");

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
