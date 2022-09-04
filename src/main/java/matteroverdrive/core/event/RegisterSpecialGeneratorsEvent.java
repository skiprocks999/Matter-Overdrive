package matteroverdrive.core.event;

import java.util.HashMap;

import com.google.common.collect.ImmutableMap;

import matteroverdrive.core.matter.generator.AbstractMatterValueGenerator;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.Event;

public class RegisterSpecialGeneratorsEvent extends Event {
	
	private HashMap<RecipeType<?>, AbstractMatterValueGenerator> matterGeneratorConsumers;
	
	public RegisterSpecialGeneratorsEvent() {
		matterGeneratorConsumers = new HashMap<>();
	}
	
	public void addGenerator(RecipeType<?> recipeType, AbstractMatterValueGenerator generator) {
		matterGeneratorConsumers.put(recipeType, generator);
	}
	
	/**
	 * 
	 * @param recipeType
	 * @return True if the RecipeType was present, False if not present
	 */
	public boolean removeGenerator(RecipeType<?> recipeType) {
		return matterGeneratorConsumers.remove(recipeType) != null;
	}
	
	public ImmutableMap<RecipeType<?>, AbstractMatterValueGenerator> getGenerators(){
		return ImmutableMap.copyOf(matterGeneratorConsumers);
	} 

}
