package matteroverdrive.core.screen.component.button;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.SoundRegister;
import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.ScreenComponentIcon.IconType;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ButtonEditTransporterLocation extends ButtonOverdrive {

	private static final IconType ICON = IconType.PENCIL_DARK;
	public int index;

	public ButtonEditTransporterLocation(GenericScreen<?> gui, int x, int y, OnPress press, int index) {
		super(gui, x, y, 20, 20, Component.empty(), press);
		this.index = index;
	}

	@Override
	public void renderForeground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		UtilsRendering.bindTexture(new ResourceLocation(ICON.getTextureLoc()));
		int widthOffset = (int) ((20 - ICON.getTextWidth()) / 2);
		int heightOffset = (int) ((20 - ICON.getTextHeight()) / 2);
		blit(stack, this.x + widthOffset, this.y + heightOffset, ICON.getTextureX(), ICON.getTextureY(),
				ICON.getTextWidth(), ICON.getTextHeight(), ICON.getTextHeight(), ICON.getTextWidth());
	}

	@Override
	public void playDownSound(SoundManager manager) {
		manager.play(SimpleSoundInstance.forUI(SoundRegister.SOUND_BUTTON_LOUD3.get(), 1.0F, 1.0F));
	}

}
