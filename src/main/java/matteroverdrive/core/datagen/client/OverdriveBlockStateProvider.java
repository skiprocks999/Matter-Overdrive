package matteroverdrive.core.datagen.client;

import matteroverdrive.References;
import matteroverdrive.common.block.OverdriveBlockColors;
import matteroverdrive.registry.BlockRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile.ExistingModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class OverdriveBlockStateProvider extends BlockStateProvider {

	private ExistingModelFile tritaniumPlatingFile;
	private ExistingModelFile coloredTritaniumPlatingFile;
	private ExistingModelFile floorTileFile;
	private ExistingModelFile floorTilesFile;

	public OverdriveBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
		super(gen, References.ID, exFileHelper);
		tritaniumPlatingFile = new ExistingModelFile(new ResourceLocation(References.ID + ":block/tritanium_plating"),
				exFileHelper);
		coloredTritaniumPlatingFile = new ExistingModelFile(
				new ResourceLocation(References.ID + ":block/tritanium_plating_colorless"), exFileHelper);
		floorTileFile = new ExistingModelFile(new ResourceLocation(References.ID + ":block/floor_tile_colorless"),
				exFileHelper);
		floorTilesFile = new ExistingModelFile(new ResourceLocation(References.ID + ":block/floor_tiles_colorless"),
				exFileHelper);

	}

	@Override
	protected void registerStatesAndModels() {
		simpleBlock(BlockRegistry.BLOCK_REGULAR_TRITANIUM_PLATING.get(), tritaniumPlatingFile);
		for (OverdriveBlockColors color : OverdriveBlockColors.values()) {
			simpleBlock(BlockRegistry.BLOCK_COLORED_TRITANIUM_PLATING.get(color).get(), coloredTritaniumPlatingFile);
			simpleBlock(BlockRegistry.BLOCK_FLOOR_TILE.get(color).get(), floorTileFile);
			simpleBlock(BlockRegistry.BLOCK_FLOOR_TILES.get(color).get(), floorTilesFile);
		}
		simpleBlock(BlockRegistry.BLOCK_SOLAR_PANEL.get());
		simpleBlock(BlockRegistry.BLOCK_CHARGER_CHILD.get());

		simpleBlock(BlockRegistry.BLOCK_INDUSTRIAL_GLASS.get());
		simpleBlock(BlockRegistry.BLOCK_VENT_OPEN.get());
		simpleBlock(BlockRegistry.BLOCK_VENT_CLOSED.get());
	}

}
