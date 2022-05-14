package matteroverdrive.common.recipe.item2item;

import matteroverdrive.common.recipe.item2item.specific_machines.InscriberRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class Item2ItemRecipeTypes {

	public static final RecipeSerializer<InscriberRecipe> INSCRIBER_JSON_SERIALIZER = new Item2ItemRecipeSerializer<>(
			InscriberRecipe.class);

}
