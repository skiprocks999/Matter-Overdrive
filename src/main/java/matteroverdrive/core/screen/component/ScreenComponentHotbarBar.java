package matteroverdrive.core.screen.component;

import java.awt.Rectangle;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.References;
import matteroverdrive.core.screen.IScreenWrapper;
import matteroverdrive.core.screen.component.utils.ScreenComponent;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.resources.ResourceLocation;

public class ScreenComponentHotbarBar extends ScreenComponent {

	private final int height = 5;
	private final int width = 169;

	public ScreenComponentHotbarBar(final IScreenWrapper gui, final int x, final int y) {
		super(new ResourceLocation(References.ID + ":textures/gui/base/hotbar_bar.png"), gui, x, y);
	}

	@Override
	public Rectangle getBounds(int guiWidth, int guiHeight) {
		return new Rectangle(guiWidth + xLocation, guiHeight + yLocation, width, height);
	}

	@Override
	public void renderBackground(PoseStack stack, int xAxis, int yAxis, int guiWidth, int guiHeight) {
		UtilsRendering.bindTexture(resource);
		gui.drawTexturedRect(stack, guiWidth + xLocation, guiHeight + yLocation, 0, 0, width, height);
	}

}
