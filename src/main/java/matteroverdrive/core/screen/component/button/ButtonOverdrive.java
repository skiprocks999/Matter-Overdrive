package matteroverdrive.core.screen.component.button;

import java.util.function.Consumer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.References;
import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.utils.AbstractOverdriveButton;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ButtonOverdrive extends AbstractOverdriveButton {

	private static final String BASE_TEXTURE_LOC = "textures/gui/button/";

	protected static final ResourceLocation DEFAULT = new ResourceLocation(References.ID,
			BASE_TEXTURE_LOC + "button_normal.png");
	protected static final ResourceLocation HOVERED = new ResourceLocation(References.ID,
			BASE_TEXTURE_LOC + "button_over.png");
	protected static final ResourceLocation PRESSED = new ResourceLocation(References.ID,
			BASE_TEXTURE_LOC + "button_over_dark.png");

	protected static final ResourceLocation DEFAULT_LEFT = new ResourceLocation(References.ID,
			BASE_TEXTURE_LOC + "button_normal_left.png");
	protected static final ResourceLocation HOVERED_LEFT = new ResourceLocation(References.ID,
			BASE_TEXTURE_LOC + "button_over_left.png");
	protected static final ResourceLocation PRESSED_LEFT = new ResourceLocation(References.ID,
			BASE_TEXTURE_LOC + "button_over_dark_left.png");

	protected static final ResourceLocation DEFAULT_RIGHT = new ResourceLocation(References.ID,
			BASE_TEXTURE_LOC + "button_normal_right.png");
	protected static final ResourceLocation HOVERED_RIGHT = new ResourceLocation(References.ID,
			BASE_TEXTURE_LOC + "button_over_right.png");
	protected static final ResourceLocation PRESSED_RIGHT = new ResourceLocation(References.ID,
			BASE_TEXTURE_LOC + "button_over_dark_right.png");

	public boolean isPressed;

	protected ResourceLocation defaultText;
	protected ResourceLocation hoveredText;
	protected ResourceLocation pressedText;

	protected int textColor = UtilsRendering.TEXT_BLUE;
	protected Consumer<SoundManager> downSound = null;

	public ButtonOverdrive(GenericScreen<?> gui, int x, int y, int width, int height, Component message,
			OnPress onPress, OnTooltip onTooltip) {
		super(gui, x, y, width, height, message, onPress, onTooltip);
		defaultText = DEFAULT;
		hoveredText = HOVERED;
		pressedText = PRESSED;
	}

	public ButtonOverdrive(GenericScreen<?> gui, int x, int y, int width, int height, Component message,
			OnPress onPress) {
		super(gui, x, y, width, height, message, onPress);
		defaultText = DEFAULT;
		hoveredText = HOVERED;
		pressedText = PRESSED;
	}

	public ButtonOverdrive setLeft() {
		defaultText = DEFAULT_LEFT;
		hoveredText = HOVERED_LEFT;
		pressedText = PRESSED_LEFT;
		return this;
	}

	public ButtonOverdrive setRight() {
		defaultText = DEFAULT_RIGHT;
		hoveredText = HOVERED_RIGHT;
		pressedText = PRESSED_RIGHT;
		return this;
	}

	public ButtonOverdrive setColor(int color) {
		textColor = color;
		return this;
	}

	public ButtonOverdrive setSound(Consumer<SoundManager> sound) {
		downSound = sound;
		return this;
	}

	@Override
	public void renderBackground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

		if (isPressed) {
			UtilsRendering.bindTexture(pressedText);
		} else if (isHoveredOrFocused()) {
			UtilsRendering.bindTexture(hoveredText);
		} else {
			UtilsRendering.bindTexture(defaultText);
		}

		drawButton(stack, this.x, this.y, this.width, this.height);

	}

	@Override
	public void renderForeground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {

		Minecraft minecraft = Minecraft.getInstance();
		Font font = minecraft.font;
		drawCenteredString(stack, font, getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2,
				getTextColor());

	}

	public static void drawButton(PoseStack stack, int x, int y, int butWidth, int butHeight) {
		if (butWidth < 18) {
			if (butHeight < 18) {
				blit(stack, x, y, butWidth, butHeight, 0, 0, butWidth, butHeight, butWidth, butHeight);
			} else {
				blit(stack, x, y, butWidth, 7, 0, 0, butWidth, 7, butWidth, 18);

				int sectionHeight = butHeight - 14;
				int heightIterations = sectionHeight / 4;
				int remainderHeight = sectionHeight % 4;

				int heightOffset = 7;
				for (int i = 0; i < heightIterations; i++) {
					blit(stack, x, y + heightOffset, butWidth, 4, 0, 7, butWidth, 4, butWidth, 18);
					heightOffset += 4;
				}
				blit(stack, x, y + heightOffset, butWidth, remainderHeight, 0, 7, butWidth, remainderHeight, butWidth,
						18);

				blit(stack, x, y + butHeight - 7, butWidth, 7, 0, 11, butWidth, 7, butWidth, 18);
			}
		} else if (butHeight < 18) {
			blit(stack, x, y, 7, butHeight, 0, 0, 7, butHeight, 18, butHeight);

			int sectionWidth = butWidth - 14;
			int widthIterations = sectionWidth / 4;
			int remainderWidth = sectionWidth % 4;

			int widthOffset = 7;
			for (int i = 0; i < widthIterations; i++) {
				blit(stack, x + widthOffset, y, 4, butHeight, 7, 0, 4, butHeight, 18, butHeight);
				widthOffset += 4;
			}
			blit(stack, x + widthOffset, y, remainderWidth, butHeight, 7, 0, remainderWidth, butHeight, 18, butHeight);

			blit(stack, x + butWidth - 7, y, 7, butHeight, 11, 0, 7, butHeight, 18, butHeight);
		} else {
			// the button is >= 18x18 at this point

			// draw squares
			int squareWidth = butWidth - 10;
			int squareWidthIterations = squareWidth / 8;
			int remainderSquareWidth = squareWidth % 8;

			int squareHeight = butHeight - 10;
			int squareHeightIterations = squareHeight / 8;
			int remainderSquareHeight = squareHeight % 8;

			int heightOffset = 5;
			int widthOffset = 5;

			for (int i = 0; i <= squareHeightIterations; i++) {
				int height = i == squareHeightIterations ? remainderSquareHeight : 8;
				for (int j = 0; j < squareWidthIterations; j++) {
					draw(stack, x, y, widthOffset, heightOffset, 5, 5, 8, height);
					widthOffset += 8;
				}
				draw(stack, x, y, widthOffset, heightOffset, 5, 5, remainderSquareWidth, height);
				widthOffset = 5;
				heightOffset += 8;
			}

			// draw tl corner
			draw(stack, x, y, 0, 0, 0, 0, 8, 8);

			// draw top strip

			int stripWidth = butWidth - 14;
			int stripWidthIterations = stripWidth / 4;
			int remainderStripWidth = stripWidth % 4;

			int stripHeight = butHeight - 14;
			int stripHeightIterations = stripHeight / 4;
			int remainderStripHeight = stripHeight % 4;

			widthOffset = 7;
			for (int i = 0; i < stripWidthIterations; i++) {
				draw(stack, x, y, widthOffset, 0, 7, 0, 4, 5);
				widthOffset += 4;
			}
			draw(stack, x, y, widthOffset, 0, 7, 0, remainderStripWidth, 5);

			// draw tr corner
			draw(stack, x, y, butWidth - 8, 0, 10, 0, 8, 8);

			// draw left strip
			heightOffset = 7;
			for (int i = 0; i < stripHeightIterations; i++) {
				draw(stack, x, y, 0, heightOffset, 0, 7, 5, 4);
				heightOffset += 4;
			}
			draw(stack, x, y, 0, heightOffset, 0, 5, 5, remainderStripHeight);

			// draw right strip
			heightOffset = 7;
			widthOffset = butWidth - 5;
			for (int i = 0; i < stripHeightIterations; i++) {
				draw(stack, x, y, widthOffset, heightOffset, 13, 7, 5, 4);
				heightOffset += 4;
			}
			draw(stack, x, y, widthOffset, heightOffset, 13, 7, 5, remainderStripHeight);

			// draw bl corner
			draw(stack, x, y, 0, butHeight - 8, 0, 10, 8, 8);

			// draw bottom strip
			heightOffset = butHeight - 5;
			widthOffset = 7;
			for (int i = 0; i < stripWidthIterations; i++) {
				draw(stack, x, y, widthOffset, heightOffset, 7, 13, 4, 5);
				widthOffset += 4;
			}
			draw(stack, x, y, widthOffset, heightOffset, 7, 13, remainderStripWidth, 5);

			// draw br corner
			draw(stack, x, y, butWidth - 8, butHeight - 8, 10, 10, 8, 8);

		}

	}

	private static void draw(PoseStack stack, int x, int y, int widthOffset, int heightOffset, int textXOffset,
			int textYOffset, int width, int height) {
		blit(stack, x + widthOffset, y + heightOffset, width, height, textXOffset, textYOffset, width, height, 18, 18);
	}

	public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
		if (isPressed || this.isValidClickButton(pButton)) {
			this.onRelease(pMouseX, pMouseY);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onPress() {
		isPressed = true;
		super.onPress();
	}

	@Override
	public void onRelease(double pMouseX, double pMouseY) {
		super.onRelease(pMouseX, pMouseY);
		isPressed = false;
	}

	@Override
	public void playDownSound(SoundManager pHandler) {
		if (downSound != null) {
			downSound.accept(pHandler);
		} else {
			super.playDownSound(pHandler);
		}
	}

	public int getTextColor() {
		return textColor;
	}

	@Override
	public void updateVisiblity(int screenNumber) {

	}

}
