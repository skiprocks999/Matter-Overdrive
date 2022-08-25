package matteroverdrive.core.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.util.TriConsumer;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.eventbus.api.Event;

/**
 * 
 * Event is posted on the FORGE bus i.e. same bus where you handle AttachCapabilitiesEvent
 * 
 * @author skip999
 *
 */
public class RegisterMatterGeneratorsEvent extends Event {
	
	private List<TriConsumer<HashMap<Item, Double>, RecipeManager, Integer>> generatorConsumers;
	
	public RegisterMatterGeneratorsEvent() {
		generatorConsumers = new ArrayList<>();
	}
	
	public void addGenerator(TriConsumer<HashMap<Item, Double>, RecipeManager, Integer> generator) {
		generatorConsumers.add(generator);
	}
	
	public List<TriConsumer<HashMap<Item, Double>, RecipeManager, Integer>> getGenerators(){
		return Collections.unmodifiableList(generatorConsumers);
	}

}
