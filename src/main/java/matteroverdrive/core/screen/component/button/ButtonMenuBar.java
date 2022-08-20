package matteroverdrive.core.screen.component.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.MatterOverdrive;
import matteroverdrive.References;
import matteroverdrive.SoundRegister;
import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.utils.AbstractOverdriveButton;
import matteroverdrive.core.utils.UtilsRendering;
import matteroverdrive.core.utils.UtilsText;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ButtonMenuBar extends AbstractOverdriveButton {

	public static final int EXTEND_DISTANCE = 32;
	private static final ResourceLocation TEXTURE = new ResourceLocation(References.ID,
			"textures/gui/button/menu_bar.png");

	public boolean isExtended;
	private boolean isPressed;

	public ButtonMenuBar(GenericScreen<?> gui, int x, int y, boolean inidialCondition, OnPress press) {
		super(gui, x, y, 16, 143, Component.empty(), press, (button, stack, mouseX, mouseY) -> {
			ButtonMenuBar bar = (ButtonMenuBar) button;
			if (bar.isExtended) {
				bar.gui.renderTooltip(stack, UtilsText.tooltip("closemenu"), mouseX, mouseY);
			} else {
				bar.gui.renderTooltip(stack, UtilsText.tooltip("openmenu"), mouseX, mouseY);
			}
		});
		isExtended = inidialCondition;
		if (isExtended) {
			this.x += EXTEND_DISTANCE;
		}
	}

	@Override
	public void renderBackground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		UtilsRendering.bindTexture(TEXTURE);

		if (isPressed) {
			this.blit(stack, this.x, this.y, 16, 0, 16, 143);
		} else {
			this.blit(stack, this.x, this.y, 0, 0, 16, 143);
		}
		if (isExtended) {
			this.blit(stack, this.x - EXTEND_DISTANCE, this.y, 32, 0, 32, 143);
		}
	}

	@Override
	public void onPress() {
		isPressed = true;
	}

	@Override
	public void onRelease(double pMouseX, double pMouseY) {
		this.onPress.onPress(this);
		isPressed = false;
		if (isExtended) {
			this.x -= EXTEND_DISTANCE;
		} else {
			this.x += EXTEND_DISTANCE;
		}
		isExtended = !isExtended;
	}

	public Boolean getIsExtended() {
		return isExtended;
	}

	@Override
	public void playDownSound(SoundManager pHandler) {
		float pitch = MatterOverdrive.RANDOM.nextFloat(0.9F, 1.1F);
		pHandler.play(SimpleSoundInstance.forUI(SoundRegister.SOUND_BUTTON_EXPAND.get(), pitch));
	}

}
