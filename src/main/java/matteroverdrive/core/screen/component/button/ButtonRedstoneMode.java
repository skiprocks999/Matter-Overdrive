package matteroverdrive.core.screen.component.button;

import java.util.function.Supplier;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.References;
import matteroverdrive.SoundRegister;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class ButtonRedstoneMode extends Button {

	private static final ResourceLocation TEXTURE = new ResourceLocation(References.ID,
			"textures/gui/button/buttons.png");
	
	private boolean isPressed;
	private Supplier<Integer> mode;
	
	public ButtonRedstoneMode(int x, int y, OnPress pOnPress, Supplier<Integer> mode) {
		super(x, y, 58, 20, TextComponent.EMPTY, pOnPress);
		this.mode = mode;
	}
	
	@Override
	public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
		Minecraft minecraft = Minecraft.getInstance();
	      Font font = minecraft.font;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		
		int x = 0;
		int y = 10;
		
		if(isPressed) {
			y = 50;
		} else if (isHoveredOrFocused()) {
			y = 30;
		}
		this.blit(pPoseStack, this.x, this.y, x, y, 58, 20);
		drawCenteredString(pPoseStack, font, getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, UtilsRendering.getRGBA(1, 169, 226, 251));
	}
	
	public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
		if (isPressed || this.isValidClickButton(pButton)) {
			this.onRelease(pMouseX, pMouseY);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onPress() {
		super.onPress();
		isPressed = true;
	}
	
	@Override
	public void onRelease(double pMouseX, double pMouseY) {
		super.onRelease(pMouseX, pMouseY);
		isPressed = false;
	}
	
	@Override
	public void playDownSound(SoundManager pHandler) {
		pHandler.play(SimpleSoundInstance.forUI(SoundRegister.SOUND_BUTTONLOUD3.get(), 1.0F));
	}
	
	@Override
	public Component getMessage() {
		switch(mode.get()) {
		case 0:
			return new TranslatableComponent("gui.matteroverdrive.redstonelow");
		case 1:
			return new TranslatableComponent("gui.matteroverdrive.redstonehigh");
		case 2:
			return new TranslatableComponent("gui.matteroverdrive.redstonenone");
		default:
			return super.getMessage();
		}
	}

}
