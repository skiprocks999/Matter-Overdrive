package matteroverdrive.core.datagen.client;

import matteroverdrive.References;
import matteroverdrive.common.block.OverdriveBlockColors;
import matteroverdrive.common.block.OverdriveBlockStates;
import matteroverdrive.common.block.OverdriveBlockStates.VerticalFacing;
import matteroverdrive.common.tile.TileTritaniumCrate.CrateColors;
import matteroverdrive.core.block.GenericEntityBlock;
import matteroverdrive.registry.BlockRegistry;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelFile.ExistingModelFile;
import net.minecraftforge.client.model.generators.loaders.ObjModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class OverdriveBlockStateProvider extends BlockStateProvider {

	private final ExistingModelFile coloredTritaniumPlatingFile;
	private final ExistingModelFile floorTileFile;
	private final ExistingModelFile floorTilesFile;
	private final ExistingModelFile chunkloader;
	private final ExistingModelFile spacetimeAccelerator;
	private final ExistingModelFile patternMonitor;
	

	public OverdriveBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
		super(gen, References.ID, exFileHelper);
		coloredTritaniumPlatingFile = existingBlock("tritanium_plating_colorless", exFileHelper);
		floorTileFile = existingBlock("floor_tile_colorless", exFileHelper);
		floorTilesFile = existingBlock("floor_tiles_colorless", exFileHelper);
		chunkloader = existingBlock("chunkloader", exFileHelper);
		spacetimeAccelerator = existingBlock("spacetime_accelerator", exFileHelper);
		patternMonitor = existingBlock("pattern_monitor", exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		simpleBlock(BlockRegistry.BLOCK_REGULAR_TRITANIUM_PLATING, models().cubeAll("tritanium_plating", blockLoc("decorative/tritanium_plating")), true);
		for (OverdriveBlockColors color : OverdriveBlockColors.values()) {
			simpleBlock(BlockRegistry.BLOCK_COLORED_TRITANIUM_PLATING.get(color), coloredTritaniumPlatingFile, true);
			simpleBlock(BlockRegistry.BLOCK_FLOOR_TILE.get(color), floorTileFile, true);
			simpleBlock(BlockRegistry.BLOCK_FLOOR_TILES.get(color), floorTilesFile, true);
		}
		for(CrateColors color : CrateColors.values()) {
			String name = color.id();
			horrRotatedBlock(BlockRegistry.BLOCK_TRITANIUM_CRATES.get(color), getObjModel(name, "block/" + name, "block/crate/" + name), true);
		}
		glassBlock(BlockRegistry.BLOCK_INDUSTRIAL_GLASS, true);
		simpleBlock(BlockRegistry.BLOCK_VENT_OPEN, true);
		simpleBlock(BlockRegistry.BLOCK_VENT_CLOSED, true);
		
		
		
		simpleBlock(BlockRegistry.BLOCK_CHUNKLOADER, chunkloader, true);
		simpleBlock(BlockRegistry.BLOCK_CHARGER_CHILD, false);
		simpleBlock(BlockRegistry.BLOCK_TRANSPORTER, blockTopBottom(BlockRegistry.BLOCK_TRANSPORTER, "block/transporter/transporter_top",
				"block/transporter/transporter_bottom", "block/transporter/transporter_side"), true);
		airBlock(BlockRegistry.BLOCK_CHARGER, "block/charger", false);
		horrRotatedBlock(BlockRegistry.BLOCK_INSCRIBER, getObjModel("inscriber", "block/inscriber", "block/inscriber"), true);
		bottomSlabBlock(BlockRegistry.BLOCK_SOLAR_PANEL, blockLoc("base"), blockLoc("base"), blockLoc("solar_panel"), true);
		horrRotatedLitBlock(BlockRegistry.BLOCK_MATTER_ANALYZER, getMatAnaBase("", ""), getMatAnaBase("_on", "_on"), true);
		horrRotatedLitBlock(BlockRegistry.BLOCK_MATTER_DECOMPOSER, getMatDecomBase("", "empty"), getMatDecomBase("_on", "full"), true);
		horrRotatedLitBlock(BlockRegistry.BLOCK_MATTER_RECYCLER, getMatRecBase("", ""), getMatRecBase("_on", "_anim"), true);
		horrRotatedBlock(BlockRegistry.BLOCK_MATTER_REPLICATOR, getObjModel("matter_replicator", "block/matter_replicator")
				.texture("bottom", blockLoc("base")).texture("back", blockLoc("network_port")).texture("sides", blockLoc("vent"))
				.texture("front", blockLoc("matter_replicator")).texture("particle", "#bottom").renderType("cutout"), true);
		horrRotatedLitBlock(BlockRegistry.BLOCK_MICROWAVE, getMicroBase("", ""), getMicroBase("_on", "_on"), true);
		horrRotatedBlock(BlockRegistry.BLOCK_PATTERN_STORAGE, getObjModel("pattern_storage", "block/pattern_storage")
				.texture("base", blockLoc("pattern_storage")).texture("vent", blockLoc("vent")).texture("particle", "#base"), true);
		horrRotatedBlock(BlockRegistry.BLOCK_SPACETIME_ACCELERATOR, spacetimeAccelerator, true);
		omniDirBlock(BlockRegistry.BLOCK_PATTERN_MONITOR, patternMonitor, true);
		
		
	}
	
	private void simpleBlock(RegistryObject<Block> block, ModelFile file, boolean registerItem) {
		simpleBlock(block.get(), file);
		if(registerItem) simpleBlockItem(block.get(), file);
	}
	
	private void simpleBlock(RegistryObject<Block> block, boolean registerItem) {
		simpleBlock(block, cubeAll(block.get()), registerItem);
	}

	private void glassBlock(RegistryObject<Block> block, boolean registerItem) {
		BlockModelBuilder builder = models().cubeAll(name(block.get()), blockTexture(block.get())).renderType("cutout");
		getVariantBuilder(block.get()).partialState().setModels(new ConfiguredModel(builder));
		if(registerItem) simpleBlockItem(block.get(), builder);
	}
	
	private void airBlock(RegistryObject<Block> block, String particleTexture, boolean registerItem) {
		BlockModelBuilder builder = models().getBuilder(name(block.get())).texture("particle", modLoc(particleTexture));
		getVariantBuilder(block.get()).partialState().setModels(new ConfiguredModel(builder));
		if(registerItem) simpleBlockItem(block.get(), builder);
	}

	public void bottomSlabBlock(RegistryObject<Block> block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, boolean registerItem) {
		BlockModelBuilder builder = models().slab(name(block.get()), side, bottom, top);
		getVariantBuilder(block.get()).partialState().setModels(new ConfiguredModel(builder));
		if(registerItem) simpleBlockItem(block.get(), builder);
	}
	
	private void horrRotatedBlock(RegistryObject<Block> block, ModelFile modelFile, boolean registerItem) {
		getVariantBuilder(block.get())
			.partialState().with(GenericEntityBlock.FACING, Direction.NORTH).modelForState().modelFile(modelFile).rotationY(0).addModel()
			.partialState().with(GenericEntityBlock.FACING, Direction.EAST).modelForState().modelFile(modelFile).rotationY(90).addModel()
			.partialState().with(GenericEntityBlock.FACING, Direction.SOUTH).modelForState().modelFile(modelFile).rotationY(180).addModel()
			.partialState().with(GenericEntityBlock.FACING, Direction.WEST).modelForState().modelFile(modelFile).rotationY(270).addModel();
		if(registerItem) simpleBlockItem(block.get(), modelFile);
	}
	
	private void horrRotatedLitBlock(RegistryObject<Block> block, ModelFile off, ModelFile on, boolean registerItem) {
		getVariantBuilder(block.get())
			.partialState().with(GenericEntityBlock.FACING, Direction.NORTH).with(BlockStateProperties.LIT, false).modelForState().modelFile(off).rotationY(0).addModel()
			.partialState().with(GenericEntityBlock.FACING, Direction.EAST).with(BlockStateProperties.LIT, false).modelForState().modelFile(off).rotationY(90).addModel()
			.partialState().with(GenericEntityBlock.FACING, Direction.SOUTH).with(BlockStateProperties.LIT, false).modelForState().modelFile(off).rotationY(180).addModel()
			.partialState().with(GenericEntityBlock.FACING, Direction.WEST).with(BlockStateProperties.LIT, false).modelForState().modelFile(off).rotationY(270).addModel()
			.partialState().with(GenericEntityBlock.FACING, Direction.NORTH).with(BlockStateProperties.LIT, true).modelForState().modelFile(on).rotationY(0).addModel()
			.partialState().with(GenericEntityBlock.FACING, Direction.EAST).with(BlockStateProperties.LIT, true).modelForState().modelFile(on).rotationY(90).addModel()
			.partialState().with(GenericEntityBlock.FACING, Direction.SOUTH).with(BlockStateProperties.LIT, true).modelForState().modelFile(on).rotationY(180).addModel()
			.partialState().with(GenericEntityBlock.FACING, Direction.WEST).with(BlockStateProperties.LIT, true).modelForState().modelFile(on).rotationY(270).addModel();
		if(registerItem) simpleBlockItem(block.get(), off);
		
	}
	
	private void omniDirBlock(RegistryObject<Block> block, ModelFile model, boolean registerItem) {
		getVariantBuilder(block.get())
			.partialState().with(GenericEntityBlock.FACING, Direction.NORTH).with(OverdriveBlockStates.VERTICAL_FACING, VerticalFacing.NONE).modelForState().modelFile(model).rotationY(0).addModel()
			.partialState().with(GenericEntityBlock.FACING, Direction.EAST).with(OverdriveBlockStates.VERTICAL_FACING, VerticalFacing.NONE).modelForState().modelFile(model).rotationY(90).addModel()
			.partialState().with(GenericEntityBlock.FACING, Direction.SOUTH).with(OverdriveBlockStates.VERTICAL_FACING, VerticalFacing.NONE).modelForState().modelFile(model).rotationY(180).addModel()
			.partialState().with(GenericEntityBlock.FACING, Direction.WEST).with(OverdriveBlockStates.VERTICAL_FACING, VerticalFacing.NONE).modelForState().modelFile(model).rotationY(270).addModel()
			.partialState().with(GenericEntityBlock.FACING, Direction.NORTH).with(OverdriveBlockStates.VERTICAL_FACING, VerticalFacing.UP).modelForState().modelFile(model).rotationY(0).rotationX(270).addModel()
			.partialState().with(GenericEntityBlock.FACING, Direction.EAST).with(OverdriveBlockStates.VERTICAL_FACING, VerticalFacing.UP).modelForState().modelFile(model).rotationY(90).rotationX(270).addModel()
			.partialState().with(GenericEntityBlock.FACING, Direction.SOUTH).with(OverdriveBlockStates.VERTICAL_FACING, VerticalFacing.UP).modelForState().modelFile(model).rotationY(180).rotationX(270).addModel()
			.partialState().with(GenericEntityBlock.FACING, Direction.WEST).with(OverdriveBlockStates.VERTICAL_FACING, VerticalFacing.UP).modelForState().modelFile(model).rotationY(270).rotationX(270).addModel()
			.partialState().with(GenericEntityBlock.FACING, Direction.NORTH).with(OverdriveBlockStates.VERTICAL_FACING, VerticalFacing.DOWN).modelForState().modelFile(model).rotationY(0).rotationX(90).addModel()
			.partialState().with(GenericEntityBlock.FACING, Direction.EAST).with(OverdriveBlockStates.VERTICAL_FACING, VerticalFacing.DOWN).modelForState().modelFile(model).rotationY(90).rotationX(90).addModel()
			.partialState().with(GenericEntityBlock.FACING, Direction.SOUTH).with(OverdriveBlockStates.VERTICAL_FACING, VerticalFacing.DOWN).modelForState().modelFile(model).rotationY(180).rotationX(90).addModel()
			.partialState().with(GenericEntityBlock.FACING, Direction.WEST).with(OverdriveBlockStates.VERTICAL_FACING, VerticalFacing.DOWN).modelForState().modelFile(model).rotationY(270).rotationX(90).addModel();
		if(registerItem) simpleBlockItem(block.get(), model);
	}
	
	private BlockModelBuilder getObjModel(String name, String modelLoc, String texture) {
		return models().withExistingParent("block/" + name, "cube").customLoader(ObjModelBuilder::begin).flipV(true)
				.modelLocation(modLoc("models/" + modelLoc + ".obj")).end().texture("texture0", texture).texture("particle", "#texture0");
	}
	
	private BlockModelBuilder getObjModel(String name, String modelLoc) {
		return models().withExistingParent("block/" + name, "cube").customLoader(ObjModelBuilder::begin).flipV(true)
				.modelLocation(modLoc("models/" + modelLoc + ".obj")).end();
	}
	
	private BlockModelBuilder blockTopBottom(RegistryObject<Block> block, String top, String bottom, String side) {
		return models().cubeBottomTop(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), new ResourceLocation(References.ID, side),
				new ResourceLocation(References.ID, bottom), new ResourceLocation(References.ID, top));
	}
	
	private BlockModelBuilder getMatAnaBase(String name, String frontText) {
		return models().withExistingParent("block/matter_analyzer" + name, modLoc("block/parent/matter_analyzer_base"))
				.texture("bottom", modLoc("block/base")).texture("top", modLoc("block/matter_analyzer/matter_analyzer_top"))
				.texture("side", modLoc("block/vent_closed")).texture("back", modLoc("block/network_port"))
				.texture("particle", "block/matter_analyzer/matter_analyzer_front").texture("front", modLoc("block/matter_analyzer/matter_analyzer_front"));
	}
	
	private BlockModelBuilder getMatDecomBase(String name, String frontText) {
		return models().orientableWithBottom("block/matter_decomposer" + name, modLoc("block/base_stripes"), modLoc("block/tank_" + frontText), 
				modLoc("block/vent_closed"), modLoc("block/decomposer_top"));
	}
	
	private BlockModelBuilder getMatRecBase(String name, String frontText) {
		return models().orientableWithBottom("block/matter_recycler" + name, modLoc("block/base_stripes"), modLoc("block/recycler_front" + frontText), 
				modLoc("block/vent_closed"), modLoc("block/decomposer_top"));
	}
	
	private BlockModelBuilder getMicroBase(String name, String frontText) {
		return models().withExistingParent("block/microwave" + name, modLoc("block/parent/microwave_base"))
				.texture("0", modLoc("block/microwave/microwave")).texture("1", modLoc("block/microwave/microwave_front" + frontText))
				.texture("2", modLoc("block/microwave/microwave_back")).texture("particle", "#0");
	}

	private ResourceLocation key(Block block) {
		return ForgeRegistries.BLOCKS.getKey(block);
	}

	private String name(Block block) {
		return key(block).getPath();
	}
	
	private ExistingModelFile existingBlock(String loc, ExistingFileHelper exFileHelper) {
		return new ExistingModelFile(new ResourceLocation(References.ID + ":block/" + loc), exFileHelper);
	}
	
	private ResourceLocation blockLoc(String texture) {
		return modLoc("block/" + texture);
	}

}
