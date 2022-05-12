package matteroverdrive.core.screen;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.References;
import matteroverdrive.core.inventory.GenericInventory;
import matteroverdrive.core.screen.component.utils.IGuiComponent;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public abstract class GenericVanillaScreen<T extends GenericInventory> extends GenericScreen<T> {

	protected ResourceLocation vanillaBg = new ResourceLocation(References.ID + ":textures/gui/base/base_vanilla.png");

	public GenericVanillaScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
	}

	@Override
	protected void renderLabels(PoseStack stack, int x, int y) {
		this.font.draw(stack, this.title, (float) this.titleLabelX, (float) this.titleLabelY, 4210752);
		this.font.draw(stack, this.playerInventoryTitle, (float) this.inventoryLabelX, (float) this.inventoryLabelY,
				4210752);
		int xAxis = x - (width - imageWidth) / 2;
		int yAxis = y - (height - imageHeight) / 2;
		for (IGuiComponent component : components) {
			component.renderForeground(stack, xAxis, yAxis);
		}
	}

	@Override
	protected void renderBg(PoseStack stack, float partialTick, int x, int y) {
		UtilsRendering.bindTexture(vanillaBg);
		int guiWidth = (width - imageWidth) / 2;
		int guiHeight = (height - imageHeight) / 2;
		blit(stack, guiWidth, guiHeight, 0, 248, imageWidth, 4);
		blit(stack, guiWidth, guiHeight + 4, 0, 0, imageWidth, imageHeight - 8);
		blit(stack, guiWidth, guiHeight + imageHeight - 4, 0, 252, imageWidth, 4);
		int xAxis = x - guiWidth;
		int yAxis = y - guiHeight;
		for (IGuiComponent component : components) {
			component.renderBackground(stack, xAxis, yAxis, guiWidth, guiHeight);
		}
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
