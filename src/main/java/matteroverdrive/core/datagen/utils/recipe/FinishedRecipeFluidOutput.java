package matteroverdrive.core.datagen.utils.recipe;

import com.google.gson.JsonObject;

import matteroverdrive.References;
import matteroverdrive.common.recipe.AbstractOverdriveRecipeSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class FinishedRecipeFluidOutput extends AbstractOverdriveFinishedRecipe {

	private FluidStack output;
	
	private FinishedRecipeFluidOutput(RecipeSerializer<?> serializer, ResourceLocation id, FluidStack stack) {
		super(serializer, id);
		this.output = stack;
	}

	@Override
	public void writeOutput(JsonObject recipeJson) {
		JsonObject output = new JsonObject();
		output.addProperty("item", ForgeRegistries.FLUIDS.getKey(this.output.getFluid()).toString());
		output.addProperty("amount", this.output.getAmount());
		recipeJson.add(AbstractOverdriveRecipeSerializer.OUTPUT, output);
	}
	
	public static FinishedRecipeFluidOutput of(RecipeSerializer<?> serializer, FluidStack output, RecipeCategory category, String machine, String name) {
		ResourceLocation loc = new ResourceLocation(References.ID, category.category() + "/" + machine + "/" + name);
		return new FinishedRecipeFluidOutput(serializer, loc, output);
	}

}
