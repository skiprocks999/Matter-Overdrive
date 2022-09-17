package matteroverdrive.compatibility.jei.utils.gui.item;

import matteroverdrive.compatibility.jei.utils.gui.ScreenObjectWrapper;
import net.minecraft.resources.ResourceLocation;

public abstract class GenericItemSlotWrapper extends ScreenObjectWrapper {

	public GenericItemSlotWrapper(ResourceLocation texture, int xStart, int yStart, int textX, int textY, int height, int width) {
		super(texture, xStart, yStart, textX, textY, height, width);
	}

	public abstract int itemXStart();

	public abstract int itemYStart();
	
	public abstract boolean isVisibleOnly();

}
