package matteroverdrive.core.screen.types;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.References;
import matteroverdrive.core.inventory.GenericInventory;
import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public abstract class GenericVanillaScreen<T extends GenericInventory> extends GenericScreen<T> {

	protected static final ResourceLocation BACKGROUND = new ResourceLocation(
			References.ID + ":textures/gui/base/base_vanilla.png");

	public GenericVanillaScreen(T menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title, BACKGROUND);
	}

	@Override
	protected void renderBg(PoseStack stack, float partialTick, int x, int y) {
		UtilsRendering.bindTexture(background);
		int xPos = getXPos();
		int yPos = getYPos();
		blit(stack, xPos, yPos, 0, 248, imageWidth, 4);
		blit(stack, xPos, yPos + 4, 0, 0, imageWidth, imageHeight - 8);
		blit(stack, xPos, yPos + imageHeight - 4, 0, 252, imageWidth, 4);
	}

	@Override
	public void setScreenParams() {
		imageHeight += playerInvOffset;
	}

	@Override
	public int getScreenNumber() {
		return 0;
	}

}
