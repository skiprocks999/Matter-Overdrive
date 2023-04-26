package matteroverdrive.core.screen.component.button;

import java.util.function.Supplier;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

public class ButtonHoldPress extends ButtonOverdrive {

	public boolean isActivated = false;

	public ButtonHoldPress(GenericScreen<?> gui, int x, int y, int width, int height, Supplier<Component> message,
			OnPress onPress) {
		super(gui, x, y, width, height, message, onPress);
	}

	public ButtonHoldPress(GenericScreen<?> gui, int x, int y, int width, int height, Supplier<Component> message,
			OnPress onPress, OnTooltip pOnTooltip) {
		super(gui, x, y, width, height, message, onPress, pOnTooltip);
	}

	@Override
	public void renderBackground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

		if (isActivated) {
			UtilsRendering.bindTexture(pressedText.getTexture());

		} else {
			if (isHoveredOrFocused()) {
				UtilsRendering.bindTexture(hoveredText.getTexture());
			} else {
				UtilsRendering.bindTexture(defaultText.getTexture());
			}
		}

		drawButton(stack, this.x, this.y, this.width, this.height);

	}

	@Override
	public void onPress() {
		this.onPress.onPress(this);
		isActivated = !isActivated;
	}

}
