package matteroverdrive.compatibility.jei.utils.gui.item;

import matteroverdrive.References;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import net.minecraft.resources.ResourceLocation;

public class DefaultItemSlotWrapper extends GenericItemSlotWrapper {

	public static final int ITEM_WIDTH = 16;
	
	private final SlotType type;
	
	private final int itemXOffset;
	private final int itemYOffset;
	private final boolean isVisibleOnly;
	private final int textureWidth;
	private final int textureHeight;
	
	public DefaultItemSlotWrapper(SlotType type, int xStart, int yStart, boolean isVisibleOnly) {
		super(new ResourceLocation(References.ID + ":textures/gui/slot/" + type.getName()), xStart, yStart, type.getTextureX(), type.getTextureY(), type.getWidth(), type.getHeight());
		this.type = type;
		if (isMainSlot()) {
			itemXOffset = (int) ((22 - ITEM_WIDTH) / 2);
		} else {
			itemXOffset = (int) ((type.getWidth() - ITEM_WIDTH) / 2);
		}
		itemYOffset = (type.getHeight() - ITEM_WIDTH) / 2;
		this.isVisibleOnly = isVisibleOnly;
		textureWidth = type.getWidth();
		textureHeight = type.getHeight();
	}

	@Override
	public int itemXStart() {
		return getXPos() + itemXOffset;
	}

	@Override
	public int itemYStart() {
		return getYPos() + itemYOffset;
	}

	@Override
	public boolean isVisibleOnly() {
		return isVisibleOnly;
	}
	
	@Override
	public int getTextureWidth() {
		return textureWidth;
	}
	
	@Override
	public int getTextureHeight() {
		return textureHeight;
	}
	
	private boolean isMainSlot() {
		return type == SlotType.MAIN || type == SlotType.MAIN_ACTIVE || type == SlotType.MAIN_DARK;
	}

}
