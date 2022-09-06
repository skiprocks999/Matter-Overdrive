package matteroverdrive.core.datagen.utils.recipe;

import com.google.gson.JsonObject;

import matteroverdrive.common.recipe.AbstractOverdriveRecipeSerializer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class FinishedRecipeFluidOutput extends AbstractOverdriveFinishedRecipe {

	private FluidStack output;
	
	private FinishedRecipeFluidOutput(RecipeSerializer<?> serializer, FluidStack stack) {
		super(serializer);
		this.output = stack;
	}

	@Override
	public void writeOutput(JsonObject recipeJson) {
		JsonObject output = new JsonObject();
		output.addProperty("item", ForgeRegistries.FLUIDS.getKey(this.output.getFluid()).toString());
		output.addProperty("amount", this.output.getAmount());
		recipeJson.add(AbstractOverdriveRecipeSerializer.OUTPUT, output);
	}
	
	@Override
	public FinishedRecipeFluidOutput name(RecipeCategory category, String parent, String name) {
		return (FinishedRecipeFluidOutput) super.name(category, parent, name);
	}
	
	public static FinishedRecipeFluidOutput of(RecipeSerializer<?> serializer, FluidStack output) {
		return new FinishedRecipeFluidOutput(serializer, output);
	}

}
