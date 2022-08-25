package matteroverdrive.core.event;

import java.util.HashMap;

import com.google.common.collect.ImmutableMap;

import matteroverdrive.core.matter.generator.AbstractMatterValueGenerator;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.Event;

/**
 * 
 * Event is posted on the FORGE bus i.e. same bus where you handle AttachCapabilitiesEvent
 * 
 * @author skip999
 *
 */
public class RegisterMatterGeneratorsEvent extends Event {
	
	private HashMap<RecipeType<?>, AbstractMatterValueGenerator> matterGeneratorConsumers;
	
	public RegisterMatterGeneratorsEvent() {
		matterGeneratorConsumers = new HashMap<>();
	}
	
	public void addGenerator(RecipeType<?> recipeType, AbstractMatterValueGenerator generator) {
		matterGeneratorConsumers.put(recipeType, generator);
	}
	
	public ImmutableMap<RecipeType<?>, AbstractMatterValueGenerator> getGenerators(){
		return ImmutableMap.copyOf(matterGeneratorConsumers);
	}

}
