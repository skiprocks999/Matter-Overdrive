package matteroverdrive.core.screen.component.button;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.ScreenComponentIcon.IconType;
import matteroverdrive.core.utils.UtilsRendering;
import matteroverdrive.registry.SoundRegistry;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;

public class ButtonIOConfig extends ButtonHoldPress {

	private IOConfigButtonType type;
	private final ResourceLocation iconLightLoc;
	private final ResourceLocation iconDarkLoc;

	private static final int WIDTH = 28;
	private static final int HEIGHT = 27;

	public ButtonIOConfig(GenericScreen<?> gui, int x, int y, OnPress onPress, IOConfigButtonType type) {
		super(gui, x, y, WIDTH, HEIGHT, NO_TEXT, onPress);
		this.type = type;
		iconDarkLoc = type.iconDark.getTexture();
		iconLightLoc = type.iconLight.getTexture();
	}

	@Override
	public void renderForeground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		IconType icon;
		if (isActivated) {
			icon = type.iconLight;
			UtilsRendering.bindTexture(iconLightLoc);
		} else {
			icon = type.iconDark;
			UtilsRendering.bindTexture(iconDarkLoc);
		}
		int widthOffset = (int) ((WIDTH - icon.getTextureWidth()) / 2);
		int heightOffset = (int) ((HEIGHT - icon.getTextureHeight()) / 2);
		blit(stack, this.x + widthOffset, this.y + heightOffset, icon.getTextureU(), icon.getTextureV(),
				icon.getUWidth(), icon.getVHeight(), icon.getTextureHeight(), icon.getTextureWidth());
	}

	@Override
	public void playDownSound(SoundManager pHandler) {
		pHandler.play(SimpleSoundInstance.forUI(SoundRegistry.SOUND_BUTTON_LOUD3.get(), 1.0F));
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
