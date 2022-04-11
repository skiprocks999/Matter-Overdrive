package matteroverdrive.core.matter;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.UpgradeRecipe;

public class DefaultGeneratorConsumers {
	
	public static void init() {
		
		MatterRegister.addGeneratorConsumer((generatedValues, recipeManager) -> {
			recipeManager.getAllRecipesFor(RecipeType.SMELTING).forEach(recipe -> {
				ItemStack result = recipe.getResultItem();
				if (MatterRegister.INSTANCE.getServerMatterValue(result) == null) {
					Ingredient ing = recipe.getIngredients().get(0);

					for (ItemStack stack : ing.getItems()) {
						Double value = MatterRegister.INSTANCE.getServerMatterValue(stack);
						if (value == null) {
							value = generatedValues.get(stack.getItem());
						}
						if (value != null && !generatedValues.containsKey(result.getItem())) {
							double matterValue = ((double) (stack.getCount() * value)) / (double) result.getCount();
							generatedValues.put(result.getItem(), matterValue);
							break;
						}
					}
				}
			});
		});
		
		MatterRegister.addGeneratorConsumer((generatedValues, recipeManager) -> {
			recipeManager.getAllRecipesFor(RecipeType.CRAFTING).forEach(recipe -> {
				ItemStack result = recipe.getResultItem();
				if (MatterRegister.INSTANCE.getServerMatterValue(result) == null && generatedValues.get(result.getItem()) == null) {
					List<Ingredient> ings = recipe.getIngredients();
					double sum = 0;
					boolean failed = false;
					for (Ingredient ing : ings) {
						if(failed) {
							break;
						}
						for (ItemStack stack : ing.getItems()) {
							Double value = MatterRegister.INSTANCE.getServerMatterValue(stack);
							if (value == null) {
								value = generatedValues.get(stack.getItem());
							}
							if (value != null) {
								sum += value * stack.getCount();
								failed = false;
								break;
							}
							failed = true;
						}
					}
					if (!failed) {
						double matterValue = (double) sum / (double) result.getCount();
						generatedValues.put(result.getItem(), matterValue);
					}
				}
			});
		});
		
		MatterRegister.addGeneratorConsumer((generatedValues, recipeManager) -> {
			recipeManager.getAllRecipesFor(RecipeType.SMITHING).forEach(recipe -> {
				UpgradeRecipe upgrade = recipe;
				ItemStack result = upgrade.getResultItem();
				if (MatterRegister.INSTANCE.getServerMatterValue(result) == null) {
					List<Ingredient> ings = new ArrayList<>();
					ings.add(upgrade.base);
					ings.add(upgrade.addition);
					double sum = 0;
					boolean failed = false;
					for (Ingredient ing : ings) {
						if(failed) {
							break;
						}
						for (ItemStack stack : ing.getItems()) {
							Double value = MatterRegister.INSTANCE.getServerMatterValue(stack);
							if (value == null) {
								value = generatedValues.get(stack.getItem());
							}
							if (value != null && !generatedValues.containsKey(result.getItem())) {
								sum += value * stack.getCount();
								failed = false;
								break;
							}
							failed = true;
						}
					}
					if (!failed) {
						double matterValue = (double) sum / (double) result.getCount();
						generatedValues.put(result.getItem(), matterValue);
					}
				}
			});
		});
		
	}

}
