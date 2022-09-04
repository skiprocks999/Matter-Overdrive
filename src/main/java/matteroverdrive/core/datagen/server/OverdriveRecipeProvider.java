package matteroverdrive.core.datagen.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;

import matteroverdrive.References;
import matteroverdrive.common.item.type.TypeIsolinearCircuit;
import matteroverdrive.common.recipe.RecipeInit;
import matteroverdrive.core.datagen.utils.recipe.AbstractOverdriveFinishedRecipe.RecipeCategory;
import matteroverdrive.core.datagen.utils.recipe.FinishedRecipeItemOutput;
import matteroverdrive.core.datagen.utils.recipe.VanillaFinishedRecipes;
import matteroverdrive.registry.ItemRegistry;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class OverdriveRecipeProvider extends RecipeProvider {
	
	private static final String INSCRIBER_LOC = "inscriber";

	public OverdriveRecipeProvider(DataGenerator gen) {
		super(gen);
	}
	
	@Override
	protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
		addShapedCraftingRecipes(consumer);
		addInscriberRecipes(consumer);
	}
	
	private void addShapedCraftingRecipes(Consumer<FinishedRecipe> consumer) {
		
		consumer.accept(
				VanillaFinishedRecipes.shaped(new ResourceLocation(References.ID, "isolinear_circuit_tier1"), 
						ItemRegistry.ITEM_ISOLINEAR_CIRCUITS.get(TypeIsolinearCircuit.TIER1).get(), 
						1, 
						makeShapedPattern("I", "R", "G"),
						makeIngredientMap(
								Pair.of('I', Ingredient.of(itemTag(forgeTag("ingots/iron")))),
								Pair.of('R', Ingredient.of(itemTag(forgeTag("dusts/redstone")))),
								Pair.of('G', Ingredient.of(itemTag(forgeTag("glass"))))
								)));
		
	}

	private void addInscriberRecipes(Consumer<FinishedRecipe> consumer) {
		consumer.accept(
				inscriberRecipe(new ItemStack(ItemRegistry.ITEM_ISOLINEAR_CIRCUITS.get(TypeIsolinearCircuit.TIER2).get()), "isolinear_circuit_tier2")
				.addItemTagInput(Pair.of(forgeTag("circuits/basic"), 1))
				.addItemTagInput(Pair.of(forgeTag("ingots/gold"), 1))
		);
		consumer.accept(
				inscriberRecipe(new ItemStack(ItemRegistry.ITEM_ISOLINEAR_CIRCUITS.get(TypeIsolinearCircuit.TIER3).get()), "isolinear_circuit_tier3")
				.addItemTagInput(Pair.of(forgeTag("circuits/advanecd"), 1))
				.addItemTagInput(Pair.of(forgeTag("gems/diamond"), 1))
		);
		consumer.accept(
				inscriberRecipe(new ItemStack(ItemRegistry.ITEM_ISOLINEAR_CIRCUITS.get(TypeIsolinearCircuit.TIER4).get()), "isolinear_circuit_tier4")
				.addItemTagInput(Pair.of(forgeTag("circuits/elite"), 1))
				.addItemTagInput(Pair.of(forgeTag("gems/emerald"), 1))
		);
	}
	
	private static FinishedRecipeItemOutput inscriberRecipe(ItemStack output, String name) {
		return FinishedRecipeItemOutput.of(RecipeInit.INSCRIBER_SERIALIZER.get(), output, RecipeCategory.ITEM_2_ITEM, INSCRIBER_LOC, name);
	}
	
	private static ResourceLocation forgeTag(String tag) {
		return new ResourceLocation("forge", tag);
	}
	
	private static TagKey<Item> itemTag(ResourceLocation tag) {
		return TagKey.create(Registry.ITEM_REGISTRY, tag);
	}
	
	@SafeVarargs
	private static Map<Character, Ingredient> makeIngredientMap(Pair<Character, Ingredient>... ingredients){
		Map<Character, Ingredient> map = Maps.newLinkedHashMap();
		for(Pair<Character, Ingredient> pair : ingredients) {
			map.put(pair.getFirst(), pair.getSecond());
		}
		return map;
	}
	
	private static List<String> makeShapedPattern(String...strings){
		List<String> list = new ArrayList<>();	
		for(String string : strings) {
			list.add(string);
		}
		return list;
	}

}
