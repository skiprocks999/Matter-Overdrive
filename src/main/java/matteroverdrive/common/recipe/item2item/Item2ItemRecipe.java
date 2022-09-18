package matteroverdrive.common.recipe.item2item;

import java.util.ArrayList;
import java.util.List;

import com.mojang.datafixers.util.Pair;

import matteroverdrive.common.recipe.AbstractOverdriveRecipe;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.recipe.CountableIngredient;
import matteroverdrive.core.recipe.ProbableFluid;
import matteroverdrive.core.recipe.ProbableItem;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public abstract class Item2ItemRecipe extends AbstractOverdriveRecipe {

	private CountableIngredient[] ITEM_INPUTS;
	private ItemStack OUTPUT;

	public Item2ItemRecipe(ResourceLocation recipeID, CountableIngredient[] inputs, ItemStack output, double experience,
			double processTime, double usage) {
		super(recipeID, experience, processTime, usage);
		ITEM_INPUTS = inputs;
		OUTPUT = output;
	}

	public Item2ItemRecipe(ResourceLocation recipeID, CountableIngredient[] inputs, ItemStack output,
			ProbableItem[] itemBiproducts, double experience, double processTime, double usage) {
		super(recipeID, itemBiproducts, experience, processTime, usage);
		ITEM_INPUTS = inputs;
		OUTPUT = output;
	}

	public Item2ItemRecipe(CountableIngredient[] inputs, ItemStack output, ProbableFluid[] fluidBiproducts,
			ResourceLocation recipeID, double experience, double processTime, double usage) {
		super(fluidBiproducts, recipeID, experience, processTime, usage);
		ITEM_INPUTS = inputs;
		OUTPUT = output;
	}

	public Item2ItemRecipe(ResourceLocation recipeID, CountableIngredient[] inputs, ItemStack output,
			ProbableItem[] itemBiproducts, ProbableFluid[] fluidBiproducts, double experience, double processTime,
			double usage) {
		super(recipeID, itemBiproducts, fluidBiproducts, experience, processTime, usage);
		ITEM_INPUTS = inputs;
		OUTPUT = output;
	}

	@Override
	public boolean matchesRecipe(CapabilityInventory inv, int procNum) {
		Pair<List<Integer>, Boolean> pair = areItemsValid(getCountedIngredients(), inv.getInputs());
		if (pair.getSecond()) {
			setItemArrangement(procNum, pair.getFirst());
			return true;
		}
		return false;
	}

	@Override
	public ItemStack assemble(RecipeWrapper inv) {
		return getResultItem();
	}

	@Override
	public ItemStack getResultItem() {
		return OUTPUT;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> list = NonNullList.create();
		for (CountableIngredient ing : ITEM_INPUTS) {
			list.add(ing);
		}
		return list;
	}

	public List<CountableIngredient> getCountedIngredients() {
		List<CountableIngredient> list = new ArrayList<>();
		for (CountableIngredient ing : ITEM_INPUTS) {
			list.add(ing);
		}
		return list;
	}

}
