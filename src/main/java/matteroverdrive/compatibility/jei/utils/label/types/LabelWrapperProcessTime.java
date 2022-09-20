package matteroverdrive.compatibility.jei.utils.label.types;

import matteroverdrive.common.recipe.AbstractOverdriveRecipe;
import matteroverdrive.compatibility.jei.categories.base.AbstractOverdriveRecipeCategory;
import matteroverdrive.compatibility.jei.utils.IPseudoRecipe;
import matteroverdrive.compatibility.jei.utils.label.LabelWrapperGeneric;
import matteroverdrive.core.utils.UtilsText;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;

public class LabelWrapperProcessTime extends LabelWrapperGeneric {

	private boolean constantTime = false;
	private double time = 0.0;
	
	public LabelWrapperProcessTime(int color, int xPos, int yPos) {
		super(color, xPos, yPos, "processtime");
	}
	
	public LabelWrapperProcessTime setConstTime(double time) {
		constantTime = true;
		this.time = time;
		return this;
	}
	
	@Override
	public MutableComponent getComponent(AbstractOverdriveRecipeCategory<?> category, Object recipe) {
		if(recipe instanceof AbstractOverdriveRecipe overdrive) {
			return makeComponent(constantTime ? time : overdrive.getProcessTime());
		} else if (recipe instanceof AbstractCookingRecipe cooking) {
			return makeComponent(cooking.getCookingTime());
		} else if (recipe instanceof IPseudoRecipe pseudo) {
			return makeComponent(pseudo.getProcessTime());
		}
		return Component.empty();
	}
	
	public MutableComponent makeComponent(double time) {
		return Component.literal(UtilsText.formatTimeValue(time / 20.0D));
	}

}
