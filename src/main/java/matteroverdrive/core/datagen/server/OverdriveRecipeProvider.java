package matteroverdrive.core.datagen.server;

import java.util.function.Consumer;

import matteroverdrive.References;
import matteroverdrive.common.item.type.TypeIsolinearCircuit;
import matteroverdrive.common.recipe.RecipeInit;
import matteroverdrive.common.tags.OverdriveTags;
import matteroverdrive.core.datagen.utils.recipe.AbstractOverdriveFinishedRecipe.RecipeCategory;
import matteroverdrive.core.datagen.utils.recipe.FinishedRecipeItemOutput;
import matteroverdrive.core.datagen.utils.recipe.OverdriveShapedCraftingRecipe;
import matteroverdrive.registry.ItemRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.ItemStack;

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
		
		OverdriveShapedCraftingRecipe.start(ItemRegistry.ITEM_ISOLINEAR_CIRCUITS.get(TypeIsolinearCircuit.TIER1).get(), 1)
			.addPattern("I").addPattern("R").addPattern("G").addKey('I', "forge", "ingots/iron").addKey('R', "forge", "dusts/redstone")
			.addKey('G', "forge", "glass").complete(References.ID, "isolinear_circuit_tier1", consumer);
		
		
	}

	private void addInscriberRecipes(Consumer<FinishedRecipe> consumer) {
		
		inscriberRecipe(new ItemStack(ItemRegistry.ITEM_ISOLINEAR_CIRCUITS.get(TypeIsolinearCircuit.TIER2).get()), "isolinear_circuit_tier2")
			.addItemTagInput(OverdriveTags.Items.CIRCUITS_BASIC, 1).addItemTagInput(OverdriveTags.Items.GOLD_INGOT, 1).complete(consumer);
		inscriberRecipe(new ItemStack(ItemRegistry.ITEM_ISOLINEAR_CIRCUITS.get(TypeIsolinearCircuit.TIER3).get()), "isolinear_circuit_tier3")
			.addItemTagInput(OverdriveTags.Items.CIRCUITS_ADVANCED, 1).addItemTagInput(OverdriveTags.Items.DIAMOND_GEM, 1).complete(consumer);
		inscriberRecipe(new ItemStack(ItemRegistry.ITEM_ISOLINEAR_CIRCUITS.get(TypeIsolinearCircuit.TIER4).get()), "isolinear_circuit_tier4")
			.addItemTagInput(OverdriveTags.Items.CIRCUITS_ELITE, 1).addItemTagInput(OverdriveTags.Items.EMERALD_GEM, 1).complete(consumer);
	
	}
	
	private FinishedRecipeItemOutput inscriberRecipe(ItemStack output, String name) {
		return FinishedRecipeItemOutput.of(RecipeInit.INSCRIBER_SERIALIZER.get(), output)
				.name(RecipeCategory.ITEM_2_ITEM, References.ID, INSCRIBER_LOC + "/" + name);
	}

}
