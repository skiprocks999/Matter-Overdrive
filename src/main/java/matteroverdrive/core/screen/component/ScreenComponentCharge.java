package matteroverdrive.core.screen.component;

import java.awt.Rectangle;
import java.util.function.DoubleSupplier;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.References;
import matteroverdrive.core.screen.IScreenWrapper;
import matteroverdrive.core.screen.component.utils.ScreenComponent;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.resources.ResourceLocation;

public class ScreenComponentCharge extends ScreenComponent {

	private boolean isMatter = false;

	private final int matterHeight = 42;
	private final int matterWidth = 14;
	private final int energyHeight = 42;
	private final int energyWidth = 14;

	private final DoubleSupplier progress;

	public ScreenComponentCharge(final DoubleSupplier progress, final IScreenWrapper gui, final int x, final int y, final int[] screenNumbers) {
		super(new ResourceLocation(References.ID + ":textures/gui/progress/progress.png"), gui, x, y, screenNumbers);
		this.progress = progress;
	}

	public ScreenComponentCharge setMatter() {
		isMatter = true;
		return this;
	}

	@Override
	public Rectangle getBounds(int guiWidth, int guiHeight) {
		return new Rectangle(guiWidth + xLocation, guiHeight + yLocation, isMatter ? matterWidth : energyWidth,
				isMatter ? matterHeight : energyHeight);
	}

	@Override
	public void renderBackground(PoseStack stack, final int xAxis, final int yAxis, final int guiWidth,
			final int guiHeight) {
		UtilsRendering.bindTexture(resource);
		if (isMatter) {

			int height = (int) (progress.getAsDouble() * matterHeight);
			int offset = matterHeight - height;
			gui.drawTexturedRect(stack, guiWidth + xLocation, guiHeight + yLocation, 0, energyWidth * 2, energyWidth,
					energyHeight);
			gui.drawTexturedRect(stack, guiWidth + xLocation, guiHeight + yLocation + offset, energyWidth * 3, offset,
					energyWidth, height);

		} else {

			int height = (int) (progress.getAsDouble() * energyHeight);
			int offset = energyHeight - height;
			gui.drawTexturedRect(stack, guiWidth + xLocation, guiHeight + yLocation, 0, 0, energyWidth, energyHeight);
			gui.drawTexturedRect(stack, guiWidth + xLocation, guiHeight + yLocation + offset, energyWidth, offset,
					energyWidth, height);

		}
	}

}
