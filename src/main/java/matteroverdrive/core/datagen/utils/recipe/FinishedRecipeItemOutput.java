package matteroverdrive.core.datagen.utils.recipe;

import com.google.gson.JsonObject;

import matteroverdrive.References;
import matteroverdrive.common.recipe.AbstractOverdriveRecipeSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;

public class FinishedRecipeItemOutput extends AbstractOverdriveFinishedRecipe {

	private ItemStack output;
	
	private FinishedRecipeItemOutput(RecipeSerializer<?> serializer, ResourceLocation id, ItemStack stack) {
		super(serializer, id);
		this.output = stack;
	}

	@Override
	public void writeOutput(JsonObject recipeJson) {
		JsonObject output = new JsonObject();
		output.addProperty("item", ForgeRegistries.ITEMS.getKey(this.output.getItem()).toString());
		output.addProperty(AbstractOverdriveRecipeSerializer.COUNT, this.output.getCount());
		recipeJson.add(AbstractOverdriveRecipeSerializer.OUTPUT, output);
	}
	
	public static FinishedRecipeItemOutput of(RecipeSerializer<?> serializer, ItemStack output, RecipeCategory category, String machine, String name) {
		ResourceLocation loc = new ResourceLocation(References.ID, category.category() + "/" + machine + "/" + name);
		return new FinishedRecipeItemOutput(serializer, loc, output);
	}

}
