package matteroverdrive.core.screen.component.button;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.SoundRegister;
import matteroverdrive.core.screen.component.ScreenComponentIcon.IconType;
import matteroverdrive.core.screen.component.utils.OverdriveTextureButton;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class ButtonEditTransporterLocation extends OverdriveTextureButton {

	private static final IconType ICON = IconType.PENCIL_DARK;
	public int index;

	public ButtonEditTransporterLocation(int pX, int pY, OnPress press, int index) {
		super(pX, pY, 20, 20, TextComponent.EMPTY, press);
		this.index = index;
	}

	@Override
	public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
		super.renderButton(pPoseStack, pMouseX, pMouseY, pPartialTick);
		UtilsRendering.bindTexture(new ResourceLocation(ICON.getTextureLoc()));
		int widthOffset = (int) ((20 - ICON.getTextWidth()) / 2);
		int heightOffset = (int) ((20 - ICON.getTextHeight()) / 2);
		blit(pPoseStack, this.x + widthOffset, this.y + heightOffset, ICON.getTextureX(), ICON.getTextureY(),
				ICON.getTextWidth(), ICON.getTextHeight(), ICON.getTextHeight(), ICON.getTextWidth());
	}

	@Override
	public void playDownSound(SoundManager manager) {
		manager.play(SimpleSoundInstance.forUI(SoundRegister.SOUND_BUTTON_LOUD3.get(), 1.0F, 1.0F));
	}

}
