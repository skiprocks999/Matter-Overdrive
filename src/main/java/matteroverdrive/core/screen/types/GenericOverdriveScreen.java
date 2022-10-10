package matteroverdrive.core.screen.types;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.client.ClientReferences.Colors;
import matteroverdrive.core.inventory.GenericInventory;
import matteroverdrive.core.screen.component.button.ButtonGeneric;
import matteroverdrive.core.screen.component.button.ButtonGeneric.ButtonType;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public abstract class GenericOverdriveScreen<T extends GenericInventory> extends GenericSwappableScreen<T> {

	private static final int OFFSET = 37;
	
	private static final int IMAGE_WIDTH = 120;
	private static final int IMAGE_HEIGHT = 76; 
	
	private static final int MIN_HEIGHT = 63;
	private static final int MIN_WIDTH = 89;
	
	private static final float TITLE_OFFSET_RATIO = 144.0F / 224.0F;

	public GenericOverdriveScreen(T menu, Inventory playerinventory, Component title, GuiTextures texture, int guiWidth, int guiHeight) {
		super(menu, playerinventory, title, texture, guiWidth, guiHeight);
		if(guiWidth < MIN_WIDTH) {
			throw new UnsupportedOperationException("Gui width must be a minumum of " + MIN_WIDTH);
		}
		if(guiHeight < MIN_HEIGHT) {
			throw new UnsupportedOperationException("Gui height must be a minumum of " + MIN_HEIGHT);
		}
	}
	
	public GenericOverdriveScreen(T menu, Inventory playerinventory, Component title, int guiWidth, int guiHeight) {
		this(menu, playerinventory, title, GuiTextures.OVERDRIVE_MENU, guiWidth, guiHeight);
	}

	@Override
	public void setScreenParams() {
		leftPos -= OFFSET;
		titleLabelX += OFFSET;
	}

	@Override
	protected void renderLabels(PoseStack stack, int x, int y) {
		float length = font.width(this.title);
		float headerWidth = (float) imageWidth * TITLE_OFFSET_RATIO;
		float offset = (headerWidth - length) / 2.0F;
		this.font.draw(stack, this.title, (float) this.titleLabelX + 3 + offset, (float) this.titleLabelY + 1,
				Colors.MATTER.getColor());
	}
	
	
	@Override
	protected void renderBg(PoseStack stack, float partialTick, int x, int y) {
		UtilsRendering.bindTexture(background.getTexture());
		
		int upLCWidth = 56;
		int upLCHeight = 41;
		
		int upRCWidth = 33;
		int upRCHeight = 41;
		
		int lowLCWidth = 56;
		int lowLCHeight = 22;
		
		int lowRCWidth = 33;
		int lowRCHeight = 22;
		
		int topStripWidth = 31;
		int topStripHeight = 41;
		
		int bottomStripWidth = 31;
		int bottomStripHeight = 22;
		
		int leftStripWidth = 56;
		int leftStripHeight = 13;
		
		int rightStripWidth = 33;
		int rightStripHeight = 13;
		
		int centerWidth = 31;
		int centerHeight = 13;
		
		int addWidth = imageWidth - upLCWidth - upRCWidth;
		int addHeight = imageHeight - upLCHeight - lowLCHeight;
		
		//upper left corner
		blit(stack, getXPos(), getYPos(), 0, 0, upLCWidth, upLCHeight, IMAGE_WIDTH, IMAGE_HEIGHT);
		
		//bottom left corner
		blit(stack, getXPos(), getYPos() + upLCHeight + addHeight, 0, upLCHeight + leftStripHeight, lowLCWidth, lowLCHeight, IMAGE_WIDTH, IMAGE_HEIGHT);
		
		//upper right corner
		blit(stack, getXPos() + upLCWidth + addWidth, getYPos(), upLCWidth + topStripWidth, 0, upRCWidth, upRCHeight, IMAGE_WIDTH, IMAGE_HEIGHT);
		
		//bottom right corner
		blit(stack, getXPos() + lowLCWidth + addWidth, getYPos() + upRCHeight + addHeight, lowLCWidth + bottomStripWidth, upRCHeight + rightStripHeight, lowRCWidth, lowRCHeight, IMAGE_WIDTH, IMAGE_HEIGHT);
		
		//top strip
		int wholeWidth = addWidth / topStripWidth;
		int remainWidth = addWidth % topStripWidth;
		
		for(int i = 0; i < wholeWidth; i++) {
			blit(stack, getXPos() + upLCWidth + topStripWidth * i, getYPos(), upLCWidth, 0, topStripWidth, topStripHeight, IMAGE_WIDTH, IMAGE_HEIGHT);
		}
		blit(stack, getXPos() + upLCWidth + topStripWidth * wholeWidth, getYPos(), upLCWidth, 0, remainWidth, topStripHeight, IMAGE_WIDTH, IMAGE_HEIGHT);
		
		
		//left strip
		int wholeHeight = addHeight / leftStripHeight;
		int remainHeight = addHeight % leftStripHeight;
		
		for(int i = 0; i < wholeHeight; i++) {
			blit(stack, getXPos(), getYPos() + upLCHeight + leftStripHeight * i, 0, upLCHeight, leftStripWidth, leftStripHeight, IMAGE_WIDTH, IMAGE_HEIGHT);
		}
		blit(stack, getXPos(), getYPos() + upLCHeight + leftStripHeight * wholeHeight, 0, upLCHeight, leftStripWidth, remainHeight, IMAGE_WIDTH, IMAGE_HEIGHT);
		
		
		//bottom strip
		wholeWidth = addWidth / bottomStripWidth;
		remainWidth = addWidth % bottomStripWidth;
		
		for(int i = 0; i < wholeWidth; i++) {
			blit(stack, getXPos() + lowLCWidth + bottomStripWidth * i, getYPos() + upLCHeight + addHeight, upLCWidth, upLCHeight + leftStripHeight, bottomStripWidth, bottomStripHeight, IMAGE_WIDTH, IMAGE_HEIGHT);
		}
		blit(stack, getXPos() + lowLCWidth + bottomStripWidth * wholeWidth, getYPos() + upLCHeight + addHeight, upLCWidth, upLCHeight + leftStripHeight, remainWidth, bottomStripHeight, IMAGE_WIDTH, IMAGE_HEIGHT);
		
		//right strip
		wholeHeight = addHeight / rightStripHeight;
		remainHeight = addHeight % rightStripHeight;
		
		for(int i = 0; i < wholeHeight; i++) {
			blit(stack, getXPos() + upLCWidth + addWidth, getYPos() + upRCHeight + rightStripHeight * i, upLCWidth + topStripWidth, upRCHeight, rightStripWidth, rightStripHeight, IMAGE_WIDTH, IMAGE_HEIGHT);
		}
		blit(stack, getXPos() + upLCWidth + addWidth, getYPos() + upRCHeight + rightStripHeight * wholeHeight, upLCWidth + topStripWidth, upRCHeight, rightStripWidth, remainHeight, IMAGE_WIDTH, IMAGE_HEIGHT);
		
		//center
		wholeWidth = addWidth / centerWidth;
		remainWidth = addWidth % centerWidth;
		
		wholeHeight = addHeight / centerHeight;
		remainHeight = addHeight % centerHeight;
		
		for(int i = 0; i <= wholeHeight; i++) {
			for(int j = 0; j < wholeWidth; j++) {
				if(i < wholeHeight) {
					blit(stack, getXPos() + upLCWidth + centerWidth * j, getYPos() + topStripHeight + centerHeight * i, upLCWidth, upLCHeight, centerWidth, centerHeight, IMAGE_WIDTH, IMAGE_HEIGHT);
				} else {
					blit(stack, getXPos() + upLCWidth + centerWidth * j, getYPos() + topStripHeight + centerHeight * i, upLCWidth, upLCHeight, centerWidth, remainHeight, IMAGE_WIDTH, IMAGE_HEIGHT);
				}
				
			}
			if(i < wholeHeight) {
				blit(stack, getXPos() + upLCWidth + centerWidth * wholeWidth, getYPos() + topStripHeight + centerHeight * i, upLCWidth, upLCHeight, remainWidth, centerHeight, IMAGE_WIDTH, IMAGE_HEIGHT);
			} else {
				blit(stack, getXPos() + upLCWidth + centerWidth * wholeWidth, getYPos() + topStripHeight + centerHeight * i, upLCWidth, upLCHeight, remainWidth, remainHeight, IMAGE_WIDTH, IMAGE_HEIGHT);
			}
			
		}
		
	}
	
	public ButtonGeneric getCloseButton(int x, int y) {
		return new ButtonGeneric(this, x, y, ButtonType.CLOSE_SCREEN, button -> onClose());
	}
	
}
