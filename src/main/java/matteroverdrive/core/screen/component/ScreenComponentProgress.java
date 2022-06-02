package matteroverdrive.core.screen.component;

import java.util.function.DoubleSupplier;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.References;
import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.utils.OverdriveScreenComponent;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.resources.ResourceLocation;

public class ScreenComponentProgress extends OverdriveScreenComponent {

	private final DoubleSupplier progress;

	private static final int WIDTH = 22;
	private static final int HEIGHT = 16;

	private static final int BASE_X = 56;
	private static final int BASE_Y = 0;

	public ScreenComponentProgress(final DoubleSupplier progress, final GenericScreen<?> gui, final int x, final int y,
			final int[] screenNumbers) {
		super(new ResourceLocation(References.ID + ":textures/gui/progress/progress.png"), gui, x, y, WIDTH, HEIGHT, screenNumbers);
		this.progress = progress;
	}

	@Override
	public void renderBackground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		UtilsRendering.bindTexture(resource);
		double progress = Math.min(1.0, this.progress.getAsDouble());

		int width = (int) (progress * this.width);
		blit(stack, this.x, this.y, BASE_X, BASE_Y, this.width, this.height);
		blit(stack, this.x, this.y, BASE_X + this.width, BASE_Y, width, this.height);

	}

}
