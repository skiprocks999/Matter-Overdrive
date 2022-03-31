package matteroverdrive.core.screen.component;

import java.awt.Rectangle;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.References;
import matteroverdrive.core.screen.IScreenWrapper;
import matteroverdrive.core.screen.component.utils.ScreenComponent;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.resources.ResourceLocation;

public class ScreenComponentSlot extends ScreenComponent {

	private final SlotType type;
	private int color = UtilsRendering.getRGBA(255, 255, 255, 255);

	public ScreenComponentSlot(final SlotType type, final IScreenWrapper gui, final int x, final int y) {
		super(new ResourceLocation(References.ID + ":textures/screen/component/slot.png"), gui, x, y);
		this.type = type;
	}

	@Override
	public Rectangle getBounds(final int guiWidth, final int guiHeight) {
		return new Rectangle(guiWidth + xLocation, guiHeight + yLocation, type.getWidth(), type.getHeight());
	}

	@Override
	public void renderBackground(PoseStack stack, final int xAxis, final int yAxis, final int guiWidth, final int guiHeight) {
		UtilsRendering.bindTexture(resource);
		UtilsRendering.color(color);
		gui.drawTexturedRect(stack, guiWidth + xLocation, guiHeight + yLocation, type.getTextureX(), type.getTextureY(), type.getWidth(), type.getHeight());
		UtilsRendering.color(UtilsRendering.getRGBA(255, 255, 255, 255));
	}

	public enum SlotType {
		GENERIC(0,0,0,0);

		private final int width;
		private final int height;
		private final int textureX;
		private final int textureY;

		SlotType(int width, int height, int textureX, int textureY) {
			this.width = width;
			this.height = height;
			this.textureX = textureX;
			this.textureY = textureY;
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
	}

}
