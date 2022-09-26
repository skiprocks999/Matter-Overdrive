package matteroverdrive.core.screen.component;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.utils.OverdriveScreenComponent;
import matteroverdrive.core.utils.UtilsRendering;

public class ScreenComponentHotbarBar extends OverdriveScreenComponent {

	private static final int HEIGHT = 5;
	private static final int WIDTH = 16;
	
	private final int width;

	public ScreenComponentHotbarBar(final GenericScreen<?> gui, final int x, final int y, final int width, final int[] screenNumbers) {
		super(OverdriveTextures.HOTBAR_BAR, gui, x, y, WIDTH, HEIGHT,screenNumbers);
		if(width < WIDTH) {
			throw new UnsupportedOperationException("Width must be a minimum of " + WIDTH);
		}
		this.width = width;
	}

	@Override
	public void renderBackground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		UtilsRendering.bindTexture(resource.getTexture());
		
		int lcWidth = 3;
		int lcHeight = HEIGHT;
		
		int midWidth = 10;
		int midHeight = HEIGHT;
		
		int rcWidth = 3;
		int rcHeight = HEIGHT;
		
		int addWidth = width - lcWidth - rcWidth;
		
		int wholeWidth = addWidth / midWidth;
		int remainWidth = addWidth % midWidth;
		
		//left corner
		blit(stack, this.x, this.y, 0, 0, lcWidth, lcHeight, WIDTH, HEIGHT);
		
		//middle
		for(int i = 0; i < wholeWidth; i++) {
			blit(stack, this.x + lcWidth + midWidth * i, this.y, lcWidth, 0, midWidth, midHeight, WIDTH, HEIGHT);
		}
		blit(stack, this.x + lcWidth + midWidth * wholeWidth, this.y, lcWidth, 0, remainWidth, midHeight, WIDTH, HEIGHT);
		
		//right corner
		blit(stack, this.x + lcWidth + addWidth, this.y, lcWidth + midWidth, 0, rcWidth, rcHeight, WIDTH, HEIGHT);
	}

}
