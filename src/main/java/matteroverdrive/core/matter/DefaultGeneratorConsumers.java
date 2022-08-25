package matteroverdrive.core.matter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiPredicate;

import javax.annotation.Nonnull;

import com.mojang.datafixers.util.Pair;

import matteroverdrive.core.config.MatterOverdriveConfig;
import matteroverdrive.core.event.RegisterMatterGeneratorsEvent;
import matteroverdrive.core.matter.generator.base.CraftingMatterValueGenerator;
import matteroverdrive.core.matter.generator.base.SmeltingMatterValueGenerator;
import matteroverdrive.core.matter.generator.base.SmithingMatterValueGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.UpgradeRecipe;

public class DefaultGeneratorConsumers {

	//private static final HashSet<Pair<Item, BiPredicate<Integer, HashMap<Item, Double>>>> SMELTING_CORRECTION_ITEMS = new HashSet<>();
	//private static final HashSet<Pair<Item, BiPredicate<Integer, HashMap<Item, Double>>>> CRAFTING_CORRECTION_ITEMS = new HashSet<>();
	//private static final HashSet<Pair<Item, BiPredicate<Integer, HashMap<Item, Double>>>> SMITHING_CORRECTION_ITEMS = new HashSet<>();
	
	public static CraftingMatterValueGenerator CRAFTING_MATTER_GENERATOR;
	public static SmeltingMatterValueGenerator SMELTING_MATTER_GENERATOR;
	public static SmithingMatterValueGenerator SMITHING_MATTER_GENERATOR;
	
	public static void init() {
		CRAFTING_MATTER_GENERATOR = new CraftingMatterValueGenerator();
		SMELTING_MATTER_GENERATOR = new SmeltingMatterValueGenerator();
		SMITHING_MATTER_GENERATOR = new SmithingMatterValueGenerator();
	}
	
	public static void gatherGenerators(RegisterMatterGeneratorsEvent event) {
		if(MatterOverdriveConfig.USE_DEFAULT_GENERATORS.get()) {
			if(MatterOverdriveConfig.USE_SMELTING_GENERATOR.get()) {
				event.addGenerator(RecipeType.SMELTING, SMELTING_MATTER_GENERATOR);
				/*
				event.addGenerator((generatedValues, recipeManager, loopInteval) -> {
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
					applySmeltingGeneratorCorrections(generatedValues, loopInteval);
				});
				*/
			}
			
			if(MatterOverdriveConfig.USE_CRAFTING_GENERATOR.get()) {
				event.addGenerator(RecipeType.CRAFTING, CRAFTING_MATTER_GENERATOR);
				/*
				event.addGenerator((generatedValues, recipeManager, loopInteval) -> {
					recipeManager.getAllRecipesFor(RecipeType.CRAFTING).forEach(recipe -> {
						ItemStack result = recipe.getResultItem();
						if (MatterRegister.INSTANCE.getServerMatterValue(result) <= 0.0
								&& generatedValues.get(result.getItem()) == null) {
							List<Ingredient> ings = recipe.getIngredients();
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
									if (value > 0.0) {
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
					
					applyCraftingGeneratorCorrections(generatedValues, loopInteval);
				});
				*/
			}
			
			if(MatterOverdriveConfig.USE_SMITHING_GENERATOR.get()) {
				event.addGenerator(RecipeType.SMITHING, SMITHING_MATTER_GENERATOR);
				/*
				event.addGenerator((generatedValues, recipeManager, loopInteval) -> {
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
					
					applySmithingGeneratorCorrections(generatedValues, loopInteval);
				});
				*/
			}
			/*
			if(MatterOverdriveConfig.USE_DEFAULT_GENERATOR_CORRECTIONS.get()) {
				if(MatterOverdriveConfig.USE_DEFAULT_SMELTING_CORRECTIONS.get()) {
					addSmeltingGeneratorCorrection(Items.IRON_NUGGET, (loopInterval, existingValueMap) -> {
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
				if(MatterOverdriveConfig.USE_DEFAULT_CRAFTING_CORRECTIONS.get()) {
					
				}
				if(MatterOverdriveConfig.USE_DEFAULT_SMITHING_CORRECTIONS.get()) {
					
				}
			}
			*/
		}
	}
	
//	/*
//	 * Certain items will have their values set by the smelting category that we don't want to.
//	 * The generator isn't perfect, so we use these methods to remove issues
//	 */
//	private static void applySmeltingGeneratorCorrections(HashMap<Item, Double> map, int loopInterval) {
//		for(Pair<Item, BiPredicate<Integer, HashMap<Item, Double>>> pair : SMELTING_CORRECTION_ITEMS) {
//			if(pair.getSecond().test(loopInterval, map)) {
//				map.remove(pair.getFirst());
//			}
//		}
//	}
//	
//	private static void applyCraftingGeneratorCorrections(HashMap<Item, Double> map, int loopInterval) {
//		for(Pair<Item, BiPredicate<Integer, HashMap<Item, Double>>> pair : CRAFTING_CORRECTION_ITEMS) {
//			if(pair.getSecond().test(loopInterval, map)) {
//				map.remove(pair.getFirst());
//			}
//		}
//	}
//	
//	private static void applySmithingGeneratorCorrections(HashMap<Item, Double> map, int loopInterval) {
//		for(Pair<Item, BiPredicate<Integer, HashMap<Item, Double>>> pair : SMITHING_CORRECTION_ITEMS) {
//			if(pair.getSecond().test(loopInterval, map)) {
//				map.remove(pair.getFirst());
//			}
//		}
//	}
//	
//	/*
//	 * Use this to add items you want removed from the SMELTING category's generator after it runs.
//	 * NOTE, if another existing recipe does not replace it, or if you do not replace it, this
//	 * may have unintended consequences!
//	 */
//	public static void addSmeltingGeneratorCorrection(@Nonnull Item item, @Nonnull BiPredicate<Integer, HashMap<Item, Double>> predicate) {
//		SMELTING_CORRECTION_ITEMS.add(Pair.of(item, predicate));
//	}
//	
//	public static void addSmeltingGeneratorCorrection(@Nonnull Item item) {
//		addSmeltingGeneratorCorrection(item, (loopInterval, existingValueMap) -> true);
//	}
//	
//	/*
//	 * Use this to add items you want removed from the SMELTING category's generator after it runs.
//	 * NOTE, if another existing recipe does not replace it, or if you do not replace it, this
//	 * may have unintended consequences!
//	 */
//	public static void addCraftingGeneratorCorrection(@Nonnull Item item, @Nonnull BiPredicate<Integer, HashMap<Item, Double>> predicate) {
//		CRAFTING_CORRECTION_ITEMS.add(Pair.of(item, predicate));
//	}
//	
//	public static void addCraftingGeneratorCorrection(@Nonnull Item item) {
//		addCraftingGeneratorCorrection(item, (loopInterval, existingValueMap) -> true);
//	}
//	
//	/*
//	 * Use this to add items you want removed from the SMELTING category's generator after it runs.
//	 * NOTE, if another existing recipe does not replace it, or if you do not replace it, this
//	 * may have unintended consequences!
//	 */
//	public static void addSmithingGeneratorCorrection(@Nonnull Item item, @Nonnull BiPredicate<Integer, HashMap<Item, Double>> predicate) {
//		SMITHING_CORRECTION_ITEMS.add(Pair.of(item, predicate));
//	}
//	
//	public static void addSmithingGeneratorCorrection(@Nonnull Item item) {
//		addSmithingGeneratorCorrection(item, (loopInterval, existingValueMap) -> true);
//	}

}
