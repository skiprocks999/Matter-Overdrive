package matteroverdrive.compatibility.jei.utils.label.types;

import java.util.Arrays;
import java.util.List;

import matteroverdrive.common.recipe.AbstractOverdriveRecipe;
import matteroverdrive.compatibility.jei.categories.base.AbstractOverdriveRecipeCategory;
import matteroverdrive.compatibility.jei.utils.label.LabelWrapperGeneric;
import matteroverdrive.core.recipe.ProbableItem;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class BiproductPercentWrapper extends LabelWrapperGeneric {

	private boolean isFluid;

	public BiproductPercentWrapper(int xEndPos, int yPos, boolean isFluid) {
		super(0xFF808080, yPos, xEndPos, "");
		this.isFluid = isFluid;
	}

	@Override
	public MutableComponent getComponent(AbstractOverdriveRecipeCategory<?> category, Object recipe) {
		if(recipe instanceof AbstractOverdriveRecipe overdrive) {
			if (isFluid) {
				// for future use
			} else if (overdrive.hasItemBiproducts()) {
				List<LabelWrapperGeneric> labels = Arrays.asList(category.LABELS);
				int biPos = labels.indexOf(this) - category.itemBiLabelFirstIndex;
				ProbableItem item = overdrive.getItemBiproducts()[biPos];
				return Component.literal(item.getChance() * 100 + "%");
			}
		}
		
		return Component.empty();
	}

}
