package matteroverdrive.core.screen.component.utils;

import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.References;
import matteroverdrive.core.screen.GenericScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * 
 * Extension of AbstractWidget class designed for custom GUI component rendering
 * 
 * @author skip999
 *
 */
public abstract class OverdriveScreenComponent extends AbstractWidget {

	protected ITexture resource;
	protected GenericScreen<?> gui;
	protected int[] screenNumbers;
	protected Supplier<Component> message;
	
	public static final Supplier<Component> NO_TEXT = () -> Component.empty();

	protected OverdriveScreenComponent(ITexture resource, GenericScreen<?> gui, int x, int y, int width,
			int height, int[] screenNumbers) {
		this(resource, gui, x, y, width, height, screenNumbers, NO_TEXT);
	}

	protected OverdriveScreenComponent(ITexture resource, GenericScreen<?> gui, int x, int y, int width,
			int height, int[] screenNumbers, Supplier<Component> component) {
		super(x, y, width, height, Component.empty());
		this.resource = resource;
		this.gui = gui;
		this.screenNumbers = screenNumbers;
		message = component;
	}

	public void initScreenSize() {
		this.x += gui.getXPos();
		this.y += gui.getYPos();
	}

	@Override
	public final void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		if (this.visible) {

			this.isHovered = isPointInRegion(mouseX, mouseY);
			renderBackground(stack, mouseX, mouseY, partialTicks);
			renderForeground(stack, mouseX, mouseY, partialTicks);

			if (isHoveredOrFocused()) {
				renderTooltip(stack, mouseX, mouseY, partialTicks);
			}

		}
	}

	public void renderBackground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {

	}

	public void renderForeground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {

	}

	public void renderTooltip(PoseStack stack, int mouseX, int mouseY, float partialTicks) {

	}

	@Override
	public final void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
	}

	@Override
	protected final void renderBg(PoseStack pPoseStack, Minecraft pMinecraft, int pMouseX, int pMouseY) {
	}

	@Override
	public final void renderToolTip(PoseStack pPoseStack, int pMouseX, int pMouseY) {

	}

	public void renderScaledText(PoseStack stack, String text, int x, int y, int color, int maxX) {
		int length = gui.getFontRenderer().width(text);

		if (length <= maxX) {
			gui.getFontRenderer().draw(stack, text, x, y, color);
		} else {
			float scale = (float) maxX / length;
			float reverse = 1 / scale;
			float yAdd = 4 - scale * 8 / 2F;

			stack.pushPose();

			stack.scale(scale, scale, scale);
			gui.getFontRenderer().draw(stack, text, (int) (x * reverse), (int) (y * reverse + yAdd), color);

			stack.popPose();
		}
	}

	protected boolean isPointInRegion(int mouseX, int mouseY) {
		return mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
	}

	public void updateVisiblity(int screenNumber) {
		boolean isVisible = false;
		for (int screen : screenNumbers) {
			if (screenNumber == screen) {
				isVisible = true;
				break;
			}
		}
		visible = isVisible;

	}

	@Override
	protected boolean clicked(double mouseX, double mouseY) {
		return this.active && this.visible && isPointInRegion((int) mouseX, (int) mouseY);
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return clicked(mouseX, mouseY);
	}

	@Override
	public void updateNarration(NarrationElementOutput pNarrationElementOutput) {

	}

	@Override
	public void playDownSound(SoundManager pHandler) {
	}
	
	@Override
	public Component getMessage() {
		return message.get();
	}
	
	public static enum OverdriveTextures implements ITexture {
		
		NONE(new ResourceLocation(""), 0, 0),
		PROGRESS_BARS(new ResourceLocation(References.ID, "textures/gui/progress/progress.png"), 256, 256),
		WHITE(new ResourceLocation("forge", "textures/white.png"), 256, 256),
		HOTBAR_BAR(new ResourceLocation(References.ID, "textures/gui/base/hotbar_bar.png"), 5, 16),
		RUNNING_INDICATOR(new ResourceLocation(References.ID, "textures/gui/base/indicator.png"), 256, 256),
		TABLET_SCREEN(new ResourceLocation(References.ID, "textures/gui/misc/screen.png"), 118, 48),
		ORDER_SEARCH_BAR(new ResourceLocation(References.ID, "textures/gui/misc/order_bar.png"), 158, 20),
		VERTICAL_SLIDER_BG(new ResourceLocation(References.ID, "textures/gui/slider/vertical_slider_bg.png"), 15, 30),
		VERTICAL_SLIDER_ACTIVE(new ResourceLocation(References.ID, "textures/gui/slider/vertical_slider_active.png"), 15, 15),
		VERTICAL_SLIDER_INACTIVE(new ResourceLocation(References.ID, "textures/gui/slider/vertical_slider_inactive.png"), 15, 15);

		private final ResourceLocation texture;
		private final int textureWidth;
		private final int textureHeight;
		
		private OverdriveTextures(ResourceLocation texture, int textureWidth, int textureHeight) {
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
