package matteroverdrive.core.screen.component.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.References;
import matteroverdrive.SoundRegister;
import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.utils.AbstractOverdriveButton;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class ButtonGeneric extends AbstractOverdriveButton {

	private static final ResourceLocation TEXTURE = new ResourceLocation(References.ID,
			"textures/gui/button/buttons.png");
	private ButtonType type;

	public ButtonGeneric(GenericScreen<?> gui, int x, int y, ButtonType type, Component message, OnPress onPress,
			OnTooltip onTooltip) {
		super(gui, x, y, type.width, type.height, message, onPress, onTooltip);
		this.type = type;
	}

	public ButtonGeneric(GenericScreen<?> gui, int x, int y, ButtonType type, OnPress onPress) {
		this(gui, x, y, type, Component.empty(), onPress, NO_TOOLTIP);
		this.type = type;
	}

	@Override
	public void renderBackground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		UtilsRendering.bindTexture(TEXTURE);
		int x = type.xOffset;
		int y = type.yOffset;

		if (isHoveredOrFocused()) {
			x = type.xOffsetHover;
			y = type.yOffsetHover;
		}

		blit(stack, this.x, this.y, x, y, type.width, type.height);
	}

	@Override
	public void playDownSound(SoundManager pHandler) {
		if (type.playSound) {
			pHandler.play(SimpleSoundInstance.forUI(type.event, 1.0F));
		}
	}

	public enum ButtonType {

		CLOSE_SCREEN(0, 0, 9, 0, 9, 9, true, SoundRegister.SOUND_BUTTON_SOFT1.get()),
		CLOSE_RED(0, 0, 18, 0, 9, 9, true, SoundRegister.SOUND_BUTTON_SOFT1.get()),
		ORDER_ITEMS(0, 57, 20, 57, 20, 10, false, null);

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
