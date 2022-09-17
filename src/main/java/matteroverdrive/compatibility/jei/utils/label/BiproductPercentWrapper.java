package matteroverdrive.compatibility.jei.utils.label;

import java.util.Arrays;
import java.util.List;

import matteroverdrive.common.recipe.AbstractOverdriveRecipe;
import matteroverdrive.compatibility.jei.categories.OverdriveRecipeCategory;
import matteroverdrive.core.recipe.ProbableItem;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class BiproductPercentWrapper extends GenericLabelWrapper {

	private boolean isFluid;

	public BiproductPercentWrapper(int xEndPos, int yPos, boolean isFluid) {
		super(0xFF808080, yPos, xEndPos, "");
		this.isFluid = isFluid;
	}

	@Override
	public MutableComponent getComponent(OverdriveRecipeCategory<?> category, AbstractOverdriveRecipe recipe) {
		if (isFluid) {
			// for future use
		} else if (recipe.hasItemBiproducts()) {
			List<GenericLabelWrapper> labels = Arrays.asList(category.LABELS);
			int biPos = labels.indexOf(this) - category.itemBiLabelFirstIndex;
			ProbableItem item = recipe.getItemBiproducts()[biPos];
			return Component.literal(item.getChance() * 100 + "%");
		}
		return Component.empty();
	}

}
