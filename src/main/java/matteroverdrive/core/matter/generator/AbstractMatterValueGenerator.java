package matteroverdrive.core.matter.generator;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.BiPredicate;

import javax.annotation.Nonnull;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeManager;

public abstract class AbstractMatterValueGenerator {

	private final HashMap<Item, BiPredicate<Integer, HashMap<Item, Double>>> corrections;
	
	public AbstractMatterValueGenerator() {
		corrections = new HashMap<>();
	}
	
	public final void handleGeneration(HashMap<Item, Double> generatedValues, RecipeManager recipeManager, int loopIteration) {
		run(generatedValues, recipeManager, loopIteration);
		applyGeneratorCorrections(generatedValues, loopIteration);
	}
	
	protected abstract void run(HashMap<Item, Double> generatedValues, RecipeManager recipeManager, int loopIteration);
	
	public void applyGeneratorCorrections(HashMap<Item, Double> map, int loopInterval) {
		for(Entry<Item, BiPredicate<Integer, HashMap<Item, Double>>> entry : corrections.entrySet()) {
			if(entry.getValue().test(loopInterval, map)) {
				map.remove(entry.getKey());
			}
		}
	}
	
	public void addGeneratorCorrection(@Nonnull Item item, @Nonnull BiPredicate<Integer, HashMap<Item, Double>> predicate) {
		corrections.put(item, predicate);
	}
	
	public void addGeneratorCorrection(@Nonnull Item item) {
		addGeneratorCorrection(item, (loopInterval, existingValueMap) -> true);
	}
	
	public void removeGeneratorCorrection(@Nonnull Item item) {
		corrections.remove(item);
	}
	
}
