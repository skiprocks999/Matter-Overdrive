package matteroverdrive.compatibility.jei.categories.base;

import matteroverdrive.common.recipe.AbstractOverdriveRecipe;
import matteroverdrive.compatibility.jei.utils.gui.ScreenObjectWrapper;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.world.item.ItemStack;

public abstract class OverdriveRecipeCategory<T extends AbstractOverdriveRecipe> extends AbstractOverdriveRecipeCategory<T> {

	public OverdriveRecipeCategory(IGuiHelper guiHelper, ItemStack inputMachine, ScreenObjectWrapper wrapper, 
			int animationTime) {

		super(guiHelper, inputMachine, wrapper, animationTime);
	}

}
