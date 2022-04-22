package matteroverdrive.core.screen.component.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.MatterOverdrive;
import matteroverdrive.References;
import matteroverdrive.SoundRegister;
import matteroverdrive.core.screen.IScreenWrapper;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class ButtonMenuBar extends Button {

	public static final int EXTEND_DISTANCE = 32;
	private static final ResourceLocation TEXTURE = new ResourceLocation(References.ID,
			"textures/gui/button/menu_bar.png");

	public boolean isExtended;
	private boolean isPressed;

	private IScreenWrapper gui;

	public ButtonMenuBar(int pX, int pY, boolean inidialCondition, OnPress press, IScreenWrapper owner) {
		super(pX, pY, 16, 143, TextComponent.EMPTY, press, (button, stack, mouseX, mouseY) -> {
			ButtonMenuBar bar = (ButtonMenuBar) button;
			if (bar.isExtended) {
				bar.gui.displayTooltip(stack, new TranslatableComponent("tooltip.matteroverdrive.closemenu"), mouseX,
						mouseY);
			} else {
				bar.gui.displayTooltip(stack, new TranslatableComponent("tooltip.matteroverdrive.openmenu"), mouseX,
						mouseY);
			}
		});
		this.gui = owner;
		isExtended = inidialCondition;
		if (isExtended) {
			this.x += EXTEND_DISTANCE;
		}
	}

	@Override
	public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		if (isPressed) {
			this.blit(pPoseStack, this.x, this.y, 16, 0, 16, 143);
		} else {
			this.blit(pPoseStack, this.x, this.y, 0, 0, 16, 143);
		}
		if (isExtended) {
			this.blit(pPoseStack, this.x - EXTEND_DISTANCE, this.y, 32, 0, 32, 143);
		}
		if (this.isHoveredOrFocused()) {
			this.renderToolTip(pPoseStack, pMouseX, pMouseY);
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
