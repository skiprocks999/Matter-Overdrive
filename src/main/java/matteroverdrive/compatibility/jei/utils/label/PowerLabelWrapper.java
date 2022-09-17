package matteroverdrive.compatibility.jei.utils.label;

import matteroverdrive.common.recipe.AbstractOverdriveRecipe;
import matteroverdrive.compatibility.jei.categories.OverdriveRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class PowerLabelWrapper extends GenericLabelWrapper {

	private double wattage;
	private int voltage;

	public PowerLabelWrapper(int xPos, int yPos, double joulesPerTick, int voltage) {
		super(0xFF808080, yPos, xPos, POWER);
		wattage = joulesPerTick * 20 / 1000.0;
		this.voltage = voltage;
	}

	@Override
	public MutableComponent getComponent(OverdriveRecipeCategory<?> category, AbstractOverdriveRecipe recipe) {
		return Component.translatable(getLocation(), voltage, wattage);
	}
}
