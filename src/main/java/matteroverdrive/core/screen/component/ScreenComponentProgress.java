package matteroverdrive.core.screen.component;

import java.awt.Rectangle;
import java.util.function.DoubleSupplier;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.References;
import matteroverdrive.core.screen.IScreenWrapper;
import matteroverdrive.core.screen.component.utils.ScreenComponent;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.resources.ResourceLocation;

public class ScreenComponentProgress extends ScreenComponent {

	private final DoubleSupplier progress;

	private static final int WIDTH = 22;
	private static final int HEIGHT = 16;

	private static final int BASE_X = 56;
	private static final int BASE_Y = 0;

	public ScreenComponentProgress(final DoubleSupplier progress, final IScreenWrapper gui, final int x, final int y,
			final int[] screenNumbers) {
		super(new ResourceLocation(References.ID + ":textures/gui/progress/progress.png"), gui, x, y, screenNumbers);
		this.progress = progress;
	}

	@Override
	public Rectangle getBounds(int guiWidth, int guiHeight) {
		return new Rectangle(guiWidth + xLocation, guiHeight + yLocation, WIDTH, HEIGHT);
	}

	@Override
	public void renderBackground(PoseStack stack, final int xAxis, final int yAxis, final int guiWidth,
			final int guiHeight) {
		UtilsRendering.bindTexture(resource);
		double progress = Math.min(1.0, this.progress.getAsDouble());

		int width = (int) (progress * WIDTH);
		gui.drawTexturedRect(stack, guiWidth + xLocation, guiHeight + yLocation, BASE_X, BASE_Y, WIDTH, HEIGHT);
		gui.drawTexturedRect(stack, guiWidth + xLocation, guiHeight + yLocation, BASE_X + WIDTH, BASE_Y, width, HEIGHT);

	}

}
