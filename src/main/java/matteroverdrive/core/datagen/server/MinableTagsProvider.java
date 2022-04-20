package matteroverdrive.core.datagen.server;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.References;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

public class MinableTagsProvider extends BlockTagsProvider {

	public MinableTagsProvider(DataGenerator pGenerator, ExistingFileHelper existingFileHelper) {
		super(pGenerator, References.ID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(DeferredRegisters.REGULAR_TRITANIUM_PLATING.get())
				.add(DeferredRegisters.COLORED_TRITANIUM_PLATING.<Block>getObjectsAsArray(new Block[0]))
				.add(DeferredRegisters.FLOOR_TILE.<Block>getObjectsAsArray(new Block[0]))
				.add(DeferredRegisters.FLOOR_TILES.<Block>getObjectsAsArray(new Block[0]))
				.add(DeferredRegisters.TRITANIUM_CRATES.<Block>getObjectsAsArray(new Block[0]))
				.add(DeferredRegisters.BLOCK_SOLAR_PANEL.get()).add(DeferredRegisters.BLOCK_MATTER_DECOMPOSER.get());

		tag(BlockTags.NEEDS_STONE_TOOL).add(DeferredRegisters.REGULAR_TRITANIUM_PLATING.get())
				.add(DeferredRegisters.COLORED_TRITANIUM_PLATING.<Block>getObjectsAsArray(new Block[0]))
				.add(DeferredRegisters.FLOOR_TILE.<Block>getObjectsAsArray(new Block[0]))
				.add(DeferredRegisters.FLOOR_TILES.<Block>getObjectsAsArray(new Block[0]))
				.add(DeferredRegisters.TRITANIUM_CRATES.<Block>getObjectsAsArray(new Block[0]))
				.add(DeferredRegisters.BLOCK_SOLAR_PANEL.get()).add(DeferredRegisters.BLOCK_MATTER_DECOMPOSER.get());
	}

}
