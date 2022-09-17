package matteroverdrive.core.screen.component;

import java.util.function.BooleanSupplier;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.utils.OverdriveScreenComponent;
import matteroverdrive.core.utils.UtilsRendering;

public class ScreenComponentIndicator extends OverdriveScreenComponent {

	private final BooleanSupplier active;
	private boolean isRed = false;

	private static final int WIDTH = 21;
	private static final int HEIGHT = 5;

	public ScreenComponentIndicator(final BooleanSupplier supplier, final GenericScreen<?> gui, final int x,
			final int y, final int[] screenNumbers) {
		super(OverdriveTextures.RUNNING_INDICATOR, gui, x, y, WIDTH, HEIGHT, screenNumbers);
		active = supplier;
	}

	public ScreenComponentIndicator setRed() {
		isRed = true;
		return this;
	}

	@Override
	public void renderBackground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {

		UtilsRendering.bindTexture(resource.getTexture());

		blit(stack, this.x, this.y, 0, 0, this.width, this.height);

		if (active.getAsBoolean()) {
			if (isRed) {
				blit(stack, this.x, this.y, 0, this.height * 2, this.width, this.height);
			} else {
				blit(stack, this.x, this.y, 0, this.height, this.width, this.height);
			}
		}

	}

}
