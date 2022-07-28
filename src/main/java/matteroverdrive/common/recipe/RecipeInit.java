package matteroverdrive.common.recipe;

import matteroverdrive.References;
import matteroverdrive.common.recipe.item2item.Item2ItemRecipeTypes;
import matteroverdrive.common.recipe.item2item.specific_machines.InscriberRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RecipeInit {

	// Deferred Register
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER = DeferredRegister
			.create(ForgeRegistries.RECIPE_SERIALIZERS, References.ID);
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister
			.create(ForgeRegistries.RECIPE_TYPES, References.ID);
	/* RECIPE TYPES */

	// Item2Item
	public static final RegistryObject<RecipeType<InscriberRecipe>> INSCRIBER_TYPE = RECIPE_TYPES
			.register(InscriberRecipe.RECIPE_GROUP, CustomRecipeType::new);

	/* SERIALIZERS */

	// Item2Item
	public static final RegistryObject<RecipeSerializer<?>> INSCRIBER_SERIALIZER = RECIPE_SERIALIZER
			.register(InscriberRecipe.RECIPE_GROUP, () -> Item2ItemRecipeTypes.INSCRIBER_JSON_SERIALIZER);

	/* Functional Methods */

	public static class CustomRecipeType<T extends Recipe<?>> implements RecipeType<T> {
		@Override
		public String toString() {
			return ForgeRegistries.RECIPE_TYPES.getKey(this).toString();
		}
	}

}
