package matteroverdrive.core.matter.generator.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import matteroverdrive.core.config.MatterOverdriveConfig;
import matteroverdrive.core.matter.MatterRegister;
import matteroverdrive.core.matter.generator.AbstractMatterValueGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.UpgradeRecipe;

public class SmithingMatterValueGenerator extends AbstractMatterValueGenerator {

	public SmithingMatterValueGenerator() {
		super();
		if(MatterOverdriveConfig.USE_DEFAULT_GENERATOR_CORRECTIONS.get() && MatterOverdriveConfig.USE_DEFAULT_SMITHING_CORRECTIONS.get()) {
			
		}
	}
	
	@Override
	public void run(HashMap<Item, Double> generatedValues, RecipeManager recipeManager, int loopIteration) {
		recipeManager.getAllRecipesFor(RecipeType.SMITHING).forEach(recipe -> {
			UpgradeRecipe upgrade = recipe;
			ItemStack result = upgrade.getResultItem();
			if (MatterRegister.INSTANCE.getServerMatterValue(result) <= 0.0) {
				List<Ingredient> ings = new ArrayList<>();
				// AT
				ings.add(upgrade.base);
				ings.add(upgrade.addition);
				double sum = 0;
				boolean failed = false;
				for (Ingredient ing : ings) {
					if (failed) {
						break;
					}
					for (ItemStack stack : ing.getItems()) {
						double value = MatterRegister.INSTANCE.getServerMatterValue(stack);
						if (value <= 0.0) {
							value = generatedValues.getOrDefault(stack.getItem(), 0.0);
						}
						if (value > 0.0 && !generatedValues.containsKey(result.getItem())) {
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
	}

}
