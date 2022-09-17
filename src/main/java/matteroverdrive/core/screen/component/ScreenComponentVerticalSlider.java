package matteroverdrive.core.screen.component;

import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.utils.OverdriveScreenComponent;
import matteroverdrive.core.utils.UtilsRendering;

public class ScreenComponentVerticalSlider extends OverdriveScreenComponent {

	private int sliderYOffset = 0;
	private boolean active = false;

	private boolean isHeld = false;

	private Consumer<Integer> sliderDragConsumer;
	private Consumer<Integer> sliderClickConsumer;

	public ScreenComponentVerticalSlider(GenericScreen<?> gui, int x, int y, int height, int[] screenNumbers) {
		super(OverdriveTextures.NONE, gui, x, y, 15, height < 30 ? 30 : height, screenNumbers);
	}

	public void setDragConsumer(Consumer<Integer> responder) {
		sliderDragConsumer = responder;
	}

	public void setClickConsumer(Consumer<Integer> responder) {
		sliderClickConsumer = responder;
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		// was on top of slider
		if (isHeld && sliderDragConsumer != null) {
			sliderDragConsumer.accept((int) mouseY);
		}
		super.mouseMoved(mouseX, mouseY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		// mouse clicked on slider
		if (isPointInRegion((int) mouseX, (int) mouseY)) {
			isHeld = true;
		}
		if (!isHeld && isPointInSlider((int) mouseX, (int) mouseY) && sliderClickConsumer != null) {
			sliderClickConsumer.accept((int) mouseY);
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		isHeld = false;
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	protected boolean isPointInRegion(int mouseX, int mouseY) {
		return mouseX > this.x && mouseX < this.x + width && mouseY >= this.y + 2 + sliderYOffset
				&& this.y <= this.y + 2 + sliderYOffset + 15;
	}

	@Override
	public void renderBackground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		UtilsRendering.bindTexture(OverdriveTextures.VERTICAL_SLIDER_BG.getTexture());
		blit(stack, x - 1, y, 15, 6, 0, 0, 15, 6, 15, 30);
		int permutations = (int) ((double) (height - 12) / 18.0D);
		int remainder = height - permutations * 18 - 12;
		for (int i = 0; i < permutations; i++) {
			blit(stack, x - 1, y + 6 + i * 18, 15, 18, 0, 6, 15, 18, 15, 30);
		}
		blit(stack, x - 1, y + 6 + 18 * permutations, 15, remainder, 0, 6, 15, remainder, 15, 30);
		blit(stack, x - 1, y + height - 6, 15, 6, 0, 24, 15, 6, 15, 30);
		if (active) {
			UtilsRendering.bindTexture(OverdriveTextures.VERTICAL_SLIDER_ACTIVE.getTexture());
		} else {
			UtilsRendering.bindTexture(OverdriveTextures.VERTICAL_SLIDER_INACTIVE.getTexture());
		}
		blit(stack, x - 1, y + 2 + sliderYOffset, 15, 15, 0, 0, 15, 15, 15, 15);
	}

	public void updateActive(boolean active) {
		this.active = active;
	}

	public void setSliderYOffset(int offset) {
		sliderYOffset = Math.min(offset, height - 4 - 15);
	}

	protected boolean isPointInSlider(int mouseX, int mouseY) {
		return mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
	}

	public boolean isSliderActive() {
		return active;
	}

	public boolean isSliderHeld() {
		return isHeld;
	}

}
