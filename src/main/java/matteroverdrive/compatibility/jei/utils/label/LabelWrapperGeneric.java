package matteroverdrive.compatibility.jei.utils.label;

import matteroverdrive.compatibility.jei.categories.base.AbstractOverdriveRecipeCategory;
import matteroverdrive.core.utils.UtilsText;
import net.minecraft.network.chat.MutableComponent;

public class LabelWrapperGeneric {

	protected final int color;
	protected final int yPos;
	protected final int xPos;
	protected final String name;

	public LabelWrapperGeneric(int color, int xPos, int yPos, String name) {
		this.color = color;
		this.yPos = yPos;
		this.xPos = xPos;
		this.name = name;
	}

	public int getColor() {
		return color;
	}

	public int getYPos() {
		return yPos;
	}

	public int getXPos() {
		return xPos;
	}

	public MutableComponent getComponent(AbstractOverdriveRecipeCategory<?> category, Object recipe) {
		return UtilsText.jeiTranslated(name);
	}
	
}
