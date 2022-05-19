package matteroverdrive.core.screen.component.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

public class ButtonHoldPress extends OverdriveTextureButton {

	public boolean isActivated = false;

	public ButtonHoldPress(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress) {
		super(pX, pY, pWidth, pHeight, pMessage, pOnPress);
	}

	public ButtonHoldPress(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress,
			OnTooltip pOnTooltip) {
		super(pX, pY, pWidth, pHeight, pMessage, pOnPress, pOnTooltip);
	}

	@Override
	public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

		if (isActivated) {
			UtilsRendering.bindTexture(pressedText);

		} else {
			if (isHoveredOrFocused()) {
				UtilsRendering.bindTexture(hoveredText);
			} else {
				UtilsRendering.bindTexture(defaultText);
			}
		}

		drawButton(pPoseStack, this.x, this.y, this.width, this.height);

		if (this.isHoveredOrFocused()) {
			this.renderToolTip(pPoseStack, pMouseX, pMouseY);
		}

		Minecraft minecraft = Minecraft.getInstance();
		Font font = minecraft.font;
		drawCenteredString(pPoseStack, font, getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2,
				getTextColor());
	}

	@Override
	public void onPress() {
		super.onPress();
		isActivated = !isActivated;
	}

}
