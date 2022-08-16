package matteroverdrive.core.screen.types;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.References;
import matteroverdrive.core.inventory.GenericInventory;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public abstract class GenericOverdriveScreen<T extends GenericInventory> extends GenericSwappableScreen<T> {

	protected static final ResourceLocation BACKGROUND = new ResourceLocation(
			References.ID + ":textures/gui/base/base_gui.png");
	
	private static final int OFFSET = 37;

	public static final int GUI_WIDTH = 224;
	public static final int GUI_HEIGHT = 176;
	
	public GenericOverdriveScreen(T menu, Inventory playerinventory, Component title) {
		this(menu, playerinventory, title, BACKGROUND);
	}
	
	public GenericOverdriveScreen(T menu, Inventory playerinventory, Component title, ResourceLocation background) {
		super(menu, playerinventory, title, background);
	}
	
	@Override
	public void setScreenParams() {
		leftPos -= OFFSET;
		imageWidth = GUI_WIDTH;
		imageHeight = GUI_HEIGHT;
		titleLabelX += OFFSET;
	}
	
	@Override
	protected void renderLabels(PoseStack stack, int x, int y) {
		float length = font.width(this.title);
		float offset = (144.0F - length) / 2.0F;
		this.font.draw(stack, this.title, (float) this.titleLabelX + 3 + offset, (float) this.titleLabelY + 1,
				UtilsRendering.TITLE_BLUE);
	}

}
