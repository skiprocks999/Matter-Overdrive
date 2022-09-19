package matteroverdrive.compatibility.jei.categories.vanillacooking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import matteroverdrive.compatibility.jei.categories.base.AbstractOverdriveRecipeCategory;
import matteroverdrive.compatibility.jei.utils.gui.ScreenObjectWrapper;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;

public abstract class CookingRecipeCategory<T extends AbstractCookingRecipe> extends AbstractOverdriveRecipeCategory<T> {

	public CookingRecipeCategory(IGuiHelper guiHelper, ItemStack inputMachine, ScreenObjectWrapper wrapper, int animationTime) {

		super(guiHelper, inputMachine, wrapper, animationTime);
	}
	
	@Override
	public List<List<ItemStack>> getItemInputs(AbstractCookingRecipe recipe) {
		List<List<ItemStack>> inputs = new ArrayList<>();
		recipe.getIngredients().forEach(h -> inputs.add(Arrays.asList(h.getItems())));
		return inputs;
	}

	@Override
	public List<ItemStack> getItemOutputs(AbstractCookingRecipe recipe) {
		List<ItemStack> outputs = new ArrayList<>();
		outputs.add(recipe.getResultItem());

		return outputs;
	}
	
}
