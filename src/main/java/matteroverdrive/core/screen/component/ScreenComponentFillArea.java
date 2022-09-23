package matteroverdrive.core.screen.component;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.utils.OverdriveScreenComponent;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.client.renderer.GameRenderer;

public class ScreenComponentFillArea extends OverdriveScreenComponent {

	private final int color;

	public ScreenComponentFillArea(GenericScreen<?> gui, int x, int y, int width, int height, int[] screenNumbers,
			int color) {
		super(OverdriveTextures.WHITE, gui, x, y, width, height, screenNumbers);
		this.color = color;
	}

	@Override
	public void renderBackground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		UtilsRendering.bindTexture(resource.getTexture());
		UtilsRendering.setShaderColor(color);
		blit(stack, this.x, this.y, 0, 0, this.width, this.height);
		UtilsRendering.resetShaderColor();
	}

}
