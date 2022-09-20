package matteroverdrive.compatibility.jei.utils.label.types;

import matteroverdrive.common.recipe.AbstractOverdriveRecipe;
import matteroverdrive.compatibility.jei.categories.base.AbstractOverdriveRecipeCategory;
import matteroverdrive.compatibility.jei.utils.IPseudoRecipe;
import matteroverdrive.compatibility.jei.utils.label.LabelWrapperGeneric;
import matteroverdrive.core.utils.UtilsText;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;

public class LabelWrapperPowerConsumed extends LabelWrapperGeneric {
	
	private boolean constPowerUsage = false;
	private double constPower = 0.0;
	
	private boolean constantTime = false;
	private double time = 0.0;
	
	public LabelWrapperPowerConsumed(int color, int xPos, int yPos) {
		super(color, xPos, yPos, "powerconsumed");
	}
	
	public LabelWrapperPowerConsumed setConstUsage(double usage) {
		constPowerUsage = true;
		constPower = usage;
		return this;
	}
	
	public LabelWrapperPowerConsumed setConstTime(double time) {
		constantTime = true;
		this.time = time;
		return this;
	}
	
	@Override
	public MutableComponent getComponent(AbstractOverdriveRecipeCategory<?> category, Object recipe) {
		if(recipe instanceof AbstractOverdriveRecipe overdrive) {
			return makeComponent(constPowerUsage ? constPower : overdrive.getUsagePerTick(), constantTime ? time : overdrive.getProcessTime());
		} else if (recipe instanceof AbstractCookingRecipe cooking) {
			return makeComponent(constPower, cooking.getCookingTime());
		} else if (recipe instanceof IPseudoRecipe pseudo) {
			return makeComponent(pseudo.getUsagePerTick(), pseudo.getProcessTime());
		}
		return Component.empty();
	}
	
	public MutableComponent makeComponent(double usagePerTick, double processTime) {
		return Component.literal(UtilsText.formatPowerValue(usagePerTick * processTime));
	}

}
