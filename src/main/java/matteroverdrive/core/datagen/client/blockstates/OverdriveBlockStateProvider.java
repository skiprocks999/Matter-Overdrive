package matteroverdrive.core.datagen.client.blockstates;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.MatterOverdrive;
import matteroverdrive.References;
import matteroverdrive.common.block.utils.BlockColors;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile.ExistingModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class OverdriveBlockStateProvider extends BlockStateProvider {

	private ExistingModelFile floorTileFile;
	
	public OverdriveBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
		super(gen, References.ID, exFileHelper);
		MatterOverdrive.LOGGER.info("called");
		floorTileFile = new ExistingModelFile(new ResourceLocation(References.ID + ":block/floor_tile_colorless"), exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		MatterOverdrive.LOGGER.info("called");
		for(BlockColors color : BlockColors.values()) {
			simpleBlock(DeferredRegisters.FLOOT_TILE.get(color).get(), floorTileFile);
		}
		
	}

}
