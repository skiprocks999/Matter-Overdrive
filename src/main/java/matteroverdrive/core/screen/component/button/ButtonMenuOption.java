package matteroverdrive.core.screen.component.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.SoundRegister;
import matteroverdrive.core.screen.IScreenWrapper;
import matteroverdrive.core.screen.component.ScreenComponentIcon.IconType;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class ButtonMenuOption extends Button {

	private IScreenWrapper gui;
	private MenuButtonType type;
	public boolean isActivated;
	private final ResourceLocation defaultLoc;
	private final ResourceLocation activeLoc;
	private final ResourceLocation iconLoc;
	private final ButtonMenuBar bar;

	public ButtonMenuOption(int pX, int pY, IScreenWrapper gui, OnPress pOnPress, MenuButtonType type,
			ButtonMenuBar bar, boolean isActivated) {
		super(pX, pY, 18, 18, TextComponent.EMPTY, pOnPress, (button, stack, mouseX, mouseY) -> {
			ButtonMenuOption menuOption = (ButtonMenuOption) button;
			menuOption.gui.displayTooltip(stack, type.tooltip, mouseX, mouseY);
		});
		this.type = type;
		this.gui = gui;
		this.bar = bar;
		this.visible = bar.getIsExtended();
		this.isActivated = isActivated;
		defaultLoc = new ResourceLocation(type.defaultSlot.getTextureLoc());
		activeLoc = new ResourceLocation(type.activeSlot.getTextureLoc());
		iconLoc = new ResourceLocation(type.icon.getTextureLoc());
	}

	@Override
	public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
		if (bar.isExtended) {
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			if (isActivated || isHoveredOrFocused()) {
				SlotType slot = type.activeSlot;
				UtilsRendering.bindTexture(activeLoc);
				blit(pPoseStack, this.x, this.y, slot.getTextureX(), slot.getTextureY(), slot.getWidth(),
						slot.getHeight(), slot.getHeight(), slot.getWidth());
				IconType icon = type.icon;
				int widthOffset = (int) ((slot.getWidth() - icon.getTextWidth()) / 2);
				int heightOffset = (int) ((slot.getHeight() - icon.getTextHeight()) / 2);
				UtilsRendering.bindTexture(iconLoc);
				blit(pPoseStack, this.x + widthOffset, this.y + heightOffset, icon.getTextureX(), icon.getTextureY(),
						icon.getTextWidth(), icon.getTextHeight(), icon.getTextHeight(), icon.getTextWidth());
			} else {
				SlotType slot = type.defaultSlot;
				UtilsRendering.bindTexture(defaultLoc);
				blit(pPoseStack, this.x, this.y, slot.getTextureX(), slot.getTextureY(), slot.getWidth(),
						slot.getHeight(), slot.getHeight(), slot.getWidth());
				IconType icon = type.icon;
				int widthOffset = (int) ((slot.getWidth() - icon.getTextWidth()) / 2);
				int heightOffset = (int) ((slot.getHeight() - icon.getTextHeight()) / 2);
				UtilsRendering.bindTexture(iconLoc);
				blit(pPoseStack, this.x + widthOffset, this.y + heightOffset, icon.getTextureX(), icon.getTextureY(),
						icon.getTextWidth(), icon.getTextHeight(), icon.getTextHeight(), icon.getTextWidth());
			}
			if (isHoveredOrFocused()) {
				renderToolTip(pPoseStack, pMouseX, pMouseY);
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
		pHandler.play(SimpleSoundInstance.forUI(SoundRegister.SOUND_BUTTON_SOFT0.get(), 1.0F));
	}

	public enum MenuButtonType {

		HOME(SlotType.BIG, SlotType.BIG_DARK, IconType.PAGE_HOME,
				new TranslatableComponent("tooltip.matteroverdrive.menuhome")),
		SETTINGS(SlotType.BIG, SlotType.BIG_DARK, IconType.PAGE_WRENCH,
				new TranslatableComponent("tooltip.matteroverdrive.menusettings")),
		UPGRADES(SlotType.BIG, SlotType.BIG_DARK, IconType.PAGE_UPGRADES,
				new TranslatableComponent("tooltip.matteroverdrive.menuupgrades")),
		IO(SlotType.BIG, SlotType.BIG_DARK, IconType.PAGE_GEAR,
				new TranslatableComponent("tooltip.matteroverdrive.menuio"));

		public final SlotType defaultSlot;
		public final SlotType activeSlot;
		public final IconType icon;
		public final TranslatableComponent tooltip;

		private MenuButtonType(SlotType defaultSlot, SlotType activeSlot, IconType icon,
				TranslatableComponent tooltip) {
			this.defaultSlot = defaultSlot;
			this.activeSlot = activeSlot;
			this.icon = icon;
			this.tooltip = tooltip;
		}

	}

}
