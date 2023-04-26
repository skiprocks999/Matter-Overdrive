package matteroverdrive.core.screen.component.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.ScreenComponentIcon.IconType;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import matteroverdrive.core.screen.component.utils.AbstractOverdriveButton;
import matteroverdrive.core.utils.UtilsRendering;
import matteroverdrive.core.utils.UtilsText;
import matteroverdrive.registry.SoundRegistry;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class ButtonMenuOption extends AbstractOverdriveButton {

	private MenuButtonType type;
	public boolean isActivated;
	private final ResourceLocation defaultLoc;
	private final ResourceLocation activeLoc;
	private final ResourceLocation iconLoc;
	private final ButtonMenuBar bar;

	public ButtonMenuOption(GenericScreen<?> gui, int x, int y, OnPress press, MenuButtonType type, ButtonMenuBar bar,
			boolean isActivated) {
		super(gui, x, y, 18, 18, NO_TEXT, press, (button, stack, mouseX, mouseY) -> {
			ButtonMenuOption menuOption = (ButtonMenuOption) button;
			menuOption.gui.renderTooltip(stack, type.tooltip, mouseX, mouseY);
		});
		this.type = type;
		this.bar = bar;
		this.visible = bar.getIsExtended();
		this.isActivated = isActivated;
		defaultLoc = type.defaultSlot.getTexture();
		activeLoc = type.activeSlot.getTexture();
		iconLoc = type.icon.getTexture();
	}

	@Override
	public void renderBackground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		if (bar.isExtended) {
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

			if (isActivated || isHoveredOrFocused()) {
				SlotType slot = type.activeSlot;
				UtilsRendering.bindTexture(activeLoc);
				blit(stack, x, y, slot.getTextureU(), slot.getTextureV(), slot.getUWidth(), slot.getVHeight(),
						slot.getTextureHeight(), slot.getTextureWidth());
				IconType icon = type.icon;
				int widthOffset = (int) ((slot.getTextureWidth() - icon.getTextureWidth()) / 2);
				int heightOffset = (int) ((slot.getTextureHeight() - icon.getTextureHeight()) / 2);
				UtilsRendering.bindTexture(iconLoc);
				blit(stack, x + widthOffset, y + heightOffset, icon.getTextureU(), icon.getTextureV(),
						icon.getUWidth(), icon.getVHeight(), icon.getTextureHeight(), icon.getTextureWidth());
			} else {
				SlotType slot = type.defaultSlot;
				UtilsRendering.bindTexture(defaultLoc);
				blit(stack, x, y, slot.getTextureU(), slot.getTextureV(), slot.getUWidth(), slot.getVHeight(),
						slot.getTextureHeight(), slot.getTextureWidth());
				IconType icon = type.icon;
				int widthOffset = (int) ((slot.getTextureWidth() - icon.getTextureWidth()) / 2);
				int heightOffset = (int) ((slot.getTextureHeight() - icon.getTextureHeight()) / 2);
				UtilsRendering.bindTexture(iconLoc);
				blit(stack, x + widthOffset, y + heightOffset, icon.getTextureU(), icon.getTextureV(),
						icon.getUWidth(), icon.getVHeight(), icon.getTextureHeight(), icon.getTextureWidth());
			}
		}
	}

	@Override
	public void onPress() {
		super.onPress();
		isActivated = true;
	}

	@Override
	public void playDownSound(SoundManager pHandler) {
		pHandler.play(SimpleSoundInstance.forUI(SoundRegistry.SOUND_BUTTON_SOFT0.get(), 1.0F));
	}

	public enum MenuButtonType {

		HOME(SlotType.BIG, SlotType.BIG_DARK, IconType.PAGE_HOME, UtilsText.tooltip("menuhome")),
		SETTINGS(SlotType.BIG, SlotType.BIG_DARK, IconType.PAGE_WRENCH, UtilsText.tooltip("menusettings")),
		UPGRADES(SlotType.BIG, SlotType.BIG_DARK, IconType.PAGE_UPGRADES, UtilsText.tooltip("menuupgrades")),
		IO(SlotType.BIG, SlotType.BIG_DARK, IconType.PAGE_GEAR, UtilsText.tooltip("menuio")),
		TASKS(SlotType.BIG, SlotType.BIG_DARK, IconType.PAGE_TASKS, UtilsText.tooltip("menutasks")),
		FUSE_DRIVE(SlotType.BIG, SlotType.BIG_DARK, IconType.CONNECTIONS, UtilsText.tooltip("fusedrive")),
		ERASE_DRIVE(SlotType.BIG, SlotType.BIG_DARK, IconType.ERASER_LIGHT, UtilsText.tooltip("erasedrive"));

		public final SlotType defaultSlot;
		public final SlotType activeSlot;
		public final IconType icon;
		public final MutableComponent tooltip;

		private MenuButtonType(SlotType defaultSlot, SlotType activeSlot, IconType icon, MutableComponent tooltip) {
			this.defaultSlot = defaultSlot;
			this.activeSlot = activeSlot;
			this.icon = icon;
			this.tooltip = tooltip;
		}

	}

}
