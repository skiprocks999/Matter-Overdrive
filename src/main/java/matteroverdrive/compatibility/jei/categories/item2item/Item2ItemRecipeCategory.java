package matteroverdrive.compatibility.jei.categories.item2item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import matteroverdrive.common.recipe.item2item.Item2ItemRecipe;
import matteroverdrive.compatibility.jei.categories.OverdriveRecipeCategory;
import matteroverdrive.compatibility.jei.utils.gui.ScreenObjectWrapper;
import matteroverdrive.core.recipe.ProbableFluid;
import matteroverdrive.core.utils.UtilsCapability;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public abstract class Item2ItemRecipeCategory<T extends Item2ItemRecipe> extends OverdriveRecipeCategory<T> {

	/*
	 * DOCUMENTATION NOTES:
	 * 
	 * > Output items supercede buckets in position 
	 * > All biproducts will be included with the outputSlots field 
	 * > All fluid bucket output slots will be incled with the outputSlots field
	 */

	protected Item2ItemRecipeCategory(IGuiHelper guiHelper, ResourceLocation loc, ItemStack inputMachine, ScreenObjectWrapper bWrap, int animTime) {
		super(guiHelper, loc, inputMachine, bWrap, animTime);
	}

	@Override
	public List<List<ItemStack>> getItemInputs(Item2ItemRecipe recipe) {
		List<List<ItemStack>> inputs = new ArrayList<>();
		recipe.getCountedIngredients().forEach(h -> inputs.add(h.fetchCountedStacks()));
		return inputs;
	}

	@Override
	public List<ItemStack> getItemOutputs(Item2ItemRecipe recipe) {
		List<ItemStack> outputs = new ArrayList<>();
		outputs.add(recipe.getResultItem());

		if (recipe.hasItemBiproducts()) {
			outputs.addAll(Arrays.asList(recipe.getFullItemBiStacks()));
		}

		if (recipe.hasFluidBiproducts()) {
			for (ProbableFluid fluid : recipe.getFluidBiproducts()) {
				ItemStack canister = new ItemStack(fluid.getFullStack().getFluid().getBucket(), 1);
				UtilsCapability.fillFluidCap(canister, fluid.getFullStack());
				outputs.add(canister);
			}
		}
		return outputs;
	}

}
