package matteroverdrive.compatibility.jei.utils.label;

import matteroverdrive.common.recipe.AbstractOverdriveRecipe;
import matteroverdrive.compatibility.jei.categories.OverdriveRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class GenericLabelWrapper {

	protected static final String POWER = "power";

	private int COLOR;
	private int Y_POS;
	private int X_POS;
	private String NAME;

	public GenericLabelWrapper(int color, int yPos, int endXPos, String name) {
		COLOR = color;
		Y_POS = yPos;
		X_POS = endXPos;
		NAME = name;
	}

	public int getColor() {
		return COLOR;
	}

	public int getYPos() {
		return Y_POS;
	}

	public int getXPos() {
		return X_POS;
	}

	public String getLocation() {
		return "jei.guilabel." + NAME;
	}

	public MutableComponent getComponent(OverdriveRecipeCategory<?> category, AbstractOverdriveRecipe recipe) {
		return Component.translatable(getLocation());
	}
}
