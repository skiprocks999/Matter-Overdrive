package matteroverdrive.core.screen.component;

import java.util.function.DoubleSupplier;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.client.ClientReferences.Colors;
import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.utils.OverdriveScreenComponent;
import matteroverdrive.core.utils.UtilsRendering;

public class ScreenComponentProgress extends OverdriveScreenComponent {

	private final DoubleSupplier progress;

	private static final int HOR_WIDTH = 22;
	private static final int HOR_HEIGHT = 16;

	private static final int VERT_WIDTH = 16;
	private static final int VERT_HEIGHT = 22;

	private static final int HOR_BASE_X = 56;
	private static final int HOR_BASE_Y = 0;

	private static final int VERT_BASE_X = 100;
	private static final int VERT_BASE_Y = 0;

	private boolean vertical = false;

	private int color = Colors.WHITE.getColor();

	public ScreenComponentProgress(final DoubleSupplier progress, final GenericScreen<?> gui, final int x, final int y,
			final int[] screenNumbers) {
		super(OverdriveTextures.PROGRESS_BARS, gui, x, y, HOR_WIDTH, HOR_HEIGHT, screenNumbers);
		this.progress = progress;
	}

	public ScreenComponentProgress vertical() {
		vertical = true;
		this.width = VERT_WIDTH;
		this.height = VERT_HEIGHT;
		return this;
	}

	public ScreenComponentProgress color(int color) {
		this.color = color;
		return this;
	}

	@Override
	public void renderBackground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		UtilsRendering.bindTexture(resource.getTexture());
		UtilsRendering.setShaderColor(color);
		double progress = Math.min(1.0, this.progress.getAsDouble());
		if (vertical) {
			int height = (int) (progress * this.height);
			blit(stack, this.x, this.y, VERT_BASE_X, VERT_BASE_Y, this.width, this.height);
			blit(stack, this.x, this.y, VERT_BASE_X + this.width, VERT_BASE_Y, this.width, height);
		} else {
			int width = (int) (progress * this.width);
			blit(stack, this.x, this.y, HOR_BASE_X, HOR_BASE_Y, this.width, this.height);
			blit(stack, this.x, this.y, HOR_BASE_X + this.width, HOR_BASE_Y, width, this.height);
		}
		UtilsRendering.resetShaderColor();
	}

}
