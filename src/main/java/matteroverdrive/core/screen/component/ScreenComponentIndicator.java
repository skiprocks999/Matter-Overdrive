package matteroverdrive.core.screen.component;

import java.awt.Rectangle;
import java.util.function.BooleanSupplier;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.References;
import matteroverdrive.core.screen.IScreenWrapper;
import matteroverdrive.core.screen.component.utils.ScreenComponent;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.resources.ResourceLocation;

public class ScreenComponentIndicator extends ScreenComponent {

	private final BooleanSupplier active;
	private boolean isRed = false;

	private final int baseWidth = 21;
	private final int blueWidth = 21;
	private final int redWidth = 21;
	private final int baseHeight = 5;
	private final int blueHeight = 5;
	private final int redHeight = 5;

	public ScreenComponentIndicator(final BooleanSupplier supplier, final IScreenWrapper gui, final int x,
			final int y, final int[] screenNumbers) {
		super(new ResourceLocation(References.ID + ":textures/gui/base/indicator.png"), gui, x, y, screenNumbers);
		active = supplier;
	}

	public ScreenComponentIndicator setRed() {
		isRed = true;
		return this;
	}

	@Override
	public Rectangle getBounds(int guiWidth, int guiHeight) {
		return new Rectangle(guiWidth + xLocation, guiHeight + yLocation, baseWidth, baseHeight);
	}

	@Override
	public void renderBackground(PoseStack stack, int xAxis, int yAxis, int guiWidth, int guiHeight) {
		UtilsRendering.bindTexture(resource);
		gui.drawTexturedRect(stack, guiWidth + xLocation, guiHeight + yLocation, 0, 0, baseWidth, baseHeight);

		if (active.getAsBoolean()) {
			if (isRed) {
				gui.drawTexturedRect(stack, guiWidth + xLocation, guiHeight + yLocation, 0, baseHeight + blueHeight,
						redWidth, redHeight);
			} else {
				gui.drawTexturedRect(stack, guiWidth + xLocation, guiHeight + yLocation, 0, baseHeight, blueWidth,
						blueHeight);
			}
		}

	}

}
