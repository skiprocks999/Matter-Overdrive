package matteroverdrive.compatibility.jei.utils;

import matteroverdrive.common.recipe.AbstractOverdriveRecipe;
import matteroverdrive.compatibility.jei.categories.OverdriveRecipeCategory;
import net.minecraft.world.item.crafting.RecipeType;

public record OverdriveRecipeDataProvider(OverdriveRecipeCategory<? extends AbstractOverdriveRecipe> category, RecipeType<? extends AbstractOverdriveRecipe> registeredType) {

}
