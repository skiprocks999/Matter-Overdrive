package matteroverdrive.compatibility.jei.utils.gui.item;

import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;

public class DefaultItemSlotWrapper extends GenericItemSlotWrapper {

	public static final int ITEM_WIDTH = 16;
	
	private final SlotType type;
	
	private final int itemXOffset;
	private final int itemYOffset;
	private final boolean isVisibleOnly;
	
	public DefaultItemSlotWrapper(SlotType type, int xStart, int yStart, boolean isVisibleOnly) {
		super(type, xStart, yStart, type.getTextureU(), type.getTextureV(), type.getUWidth(), type.getVHeight());
		this.type = type;
		if (isMainSlot()) {
			itemXOffset = (int) ((22 - ITEM_WIDTH) / 2);
		} else {
			itemXOffset = (int) ((type.getTextureWidth() - ITEM_WIDTH) / 2);
		}
		itemYOffset = (type.getTextureHeight() - ITEM_WIDTH) / 2;
		this.isVisibleOnly = isVisibleOnly;
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
	
	private boolean isMainSlot() {
		return type == SlotType.MAIN || type == SlotType.MAIN_ACTIVE || type == SlotType.MAIN_DARK;
	}

}
