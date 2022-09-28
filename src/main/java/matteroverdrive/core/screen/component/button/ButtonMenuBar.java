package matteroverdrive.core.screen.component.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.MatterOverdrive;
import matteroverdrive.client.ClientReferences.Colors;
import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.utils.AbstractOverdriveButton;
import matteroverdrive.core.utils.UtilsRendering;
import matteroverdrive.core.utils.UtilsText;
import matteroverdrive.registry.SoundRegistry;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;

public class ButtonMenuBar extends AbstractOverdriveButton {

	public static final int EXTEND_DISTANCE = 32;

	private static final int IMAGE_WIDTH = 87;
	private static final int IMAGE_HEIGHT = 43;
	
	public boolean isExtended;
	private boolean isPressed;
	
	private final int height;

	public ButtonMenuBar(GenericScreen<?> gui, int x, int y, int height, boolean inidialCondition, OnPress press) {
		super(gui, x, y, 16, 143, Component.empty(), press, (button, stack, mouseX, mouseY) -> {
			ButtonMenuBar bar = (ButtonMenuBar) button;
			if (bar.isExtended) {
				bar.gui.renderTooltip(stack, UtilsText.tooltip("closemenu"), mouseX, mouseY);
			} else {
				bar.gui.renderTooltip(stack, UtilsText.tooltip("openmenu"), mouseX, mouseY);
			}
		});
		isExtended = inidialCondition;
		if (isExtended) {
			this.x += EXTEND_DISTANCE;
		}
		this.height = height; 
	}

	@Override
	public void renderBackground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		UtilsRendering.setShaderColor(Colors.WHITE.getColor());
		UtilsRendering.bindTexture(ButtonTextures.MENU_BAR.getTexture());
		
		renderButton(stack);
		
		if (isExtended) {
			renderBar(stack);
		}
	}
	
	private void renderButton(PoseStack stack) {
		
		int tcWidth = 16;
		int tcHeight = 5;
		
		int midWidth = 16;
		int midHeight = 32;
		
		int lcWidth = 16;
		int lcHeight = 6;
		
		int pressed = isPressed ? 16 : 0;
		
		int addHeight = height - tcHeight - lcHeight;
		
		//top corner
		blit(stack, this.x, this.y, pressed, 0, tcWidth, tcHeight, IMAGE_WIDTH, IMAGE_HEIGHT);
		
		//middle
		int wholeHeight = addHeight / midHeight;
		int remainHeight = addHeight % midHeight;
		
		for(int i = 0; i < wholeHeight; i++) {
			blit(stack, this.x, this.y + tcHeight + midHeight * i, pressed, tcHeight, midWidth, midHeight, IMAGE_WIDTH, IMAGE_HEIGHT);
		}
		blit(stack, this.x, this.y + tcHeight + midHeight * wholeHeight, pressed, tcHeight, midWidth, remainHeight, IMAGE_WIDTH, IMAGE_HEIGHT);
		
		//bottom corner
		blit(stack, this.x, this.y + tcHeight + addHeight, pressed, tcHeight + midHeight, lcWidth, lcHeight, IMAGE_WIDTH, IMAGE_HEIGHT);
	
		//arrow
		
		int arrowWidth = 15;
		int arrowHeight = 28;
		
		int arrowOffset = (addHeight - arrowHeight) / 2;
		
		blit(stack, this.x, this.y + tcHeight + arrowOffset, 68, 0, arrowWidth, arrowHeight, IMAGE_WIDTH, IMAGE_HEIGHT);
	
	}
	
	private void renderBar(PoseStack stack) {
		
		int tcWidth = 36;
		int tcHeight = 7;
		
		int midWidth = 32;
		int midHeight = 28;
		
		int lcWidth = 32;
		int lcHeight = 8;
		
		int addHeight = height - tcHeight - lcHeight;
		
		//top corner
		blit(stack, this.x - EXTEND_DISTANCE, this.y, 32, 0, tcWidth, tcHeight, IMAGE_WIDTH, IMAGE_HEIGHT);
		
		//middle
		int wholeHeight = addHeight / midHeight;
		int remainHeight = addHeight % midHeight;
		
		for(int i = 0; i < wholeHeight; i++) {
			blit(stack, this.x - EXTEND_DISTANCE, this.y + tcHeight + midHeight * i, 32, tcHeight, midWidth, midHeight, IMAGE_WIDTH, IMAGE_HEIGHT);
		}
		blit(stack, this.x - EXTEND_DISTANCE, this.y + tcHeight + midHeight * wholeHeight, 32, tcHeight, midWidth, remainHeight, IMAGE_WIDTH, IMAGE_HEIGHT);
		
		//bottom corner
		blit(stack, this.x - EXTEND_DISTANCE, this.y + tcHeight + addHeight, 32, tcHeight + midHeight, lcWidth, lcHeight, IMAGE_WIDTH, IMAGE_HEIGHT);
	}

	@Override
	public void onPress() {
		isPressed = true;
	}

	@Override
	public void onRelease(double pMouseX, double pMouseY) {
		this.onPress.onPress(this);
		isPressed = false;
		if (isExtended) {
			this.x -= EXTEND_DISTANCE;
		} else {
			this.x += EXTEND_DISTANCE;
		}
		isExtended = !isExtended;
	}

	public Boolean getIsExtended() {
		return isExtended;
	}

	@Override
	public void playDownSound(SoundManager pHandler) {
		float pitch = MatterOverdrive.RANDOM.nextFloat(0.9F, 1.1F);
		pHandler.play(SimpleSoundInstance.forUI(SoundRegistry.SOUND_BUTTON_EXPAND.get(), pitch));
	}

}
