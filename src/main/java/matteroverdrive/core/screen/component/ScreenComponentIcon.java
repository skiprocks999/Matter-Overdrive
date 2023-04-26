package matteroverdrive.core.screen.component;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.References;
import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.utils.ITexture;
import matteroverdrive.core.screen.component.utils.OverdriveScreenComponent;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.resources.ResourceLocation;

public class ScreenComponentIcon extends OverdriveScreenComponent {

	private IconType type;
	private static final String BASE_TEXTURE_LOC = "textures/gui/icon/";

	public ScreenComponentIcon(@Nonnull IconType type, final GenericScreen<?> gui, final int x, final int y,
			final int[] screenNumbers) {
		super(type, gui, x, y, type.width, type.height, screenNumbers);
		this.type = type;
	}

	@Override
	public void renderBackground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		if (type != IconType.NONE) {
			UtilsRendering.bindTexture(resource.getTexture());
			UtilsRendering.resetShaderColor();
			blit(stack, this.x, this.y, type.getTextureU(), type.getTextureV(), type.getUWidth(),
					type.getVHeight(), type.getTextureWidth(), type.getTextureHeight());
		}
	}

	public IconType getType() {
		return type;
	}

	public static enum IconType implements ITexture {

		NONE(""),

		PAGE_WRENCH("page_icon_config"), PAGE_HOME(14, 14, "page_icon_home"),
		PAGE_UPGRADES(12, 12, "page_icon_upgrades"), PAGE_GEAR("page_icon_gear"), PAGE_TASKS(15, 15, "page_icon_tasks"),

		UPGRADE_DARK("upgrade"), MATTER_DARK("matter"), MATTER_LIGHT("matter_white"), MATTER_DUST_DARK("matter_dust"),
		ENERGY_DARK("energy"), ENERGY_LIGHT("energy_white"), BLOCK_DARK("decompose"), BLOCK_LIGHT("decompose_white"),
		FLASHDRIVE_DARK("flash_drive"), PENCIL_DARK("pencil"), PATTERN_DRIVE_DARK("pattern_drive"),
		MATTER_SCANNER_DARK("matter_scanner"), SHIELDING_DARK("shielding"), CONNECTIONS("connections"), ERASER_DARK("eraser"),
		ERASER_LIGHT("eraser_white"), COMMUNICATOR_DARK("communicator_icon");

		private final int width;
		private final int height;
		private final int textureX;
		private final int textureY;
		private final ResourceLocation texture;

		private IconType(int width, int height, int textureX, int textureY, String name) {
			this.width = width;
			this.height = height;
			this.textureX = textureX;
			this.textureY = textureY;
			this.texture = new ResourceLocation(References.ID, BASE_TEXTURE_LOC + name + ".png");
		}

		private IconType(int width, int height, String name) {
			this(width, height, 0, 0, name);
		}

		private IconType(String name) {
			this(16, 16, 0, 0, name);
		}

		@Override
		public ResourceLocation getTexture() {
			return texture;
		}

		public int getTextureU() {
			return textureX;
		}

		public int getTextureV() {
			return textureY;
		}

		public int getUWidth() {
			return width;
		}

		public int getVHeight() {
			return height;
		}

		@Override
		public int getTextureWidth() {
			return width;
		}

		@Override
		public int getTextureHeight() {
			return height;
		}
	}

}
