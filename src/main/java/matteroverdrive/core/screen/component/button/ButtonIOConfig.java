package matteroverdrive.core.screen.component.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.References;
import matteroverdrive.SoundRegister;
import matteroverdrive.core.screen.component.ScreenComponentIcon.IconType;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class ButtonIOConfig extends Button {

	private static final ResourceLocation TEXTURE = new ResourceLocation(
			References.ID + ":textures/gui/button/buttons.png");

	private IOConfigButtonType type;
	private final ResourceLocation iconLightLoc;
	private final ResourceLocation iconDarkLoc;
	public boolean isActivated = false;

	private static final int X_START = 0;
	private static final int Y_START = 69;
	private static final int WIDTH = 28;
	private static final int HEIGHT = 27;

	public ButtonIOConfig(int x, int y, OnPress onPress, IOConfigButtonType type) {
		super(x, y, WIDTH, HEIGHT, TextComponent.EMPTY, onPress);
		this.type = type;
		iconDarkLoc = new ResourceLocation(type.iconDark.getTextureLoc());
		iconLightLoc = new ResourceLocation(type.iconLight.getTextureLoc());
	}

	@Override
	public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		UtilsRendering.bindTexture(TEXTURE);
		IconType icon;
		if (isActivated) {
			blit(pPoseStack, this.x, this.y, X_START, Y_START + HEIGHT * 2, WIDTH, HEIGHT);
			icon = type.iconLight;
			UtilsRendering.bindTexture(iconLightLoc);
		} else {
			if (isHoveredOrFocused()) {
				blit(pPoseStack, this.x, this.y, X_START, Y_START + HEIGHT, WIDTH, HEIGHT);
			} else {
				blit(pPoseStack, this.x, this.y, X_START, Y_START, WIDTH, HEIGHT);
			}
			icon = type.iconDark;
			UtilsRendering.bindTexture(iconDarkLoc);
		}
		int widthOffset = (int) ((WIDTH - icon.getTextWidth()) / 2);
		int heightOffset = (int) ((HEIGHT - icon.getTextHeight()) / 2);
		blit(pPoseStack, this.x + widthOffset, this.y + heightOffset, icon.getTextureX(), icon.getTextureY(),
				icon.getTextWidth(), icon.getTextHeight(), icon.getTextHeight(), icon.getTextWidth());
	}

	@Override
	public void onPress() {
		super.onPress();
		isActivated = !isActivated;
	}

	@Override
	public void playDownSound(SoundManager pHandler) {
		pHandler.play(SimpleSoundInstance.forUI(SoundRegister.SOUND_BUTTON_LOUD3.get(), 1.0F));
	}

	public enum IOConfigButtonType {

		ITEM(IconType.BLOCK_DARK, IconType.BLOCK_LIGHT), ENERGY(IconType.ENERGY_DARK, IconType.ENERGY_LIGHT),
		MATTER(IconType.MATTER_DARK, IconType.MATTER_LIGHT);

		public final IconType iconDark;
		public final IconType iconLight;

		private IOConfigButtonType(IconType iconDark, IconType iconLight) {
			this.iconDark = iconDark;
			this.iconLight = iconLight;
		}

	}

}
