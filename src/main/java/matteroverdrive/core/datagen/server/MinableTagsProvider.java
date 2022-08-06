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
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(DeferredRegisters.BLOCK_REGULAR_TRITANIUM_PLATING.get())
				.add(DeferredRegisters.BLOCK_COLORED_TRITANIUM_PLATING.<Block>getObjectsAsArray(new Block[0]))
				.add(DeferredRegisters.BLOCK_FLOOR_TILE.<Block>getObjectsAsArray(new Block[0]))
				.add(DeferredRegisters.BLOCK_FLOOR_TILES.<Block>getObjectsAsArray(new Block[0]))
				.add(DeferredRegisters.BLOCK_TRITANIUM_CRATES.<Block>getObjectsAsArray(new Block[0]))
				.add(DeferredRegisters.BLOCK_SOLAR_PANEL.get()).add(DeferredRegisters.BLOCK_MATTER_DECOMPOSER.get())
				.add(DeferredRegisters.BLOCK_MATTER_RECYCLER.get()).add(DeferredRegisters.BLOCK_CHARGER.get())
				.add(DeferredRegisters.BLOCK_MICROWAVE.get()).add(DeferredRegisters.BLOCK_TRANSPORTER.get())
				.add(DeferredRegisters.BLOCK_VENT_OPEN.get()).add(DeferredRegisters.BLOCK_VENT_CLOSED.get())
				.add(DeferredRegisters.BLOCK_SPACETIME_ACCELERATOR.get())
				.add(DeferredRegisters.BLOCK_MATTER_NETWORK_CABLES.getObjectsAsArray(new Block[0]))
				.add(DeferredRegisters.BLOCK_CHUNKLOADER.get()).add(DeferredRegisters.BLOCK_MATTER_ANALYZER.get())
				.add(DeferredRegisters.BLOCK_PATTERN_STORAGE.get()).add(DeferredRegisters.BLOCK_PATTERN_MONITOR.get())
				.add(DeferredRegisters.BLOCK_MATTER_REPLICATOR.get());

		tag(BlockTags.NEEDS_STONE_TOOL).add(DeferredRegisters.BLOCK_REGULAR_TRITANIUM_PLATING.get())
				.add(DeferredRegisters.BLOCK_COLORED_TRITANIUM_PLATING.<Block>getObjectsAsArray(new Block[0]))
				.add(DeferredRegisters.BLOCK_FLOOR_TILE.<Block>getObjectsAsArray(new Block[0]))
				.add(DeferredRegisters.BLOCK_FLOOR_TILES.<Block>getObjectsAsArray(new Block[0]))
				.add(DeferredRegisters.BLOCK_TRITANIUM_CRATES.<Block>getObjectsAsArray(new Block[0]))
				.add(DeferredRegisters.BLOCK_SOLAR_PANEL.get()).add(DeferredRegisters.BLOCK_MATTER_DECOMPOSER.get())
				.add(DeferredRegisters.BLOCK_MATTER_RECYCLER.get()).add(DeferredRegisters.BLOCK_CHARGER.get())
				.add(DeferredRegisters.BLOCK_MICROWAVE.get()).add(DeferredRegisters.BLOCK_TRANSPORTER.get())
				.add(DeferredRegisters.BLOCK_VENT_OPEN.get()).add(DeferredRegisters.BLOCK_VENT_CLOSED.get())
				.add(DeferredRegisters.BLOCK_SPACETIME_ACCELERATOR.get())
				.add(DeferredRegisters.BLOCK_MATTER_NETWORK_CABLES.getObjectsAsArray(new Block[0]))
				.add(DeferredRegisters.BLOCK_CHUNKLOADER.get()).add(DeferredRegisters.BLOCK_MATTER_ANALYZER.get())
				.add(DeferredRegisters.BLOCK_PATTERN_STORAGE.get()).add(DeferredRegisters.BLOCK_PATTERN_MONITOR.get())
				.add(DeferredRegisters.BLOCK_MATTER_REPLICATOR.get());
	}

}
