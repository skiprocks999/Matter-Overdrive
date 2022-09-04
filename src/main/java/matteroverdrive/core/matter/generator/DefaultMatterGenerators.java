package matteroverdrive.core.matter.generator;

import matteroverdrive.core.config.MatterOverdriveConfig;
import matteroverdrive.core.event.RegisterMatterGeneratorsEvent;
import matteroverdrive.core.matter.generator.base.CraftingMatterValueGenerator;
import matteroverdrive.core.matter.generator.base.SmeltingMatterValueGenerator;
import matteroverdrive.core.matter.generator.base.SmithingMatterValueGenerator;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * 
 * The default generators are only initialized just before the event is posted
 * 
 * @author skip999
 *
 */
public class DefaultMatterGenerators {

	public static CraftingMatterValueGenerator CRAFTING_MATTER_GENERATOR;
	public static SmeltingMatterValueGenerator SMELTING_MATTER_GENERATOR;
	public static SmithingMatterValueGenerator SMITHING_MATTER_GENERATOR;
	
	public static void init() {
		CRAFTING_MATTER_GENERATOR = new CraftingMatterValueGenerator();
		SMELTING_MATTER_GENERATOR = new SmeltingMatterValueGenerator();
		SMITHING_MATTER_GENERATOR = new SmithingMatterValueGenerator();
	}
	
	public static void gatherGenerators(RegisterMatterGeneratorsEvent event) {
		if(MatterOverdriveConfig.USE_DEFAULT_GENERATORS.get()) {
			if(MatterOverdriveConfig.USE_SMELTING_GENERATOR.get()) {
				event.addGenerator(RecipeType.SMELTING, SMELTING_MATTER_GENERATOR);
			}
			
			if(MatterOverdriveConfig.USE_CRAFTING_GENERATOR.get()) {
				event.addGenerator(RecipeType.CRAFTING, CRAFTING_MATTER_GENERATOR);
			}
			
			if(MatterOverdriveConfig.USE_SMITHING_GENERATOR.get()) {
				event.addGenerator(RecipeType.SMITHING, SMITHING_MATTER_GENERATOR);
			}
		}
	}

}
