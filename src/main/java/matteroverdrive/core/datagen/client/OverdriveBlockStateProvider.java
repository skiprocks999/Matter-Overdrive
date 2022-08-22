package matteroverdrive.core.datagen.client;

import matteroverdrive.References;
import matteroverdrive.common.block.OverdriveBlockColors;
import matteroverdrive.registry.BlockRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile.ExistingModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

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

		bottomSlabBlock(BlockRegistry.BLOCK_SOLAR_PANEL.get(), "matteroverdrive:block/base", "matteroverdrive:block/base",
				"matteroverdrive:block/solar_panel");
		simpleBlock(BlockRegistry.BLOCK_CHARGER_CHILD.get());

		glassBlock(BlockRegistry.BLOCK_INDUSTRIAL_GLASS.get());
		simpleBlock(BlockRegistry.BLOCK_VENT_OPEN.get());
		simpleBlock(BlockRegistry.BLOCK_VENT_CLOSED.get());

	}

	private void glassBlock(Block block) {
		getVariantBuilder(block).partialState().setModels(
				new ConfiguredModel(models().cubeAll(name(block), blockTexture(block)).renderType("cutout")));
	}

	public void bottomSlabBlock(Block block, String side, String bottom, String top) {
		getVariantBuilder(block).partialState()
				.setModels(new ConfiguredModel(models().slab(name(block), new ResourceLocation(side), new ResourceLocation(bottom), new ResourceLocation(top))));
	}

	private ResourceLocation key(Block block) {
		return ForgeRegistries.BLOCKS.getKey(block);
	}

	private String name(Block block) {
		return key(block).getPath();
	}

}
