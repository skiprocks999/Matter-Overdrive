package matteroverdrive.common.recipe.item2item.specific_machines;

import matteroverdrive.References;
import matteroverdrive.common.recipe.RecipeInit;
import matteroverdrive.common.recipe.item2item.Item2ItemRecipe;
import matteroverdrive.core.recipe.CountableIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class InscriberRecipe extends Item2ItemRecipe {

	public static final String RECIPE_GROUP = "inscriber_recipe";
	public static final String MOD_ID = References.ID;
	public static final ResourceLocation RECIPE_ID = new ResourceLocation(MOD_ID, RECIPE_GROUP);

	public InscriberRecipe(ResourceLocation recipeID, CountableIngredient[] inputs, ItemStack output,
			double experience, double processTime, double usage) {
		super(recipeID, inputs, output, experience, processTime, usage);

	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeInit.INSCRIBER_SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return RecipeInit.INSCRIBER_TYPE.get();
	}

}
