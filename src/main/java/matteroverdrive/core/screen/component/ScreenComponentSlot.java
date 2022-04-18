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
	private ScreenComponentIcon icon = null;

	public ScreenComponentSlot(final SlotType type, final IScreenWrapper gui, final int x, final int y, final int[] screenNumbers) {
		super(new ResourceLocation(BASE_TEXTURE_LOC + type.getName()), gui, x, y, screenNumbers);
		this.type = type;
	}
	
	public ScreenComponentSlot(final SlotType type, final IconType icon, final IScreenWrapper gui, final int x, final int y, final int[] screenNumbers) {
		super(new ResourceLocation(BASE_TEXTURE_LOC + type.getName()), gui, x, y, screenNumbers);
		this.type = type;
		if(icon != null) {
			this.icon = new ScreenComponentIcon(icon, gui, this.xLocation, this.yLocation, this.screenNumbers);
		}
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
		gui.drawTexturedRect(stack, guiWidth + xLocation + type.getXOffset(), guiHeight + yLocation + type.getYOffset(), type.getTextureX(), type.getTextureY(),
				type.getWidth(), type.getHeight(), type.getWidth(), type.getHeight());
		UtilsRendering.color(UtilsRendering.getRGBA(255, 255, 255, 255));
		if (icon != null) {
			IconType iType = icon.getType();
			int widthOffset = (int) ((type.getWidth() - iType.getTextWidth()) / 2);
			int heightOffset = (int) ((type.getHeight() - iType.getTextHeight()) / 2);
			icon.renderBackground(stack, xAxis, yAxis, guiWidth + widthOffset + type.getXOffset(), guiHeight + heightOffset + type.getYOffset());
		}
	}

	public enum SlotType {
		SMALL("slot_small"), BIG(22, 22, 0, 0, "slot_big", -2, -2), BIG_DARK(22, 22, 0, 0, "slot_big_dark", -1, -1), HOLO("slot_holo"),
		HOLO_BG("slot_holo_with_bg"), MAIN(37, 22, 0, 0, "slot_big_main", 0, 0),
		MAIN_DARK(37, 22, 0, 0, "slot_big_main_dark", 0, 0), MAIN_ACTIVE(37, 22, 0, 0, "slot_big_main_active", 0, 0),
		VANILLA("slot_vanilla");

		private final int width;
		private final int height;
		private final int textureX;
		private final int textureY;
		private final int xOffset;
		private final int yOffset;
		private final String name;

		private SlotType(int width, int height, int textureX, int textureY, String name, int xOffset, int yOffset) {
			this.width = width;
			this.height = height;
			this.textureX = textureX;
			this.textureY = textureY;
			this.name = name + ".png";
			this.xOffset = xOffset;
			this.yOffset = yOffset;
		}

		private SlotType(String name) {
			this(18, 18, 0, 0, name, 0, 0);
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
		
		public int getXOffset() {
			return xOffset;
		}
		
		public int getYOffset() {
			return yOffset;
		}

		public String getTextureLoc() {
			return BASE_TEXTURE_LOC + getName();
		}

	}

}
