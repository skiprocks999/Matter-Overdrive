package matteroverdrive.core.screen.types;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.core.inventory.GenericInventory;
import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public abstract class GenericVanillaScreen<T extends GenericInventory> extends GenericScreen<T> {

	public GenericVanillaScreen(T menu, Inventory playerInventory, Component title, int guiWidth, int guiHeight) {
		super(menu, playerInventory, title, GuiTextures.VANILLA, guiWidth, guiHeight);
	}
	
	@Override
	public void setScreenParams() {
		
	}

	@Override
	protected void renderBg(PoseStack stack, float partialTick, int x, int y) {
		UtilsRendering.bindTexture(background.getTexture());
		int xPos = getXPos();
		int yPos = getYPos();
		blit(stack, xPos, yPos, 0, 248, imageWidth, 4);
		blit(stack, xPos, yPos + 4, 0, 0, imageWidth, imageHeight - 8);
		blit(stack, xPos, yPos + imageHeight - 4, 0, 252, imageWidth, 4);
	}
	
	@Override
	public int getScreenNumber() {
		return 0;
	}

}
