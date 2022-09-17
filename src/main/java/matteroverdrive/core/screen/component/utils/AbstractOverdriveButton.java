package matteroverdrive.core.screen.component.utils;

import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.References;
import matteroverdrive.core.screen.GenericScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractOverdriveButton extends OverdriveScreenComponent {

	public static final OnTooltip NO_TOOLTIP = (button, stack, mouseX, mouseY) -> {
	};
	protected final OnPress onPress;
	protected final OnTooltip onTooltip;

	public AbstractOverdriveButton(GenericScreen<?> gui, int x, int y, int width, int height, Component component,
			OnPress onPress) {
		this(gui, x, y, width, height, component, onPress, NO_TOOLTIP);
	}

	public AbstractOverdriveButton(GenericScreen<?> gui, int x, int y, int width, int height, Component message,
			OnPress onPress, OnTooltip onTooltip) {
		super(OverdriveTextures.NONE, gui, x, y, width, height, new int[] {}, message);
		this.onPress = onPress;
		this.onTooltip = onTooltip;
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		this.onPress();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.active && this.visible) {
			if (keyCode != 257 && keyCode != 32 && keyCode != 335) {
				return false;
			} else {
				this.playDownSound(Minecraft.getInstance().getSoundManager());
				this.onPress();
				return true;
			}
		} else {
			return false;
		}
	}

	public void onPress() {
		this.onPress.onPress(this);
	}

	@Override
	public void renderTooltip(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		this.onTooltip.onTooltip(this, stack, mouseX, mouseY);
	}

	@Override
	public void updateNarration(NarrationElementOutput narrationElementOutput) {
		this.defaultButtonNarrationText(narrationElementOutput);
		this.onTooltip.narrateTooltip((component) -> {
			narrationElementOutput.add(NarratedElementType.HINT, component);
		});
	}

	@OnlyIn(Dist.CLIENT)
	public interface OnPress {
		void onPress(AbstractOverdriveButton button);
	}

	@OnlyIn(Dist.CLIENT)
	public interface OnTooltip {
		void onTooltip(AbstractOverdriveButton button, PoseStack stack, int mouseX, int mouseY);

		default void narrateTooltip(Consumer<Component> contents) {
		}
	}
	
	public static enum ButtonTextures implements ITexture {
		
		GENERIC_BUTTONS(new ResourceLocation(References.ID, "textures/gui/button/buttons.png"), 256, 256),
		MENU_BAR(new ResourceLocation(References.ID, "textures/gui/button/menu_bar.png"), 256, 256),
		
		OVERDRIVE_NONE_REG(new ResourceLocation(References.ID, "textures/gui/button/button_normal.png"), 18, 18),
		OVERDRIVE_NONE_LEFT(new ResourceLocation(References.ID, "textures/gui/button/button_normal_left.png"), 18, 18),
		OVERDRIVE_NONE_RIGHT(new ResourceLocation(References.ID, "textures/gui/button/button_normal_right.png"), 18, 18),
		OVERDRIVE_HOVER_REG(new ResourceLocation(References.ID, "textures/gui/button/button_over.png"), 18, 18),
		OVERDRIVE_HOVER_LEFT(new ResourceLocation(References.ID, "textures/gui/button/button_over_left.png"), 18, 18),
		OVERDRIVE_HOVER_RIGHT(new ResourceLocation(References.ID, "textures/gui/button/button_over_right.png"), 18, 18),
		OVERDRIVE_PRESS_REG(new ResourceLocation(References.ID, "textures/gui/button/button_over_dark.png"), 18, 18),
		OVERDRIVE_PRESS_LEFT(new ResourceLocation(References.ID, "textures/gui/button/button_over_dark_left.png"), 18, 18),
		OVERDRIVE_PRESS_RIGHT(new ResourceLocation(References.ID, "textures/gui/button/button_over_dark_right.png"), 18, 18);
		
		private final ResourceLocation texture;
		private final int textureWidth;
		private final int textureHeight;
		
		private ButtonTextures(ResourceLocation texture, int textureWidth, int textureHeight) {
			this.texture = texture;
			this.textureWidth = textureWidth;
			this.textureHeight = textureHeight;
		}
		
		@Override
		public ResourceLocation getTexture() {
			return texture;
		}

		@Override
		public int getTextureWidth() {
			return textureWidth;
		}

		@Override
		public int getTextureHeight() {
			return textureHeight;
		}
	}

}
