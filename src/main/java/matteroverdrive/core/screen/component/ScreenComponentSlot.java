package matteroverdrive.core.screen.component;

import java.awt.Rectangle;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.References;
import matteroverdrive.core.screen.IScreenWrapper;
import matteroverdrive.core.screen.component.ScreenComponentIcon.IconType;
import matteroverdrive.core.screen.component.utils.ScreenComponent;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.resources.ResourceLocation;

public class ScreenComponentSlot extends ScreenComponent {

	private final SlotType type;
	private int color = UtilsRendering.getRGBA(255, 255, 255, 255);
	private static final String BASE_TEXTURE_LOC = References.ID + ":textures/gui/slot/";

	public ScreenComponentSlot(final SlotType type, final IScreenWrapper gui, final int x, final int y) {
		super(new ResourceLocation(BASE_TEXTURE_LOC  + type.getName()), gui, x, y);
		this.type = type;
	}
	
	public ScreenComponentSlot withIcon(IconType type) {
		
	}

	@Override
	public Rectangle getBounds(final int guiWidth, final int guiHeight) {
		return new Rectangle(guiWidth + xLocation, guiHeight + yLocation, type.getWidth(), type.getHeight());
	}

	@Override
	public void renderBackground(PoseStack stack, final int xAxis, final int yAxis, final int guiWidth,
			final int guiHeight) {
		UtilsRendering.bindTexture(resource);
		UtilsRendering.color(color);
		gui.drawTexturedRect(stack, guiWidth + xLocation, guiHeight + yLocation, type.getTextureX(), type.getTextureY(),
				type.getWidth(), type.getHeight());
		UtilsRendering.color(UtilsRendering.getRGBA(255, 255, 255, 255));
	}

	public enum SlotType {
		SMALL("slot_small"),
		BIG(22, 22, 0, 0, "slot_big"),
		BIG_DARK(22, 22, 0, 0, "slot_big_dark"),
		HOLO("slot_holo"),
		HOLO_BG("slot_holo_with_bg"),
		MAIN(37, 22, 0, 0, "slot_big_main"),
		MAIN_DARK(37, 22, 0, 0, "slot_big_main_dark"),
		MAIN_ACTIVE(37, 22, 0, 0, "slot_big_main_active"),
		VANILLA("slot_vanilla");

		private final int width;
		private final int height;
		private final int textureX;
		private final int textureY;
		private final String name;

		private SlotType(int width, int height, int textureX, int textureY, String name) {
			this.width = width;
			this.height = height;
			this.textureX = textureX;
			this.textureY = textureY;
			this.name = name + ".png";
		}
		
		private SlotType(String name) {
			this(18, 18, 0, 0, name);
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
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
