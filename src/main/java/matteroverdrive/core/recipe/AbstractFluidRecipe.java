package matteroverdrive.core.recipe;

import java.util.List;

import javax.annotation.Nullable;

import matteroverdrive.common.recipe.AbstractOverdriveRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public abstract class AbstractFluidRecipe extends AbstractOverdriveRecipe {

	protected AbstractFluidRecipe(ResourceLocation recipeID, double experience, double processTime, double usage) {
		super(recipeID, experience, processTime, usage);
	}

	protected AbstractFluidRecipe(ResourceLocation recipeID, ProbableItem[] itemBiproducts, double experience,
			double processTime, double usage) {
		super(recipeID, itemBiproducts, experience, processTime, usage);
	}

	protected AbstractFluidRecipe(ProbableFluid[] fluidBiproducts, ResourceLocation recipeID, double experience,
			double processTime, double usage) {
		super(fluidBiproducts, recipeID, experience, processTime, usage);
	}

	protected AbstractFluidRecipe(ResourceLocation recipeID, ProbableItem[] itemBiproducts,
			ProbableFluid[] fluidBiproducts, double experience, double processTime, double usage) {
		super(recipeID, itemBiproducts, fluidBiproducts, experience, processTime, usage);
	}

	@Override
	public ItemStack assemble(RecipeWrapper inv) {
		return new ItemStack(Items.DIRT, 1);
	}

	@Override
	public ItemStack getResultItem() {
		return new ItemStack(Items.DIRT, 1);
	}

	@Nullable
	public FluidStack getFluidRecipeOutput() {
		return FluidStack.EMPTY;
	}

	public abstract List<FluidIngredient> getFluidIngredients();

}
