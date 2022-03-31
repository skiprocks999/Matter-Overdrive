package matteroverdrive.core.datagen.server;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.References;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

public class MinableTags extends BlockTagsProvider {

	public MinableTags(DataGenerator pGenerator, ExistingFileHelper existingFileHelper) {
		super(pGenerator, References.ID, existingFileHelper);
	}
	
	@Override
	protected void addTags() {
		tag(BlockTags.MINEABLE_WITH_PICKAXE)
			.add(DeferredRegisters.FLOOR_TILE.<Block>getObjectsAsArray(new Block[0]));
		tag(BlockTags.NEEDS_STONE_TOOL)
			.add(DeferredRegisters.FLOOR_TILE.<Block>getObjectsAsArray(new Block[0]));
		
		tag(BlockTags.MINEABLE_WITH_PICKAXE)
			.add(DeferredRegisters.FLOOR_TILES.<Block>getObjectsAsArray(new Block[0]));
		tag(BlockTags.NEEDS_STONE_TOOL)
			.add(DeferredRegisters.FLOOR_TILES.<Block>getObjectsAsArray(new Block[0]));
	}

}
