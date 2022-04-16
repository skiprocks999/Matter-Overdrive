package matteroverdrive.core.screen.component.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.References;
import matteroverdrive.SoundRegister;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class ButtonGeneric extends Button {

	private static final ResourceLocation TEXTURE = new ResourceLocation(References.ID,
			"textures/gui/button/buttons.png");
	private ButtonType type;

	public ButtonGeneric(int x, int y, ButtonType type, Component pMessage, OnPress pOnPress, OnTooltip pOnTooltip) {
		super(x, y, type.width, type.height, pMessage, pOnPress, pOnTooltip);
		this.type = type;
	}

	public ButtonGeneric(int x, int y, ButtonType type, OnPress pOnPress) {
		this(x, y, type, TextComponent.EMPTY, pOnPress, Button.NO_TOOLTIP);
		this.type = type;
	}

	@Override
	public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		int x = type.xOffset;
		int y = type.yOffset;

		if (isHoveredOrFocused()) {
			x = type.xOffsetHover;
			y = type.yOffsetHover;
		}

		this.blit(pPoseStack, this.x, this.y, x, y, type.width, type.height);
	}

	@Override
	public void playDownSound(SoundManager pHandler) {
		if (type.playSound) {
			pHandler.play(SimpleSoundInstance.forUI(type.event, 1.0F));
		}
	}

	public enum ButtonType {

		CLOSE_SCREEN(0, 0, 9, 0, 9, 9, true, SoundRegister.SOUND_BUTTONSOFT1.get());

		public final int xOffset;
		public final int yOffset;
		public final int xOffsetHover;
		public final int yOffsetHover;
		public final int width;
		public final int height;
		public final boolean playSound;
		public final SoundEvent event;

		ButtonType(int xOffset, int yOffset, int xOffsetHover, int yOffsetHover, int width, int height,
				boolean playSound, SoundEvent event) {
			this.xOffset = xOffset;
			this.yOffset = yOffset;
			this.xOffsetHover = xOffsetHover;
			this.yOffsetHover = yOffsetHover;
			this.width = width;
			this.height = height;
			this.playSound = playSound;
			this.event = event;
		}

	}

}
