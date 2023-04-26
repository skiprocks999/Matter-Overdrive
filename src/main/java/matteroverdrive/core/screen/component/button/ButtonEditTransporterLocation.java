package matteroverdrive.core.screen.component.button;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.ScreenComponentIcon.IconType;
import matteroverdrive.core.utils.UtilsRendering;
import matteroverdrive.registry.SoundRegistry;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;;

public class ButtonEditTransporterLocation extends ButtonOverdrive {

	private static final IconType ICON = IconType.PENCIL_DARK;
	public int index;

	public ButtonEditTransporterLocation(GenericScreen<?> gui, int x, int y, OnPress press, int index) {
		super(gui, x, y, 20, 20, NO_TEXT, press);
		this.index = index;
	}

	@Override
	public void renderForeground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		UtilsRendering.bindTexture(ICON.getTexture());
		int widthOffset = (int) ((20 - ICON.getTextureWidth()) / 2);
		int heightOffset = (int) ((20 - ICON.getTextureHeight()) / 2);
		blit(stack, this.x + widthOffset, this.y + heightOffset, ICON.getTextureU(), ICON.getTextureV(),
				ICON.getUWidth(), ICON.getVHeight(), ICON.getTextureHeight(), ICON.getTextureWidth());
	}

	@Override
	public void playDownSound(SoundManager manager) {
		manager.play(SimpleSoundInstance.forUI(SoundRegistry.SOUND_BUTTON_LOUD3.get(), 1.0F, 1.0F));
	}

}
