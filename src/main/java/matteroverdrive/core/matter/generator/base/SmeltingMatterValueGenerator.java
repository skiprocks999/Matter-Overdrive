package matteroverdrive.core.matter.generator.base;

import java.util.HashMap;

import matteroverdrive.core.config.MatterOverdriveConfig;
import matteroverdrive.core.matter.MatterRegister;
import matteroverdrive.core.matter.generator.AbstractMatterValueGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

public class SmeltingMatterValueGenerator extends AbstractMatterValueGenerator {

	public SmeltingMatterValueGenerator() {
		super();
		if(MatterOverdriveConfig.USE_DEFAULT_GENERATOR_CORRECTIONS.get() && MatterOverdriveConfig.USE_DEFAULT_SMELTING_CORRECTIONS.get()) {
			addGeneratorCorrection(Items.IRON_NUGGET, (loopInterval, existingValueMap) -> {
				double ironIngotValue = existingValueMap.getOrDefault(Items.IRON_INGOT, 0.0);
				if(ironIngotValue > 0) {
					double ironNuggetValue = existingValueMap.getOrDefault(Items.IRON_NUGGET, 0.0);
					if(existingValueMap.containsKey(Items.IRON_NUGGET) && ironNuggetValue != (ironIngotValue / 9.0D) ) {
						return true;
					}
				}
				return false;
			});
		}
	}
	
	@Override
	public void run(HashMap<Item, Double> generatedValues, RecipeManager recipeManager, int loopIteration) {
		recipeManager.getAllRecipesFor(RecipeType.SMELTING).forEach(recipe -> {
			ItemStack result = recipe.getResultItem();
			if (MatterRegister.INSTANCE.getServerMatterValue(result) <= 0.0) {
				Ingredient ing = recipe.getIngredients().get(0);

				for (ItemStack stack : ing.getItems()) {
					double value = MatterRegister.INSTANCE.getServerMatterValue(stack);
					if (value <= 0.0) {
						value = generatedValues.getOrDefault(stack.getItem(), 0.0);
					}
					if (value > 0.0 && !generatedValues.containsKey(result.getItem())) {
						double matterValue = ((double) (stack.getCount() * value)) / (double) result.getCount();
						generatedValues.put(result.getItem(), matterValue);
						break;
					}
				}
			}
		});
	}

}
