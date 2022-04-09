package matteroverdrive.core.command;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;

import matteroverdrive.References;
import matteroverdrive.core.matter.MatterRegister;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraftforge.registries.ForgeRegistries;

public class CommandGenerateMatterValues {

	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
	
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		
		dispatcher.register(Commands.literal(References.ID).requires(source -> {
			return source.hasPermission(2);
		}).then(Commands.literal("genmatterfile").executes(source -> {
			return generateMatterFile(source.getSource());
		})));
		
	}
	
	private static int generateMatterFile(CommandSourceStack source) {
		
		source.sendSuccess(new TranslatableComponent("command.matteroverdrive.startmattercalc"), true);
		
		RecipeManager manager = source.getRecipeManager();
		Map<Item, Integer> generatedValues = new HashMap<>();
		
		//we make 50 passes to be extra sure
		for(int i = 0; i < 50; i++) {
			manager.getAllRecipesFor(RecipeType.SMELTING).forEach(recipe -> {
				ItemStack result = recipe.getResultItem();
				if(MatterRegister.INSTANCE.getServerMatterValue(result.getItem()) == null) {
					Ingredient ing = recipe.getIngredients().get(0);
					
					for(ItemStack stack : ing.getItems()) {
						Integer value = MatterRegister.INSTANCE.getServerMatterValue(stack.getItem());
						if(value == null) {
							value = generatedValues.get(stack.getItem());
						}
						if(value != null && !generatedValues.containsKey(result.getItem())) {
							int matterValue = (int) Math.ceil(((double) (stack.getCount() * value)) / (double) result.getCount());
							generatedValues.put(result.getItem(), matterValue);
							break;
						} 
					}
				}
			});
			
			manager.getAllRecipesFor(RecipeType.CRAFTING).forEach(recipe -> {
				ItemStack result = recipe.getResultItem();
				if(MatterRegister.INSTANCE.getServerMatterValue(result.getItem()) == null) {
					List<Ingredient> ings = recipe.getIngredients();
					int sum = 0;
					boolean failed = false;
					for(Ingredient ing : ings) {
						for(ItemStack stack : ing.getItems()) {
							Integer value = MatterRegister.INSTANCE.getServerMatterValue(stack.getItem());
							if(value == null) {
								value = generatedValues.get(stack.getItem());
							}
							if(value != null && !generatedValues.containsKey(result.getItem())) {
								sum += value * stack.getCount();
								failed = false;
								break;
							} else {
								failed = true;
							}
						}
					}
					if (!failed) {
						int matterValue = (int) Math.ceil((double) sum / (double) result.getCount());
						generatedValues.put(result.getItem(), matterValue);
					} 
				}
			});
			
			manager.getAllRecipesFor(RecipeType.SMITHING).forEach(recipe -> {
				UpgradeRecipe upgrade = (UpgradeRecipe) recipe;
				ItemStack result = upgrade.getResultItem();
				if(MatterRegister.INSTANCE.getServerMatterValue(result.getItem()) == null) {
					List<Ingredient> ings = new ArrayList<>();
					ings.add(upgrade.base);
					ings.add(upgrade.addition);
					int sum = 0;
					boolean failed = false;
					for(Ingredient ing : ings) {
						for(ItemStack stack : ing.getItems()) {
							Integer value = MatterRegister.INSTANCE.getServerMatterValue(stack.getItem());
							if(value == null) {
								value = generatedValues.get(stack.getItem());
							}
							if(value != null && !generatedValues.containsKey(result.getItem())) {
								sum += value * stack.getCount();
								failed = false;
								break;
							} else {
								failed = true;
							}
						}
					}
					if (!failed) {
						int matterValue = (int) Math.ceil((double) sum / (double) result.getCount());
						generatedValues.put(result.getItem(), matterValue);
					} 
				}
			});
		}
		
		//now we sort them alphabetically 
		List<Pair<String, Integer>> sorted = new ArrayList<>();
		List<String> names = new ArrayList<>();
		generatedValues.keySet().forEach(item -> {
			names.add(item.getRegistryName().toString());
		});
		Collections.sort(names);
		names.forEach(string -> {
			String[] split = string.split(":");
			sorted.add(Pair.of(string, generatedValues.get(ForgeRegistries.ITEMS.getHolder(new ResourceLocation(split[0], split[1])).get().value())));
		});
		
		JsonObject json = new JsonObject();
		
		sorted.forEach(entry -> {
			if(entry.getSecond() > 0) {
				json.addProperty(entry.getFirst(), entry.getSecond());
			}
		});
		
		Path path = Paths.get("Matter Overdrive/generated.json");
		try {
			String s = GSON.toJson((JsonElement)json);
            

		   Files.createDirectories(path.getParent());
		   BufferedWriter bufferedwriter = Files.newBufferedWriter(path);
		
		   try {
		      bufferedwriter.write(s);
		   } catch (Throwable throwable1) {
		      if (bufferedwriter != null) {
		         try {
		            bufferedwriter.close();
		         } catch (Throwable throwable) {
		            throwable1.addSuppressed(throwable);
		         }
		      }
		
		      throw throwable1;
		   }
		
		   if (bufferedwriter != null) {
		      bufferedwriter.close();
		   }
            
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		source.sendSuccess(new TranslatableComponent("command.matteroverdrive.endmattercalc"), true);
		return 1;
	}
	
}
