package matteroverdrive.core.datagen.utils.recipe;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;

import matteroverdrive.common.recipe.AbstractOverdriveRecipeSerializer;
import matteroverdrive.core.recipe.ProbableFluid;
import matteroverdrive.core.recipe.ProbableItem;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class AbstractOverdriveFinishedRecipe implements FinishedRecipe {

	private RecipeSerializer<?> serializer;
	private ResourceLocation id;
	
	private List<ProbableItem> itemBiproducts = new ArrayList<>();
	private List<ProbableFluid> fluidBiproducts = new ArrayList<>();
	
	private List<ItemStack> itemIngredients = new ArrayList<>();
	private List<Pair<ResourceLocation, Integer>> tagItemIngredients = new ArrayList<>();
	private List<FluidStack> fluidIngredients = new ArrayList<>();
	private List<Pair<ResourceLocation, Integer>> tagFluidIngredients = new ArrayList<>();
	
	private double experience = 0.0;
	
	protected AbstractOverdriveFinishedRecipe(RecipeSerializer<?> serializer, ResourceLocation id) {
		this.serializer = serializer;
		this.id = id;
	}
	
	public AbstractOverdriveFinishedRecipe addItemStackInput(ItemStack stack) {
		itemIngredients.add(stack);
		return this;
	}
	
	public AbstractOverdriveFinishedRecipe addItemTagInput(Pair<ResourceLocation, Integer> tag) {
		tagItemIngredients.add(tag);
		return this;
	}
	
	public AbstractOverdriveFinishedRecipe addFluidStackInput(FluidStack stack) {
		fluidIngredients.add(stack);
		return this;
	}
	
	public AbstractOverdriveFinishedRecipe addFluidTagInput(Pair<ResourceLocation, Integer> tag) {
		tagFluidIngredients.add(tag);
		return this;
	}
	
	public AbstractOverdriveFinishedRecipe addItemBiproduct(ProbableItem biproudct) {
		itemBiproducts.add(biproudct);
		return this;
	}
	
	public AbstractOverdriveFinishedRecipe addFluidBiproduct(ProbableFluid biproduct) {
		fluidBiproducts.add(biproduct);
		return this;
	}
	
	@Override
	public void serializeRecipeData(JsonObject recipeJson) {
		boolean inputsFlag = false;
		
		int itemInputsCount = itemIngredients.size() + tagItemIngredients.size();
		if(itemInputsCount > 0) {
			inputsFlag = true;
			JsonObject itemInputs = new JsonObject();
			itemInputs.addProperty(AbstractOverdriveRecipeSerializer.COUNT, itemInputsCount);
			JsonObject itemJson;
			int index = 0;
			for(ItemStack stack : itemIngredients) {
				itemJson = new JsonObject();
				itemJson.addProperty("item", ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
				itemJson.addProperty(AbstractOverdriveRecipeSerializer.COUNT, stack.getCount());
				itemInputs.add(index + "", itemJson);
				index++;
			}
			for(Pair<ResourceLocation, Integer> itemTags: tagItemIngredients) {
				itemJson = new JsonObject();
				itemJson.addProperty("tag", itemTags.getFirst().toString());
				itemJson.addProperty(AbstractOverdriveRecipeSerializer.COUNT, itemTags.getSecond());
				itemInputs.add(index + "", itemJson);
				index++;
			}
			recipeJson.add(AbstractOverdriveRecipeSerializer.ITEM_INPUTS, itemInputs);
		}
		
		int fluidInputsCount = fluidIngredients.size() + tagFluidIngredients.size();
		if(fluidInputsCount > 0) {
			inputsFlag = true;
			JsonObject fluidInputs = new JsonObject();
			fluidInputs.addProperty(AbstractOverdriveRecipeSerializer.COUNT, fluidInputsCount);
			JsonObject fluidJson;
			int index = 0;
			for(FluidStack stack : fluidIngredients) {
				fluidJson = new JsonObject();
				fluidJson.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(stack.getFluid()).toString());
				fluidJson.addProperty("amount", stack.getAmount());
				fluidInputs.add(index + "", fluidJson);
				index++;
			}
			for(Pair<ResourceLocation, Integer> itemTags: tagFluidIngredients) {
				fluidJson = new JsonObject();
				fluidJson.addProperty("tag", itemTags.getFirst().getNamespace());
				fluidJson.addProperty("amount", itemTags.getSecond());
				fluidInputs.add(index + "", fluidJson);
				index++;
			}
			recipeJson.add(AbstractOverdriveRecipeSerializer.FLUID_INPUTS, fluidInputs);
		}
		
		if(!inputsFlag) {
			throw new RuntimeException("You must specify at least one item or fluid input");
		}
		
		writeOutput(recipeJson);
		
		recipeJson.addProperty(AbstractOverdriveRecipeSerializer.EXPERIENCE, experience);
		
		if(itemBiproducts.size() > 0) {
			JsonObject itemBiproducts = new JsonObject();
			itemBiproducts.addProperty(AbstractOverdriveRecipeSerializer.COUNT, this.itemBiproducts.size());
			JsonObject itemJson;
			ItemStack stack;
			int index = 0;
			for(ProbableItem biproduct : this.itemBiproducts) {
				itemJson = new JsonObject();
				stack = biproduct.getFullStack();
				itemJson.addProperty("item", ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
				itemJson.addProperty(AbstractOverdriveRecipeSerializer.COUNT, stack.getCount());
				itemJson.addProperty("chance", biproduct.getChance());
				itemBiproducts.add(index + "", itemJson);
				index++;
			}
			recipeJson.add(AbstractOverdriveRecipeSerializer.ITEM_BIPRODUCTS, itemBiproducts);
		}
		
		if(fluidBiproducts.size() > 0) {
			JsonObject fluidBiproducts = new JsonObject();
			fluidBiproducts.addProperty(AbstractOverdriveRecipeSerializer.COUNT, this.fluidBiproducts.size());
			JsonObject fluidJson;
			FluidStack stack;
			int index = 0;
			for(ProbableFluid biproduct : this.fluidBiproducts) {
				fluidJson = new JsonObject();
				stack = biproduct.getFullStack();
				fluidJson.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(stack.getFluid()).toString());
				fluidJson.addProperty("amount", stack.getAmount());
				fluidJson.addProperty("chance", biproduct.getChance());
				fluidBiproducts.add(index + "", fluidJson);
				index++;
			}
			recipeJson.add(AbstractOverdriveRecipeSerializer.FLUID_BIPRODUCTS, fluidBiproducts);
		}
		
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getType() {
		return serializer;
	}

	@Override
	@Nullable
	public JsonObject serializeAdvancement() {
		return null;
	}

	@Override
	@Nullable
	public ResourceLocation getAdvancementId() {
		return null;
	}
	
	public abstract void writeOutput(JsonObject recipeJson);
	
	public static enum RecipeCategory {
		ITEM_2_ITEM, ITEM_2_FLUID, FLUID_ITEM_2_ITEM, FLUID_ITEM_2_FLUID, FLUID_2_ITEM, FLUID_2_FLUID;
		
		public String category() {
			return toString().toLowerCase().replaceAll("_", "");
		}
	}

}
