package matteroverdrive.compatibility.jei.utils.label.types;

import matteroverdrive.common.recipe.AbstractOverdriveRecipe;
import matteroverdrive.compatibility.jei.categories.base.AbstractOverdriveRecipeCategory;
import matteroverdrive.compatibility.jei.utils.IPseudoRecipe;
import matteroverdrive.compatibility.jei.utils.label.GenericLabelWrapper;
import matteroverdrive.core.utils.UtilsText;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;

public class PowerUsageLabelWrapper extends GenericLabelWrapper {

	private boolean constPowerUsage = false;
	private double constPower = 0.0;
	
	public PowerUsageLabelWrapper(int color, int xPos, int yPos) {
		super(color, xPos, yPos, "usagepertick");
	}
	
	public PowerUsageLabelWrapper setConstUsage(double usage) {
		constPowerUsage = true;
		constPower = usage;
		return this;
	}

	@Override
	public MutableComponent getComponent(AbstractOverdriveRecipeCategory<?> category, Object recipe) {
		if(recipe instanceof AbstractOverdriveRecipe overdrive) {
			return makeComponent(constPowerUsage ? constPower : overdrive.getUsagePerTick());
		} else if (recipe instanceof AbstractCookingRecipe cooking) {
			return makeComponent(constPower);
		} else if (recipe instanceof IPseudoRecipe pseudo) {
			return makeComponent(pseudo.getUsagePerTick());
		}
		return Component.empty();
	}
	
	public MutableComponent makeComponent(double usage) {
		return UtilsText.jeiTranslated(name, UtilsText.formatPowerValue(usage));
	}
}
