package matteroverdrive.compatibility.jei.utils.gui.item;

import matteroverdrive.compatibility.jei.utils.gui.ScreenObjectWrapper;
import matteroverdrive.core.screen.component.utils.ITexture;

public abstract class GenericItemSlotWrapper extends ScreenObjectWrapper {

	public GenericItemSlotWrapper(ITexture texture, int xStart, int yStart, int textX, int textY, int height, int width) {
		super(texture, xStart, yStart, textX, textY, height, width);
	}

	public abstract int itemXStart();

	public abstract int itemYStart();
	
	public abstract boolean isVisibleOnly();

}
