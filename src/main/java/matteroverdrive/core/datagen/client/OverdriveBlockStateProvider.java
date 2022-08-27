package matteroverdrive.core.datagen.client;

import matteroverdrive.References;
import matteroverdrive.common.block.OverdriveBlockColors;
import matteroverdrive.common.block.OverdriveBlockStates;
import matteroverdrive.common.block.OverdriveBlockStates.VerticalFacing;
import matteroverdrive.common.tile.TileTritaniumCrate.CrateColors;
import matteroverdrive.core.block.GenericEntityBlock;
import matteroverdrive.core.datagen.utils.ExistingLightableModel;
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
	private final ExistingLightableModel matterAnalyzer;
	private final ExistingLightableModel matterDecomposer;
	private final ExistingLightableModel matterRecycler;
	private final ExistingLightableModel microwave;
	

	public OverdriveBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
		super(gen, References.ID, exFileHelper);
		coloredTritaniumPlatingFile = existingBlock("tritanium_plating_colorless", exFileHelper);
		floorTileFile = existingBlock("floor_tile_colorless", exFileHelper);
		floorTilesFile = existingBlock("floor_tiles_colorless", exFileHelper);
		chunkloader = existingBlock("chunkloader", exFileHelper);
		spacetimeAccelerator = existingBlock("spacetime_accelerator", exFileHelper);
		patternMonitor = existingBlock("pattern_monitor", exFileHelper);
		matterAnalyzer = new ExistingLightableModel("matter_analyzer", exFileHelper);
		matterDecomposer = new ExistingLightableModel("matter_decomposer", exFileHelper);
		matterRecycler = new ExistingLightableModel("matter_recycler", exFileHelper);
		microwave = new ExistingLightableModel("microwave", exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		simpleBlock(BlockRegistry.BLOCK_REGULAR_TRITANIUM_PLATING.get(), models().cubeAll("tritanium_plating", new ResourceLocation(References.ID, "block/decorative/tritanium_plating")));
		for (OverdriveBlockColors color : OverdriveBlockColors.values()) {
			simpleBlock(BlockRegistry.BLOCK_COLORED_TRITANIUM_PLATING.get(color).get(), coloredTritaniumPlatingFile);
			simpleBlock(BlockRegistry.BLOCK_FLOOR_TILE.get(color).get(), floorTileFile);
			simpleBlock(BlockRegistry.BLOCK_FLOOR_TILES.get(color).get(), floorTilesFile);
		}
		for(CrateColors color : CrateColors.values()) {
			String name = color.id();
			horrRotatedBlock(BlockRegistry.BLOCK_TRITANIUM_CRATES.get(color).get(), getObjModel(name, "block/" + name, "block/crate/" + name));
		}
		simpleBlock(BlockRegistry.BLOCK_CHUNKLOADER.get(), chunkloader);
		horrRotatedBlock(BlockRegistry.BLOCK_INSCRIBER.get(), getObjModel("inscriber", "block/inscriber", "block/inscriber"));
		bottomSlabBlock(BlockRegistry.BLOCK_SOLAR_PANEL.get(), "matteroverdrive:block/base", "matteroverdrive:block/base",
				"matteroverdrive:block/solar_panel");
		simpleBlock(BlockRegistry.BLOCK_CHARGER_CHILD.get());
		horrRotatedLitBlock(BlockRegistry.BLOCK_MATTER_ANALYZER.get(), matterAnalyzer);
		horrRotatedLitBlock(BlockRegistry.BLOCK_MATTER_DECOMPOSER.get(), matterDecomposer);
		horrRotatedLitBlock(BlockRegistry.BLOCK_MATTER_RECYCLER.get(), matterRecycler);
		horrRotatedBlock(BlockRegistry.BLOCK_MATTER_REPLICATOR.get(), getObjModel("matter_replicator", "block/matter_replicator")
				.texture("bottom", modLoc("block/base")).texture("back", modLoc("block/network_port")).texture("sides", modLoc("block/vent"))
				.texture("front", modLoc("block/matter_replicator")).texture("particle", "#bottom"));
		horrRotatedLitBlock(BlockRegistry.BLOCK_MICROWAVE.get(), microwave);
		horrRotatedBlock(BlockRegistry.BLOCK_PATTERN_STORAGE.get(), getObjModel("pattern_storage", "block/pattern_storage")
				.texture("base", modLoc("block/pattern_storage")).texture("vent", modLoc("block/vent")).texture("particle", "#base"));
		horrRotatedBlock(BlockRegistry.BLOCK_SPACETIME_ACCELERATOR.get(), spacetimeAccelerator);
		airBlock(BlockRegistry.BLOCK_CHARGER.get(), "block/charger");
		simpleBlock(BlockRegistry.BLOCK_TRANSPORTER.get(), blockTopBottom(BlockRegistry.BLOCK_TRANSPORTER, "block/transporter/transporter_top",
				"block/transporter/transporter_bottom", "block/transporter/transporter_side"));
		omniDirBlock(BlockRegistry.BLOCK_PATTERN_MONITOR.get(), patternMonitor);
		
		
		glassBlock(BlockRegistry.BLOCK_INDUSTRIAL_GLASS.get());
		simpleBlock(BlockRegistry.BLOCK_VENT_OPEN.get());
		simpleBlock(BlockRegistry.BLOCK_VENT_CLOSED.get());
		

	}

	private void glassBlock(Block block) {
		getVariantBuilder(block).partialState().setModels(
				new ConfiguredModel(models().cubeAll(name(block), blockTexture(block)).renderType("cutout")));
	}
	
	private void airBlock(Block block, String particleTexture) {
		getVariantBuilder(block).partialState().setModels(new ConfiguredModel(models().getBuilder(name(block)).texture("particle", new ResourceLocation(References.ID, particleTexture))));
	}

	public void bottomSlabBlock(Block block, String side, String bottom, String top) {
		getVariantBuilder(block).partialState()
				.setModels(new ConfiguredModel(models().slab(name(block), new ResourceLocation(side), new ResourceLocation(bottom), new ResourceLocation(top))));
	}
	
	private void horrRotatedBlock(Block block, ModelFile modelFile) {
		getVariantBuilder(block)
			.partialState().with(GenericEntityBlock.FACING, Direction.NORTH).modelForState().modelFile(modelFile).rotationY(0).addModel()
			.partialState().with(GenericEntityBlock.FACING, Direction.EAST).modelForState().modelFile(modelFile).rotationY(90).addModel()
			.partialState().with(GenericEntityBlock.FACING, Direction.SOUTH).modelForState().modelFile(modelFile).rotationY(180).addModel()
			.partialState().with(GenericEntityBlock.FACING, Direction.WEST).modelForState().modelFile(modelFile).rotationY(270).addModel();
	}
	
	private void horrRotatedLitBlock(Block block, ExistingLightableModel existing) {
		getVariantBuilder(block)
			.partialState().with(GenericEntityBlock.FACING, Direction.NORTH).with(BlockStateProperties.LIT, false).modelForState().modelFile(existing.off).rotationY(0).addModel()
			.partialState().with(GenericEntityBlock.FACING, Direction.EAST).with(BlockStateProperties.LIT, false).modelForState().modelFile(existing.off).rotationY(90).addModel()
			.partialState().with(GenericEntityBlock.FACING, Direction.SOUTH).with(BlockStateProperties.LIT, false).modelForState().modelFile(existing.off).rotationY(180).addModel()
			.partialState().with(GenericEntityBlock.FACING, Direction.WEST).with(BlockStateProperties.LIT, false).modelForState().modelFile(existing.off).rotationY(270).addModel()
			.partialState().with(GenericEntityBlock.FACING, Direction.NORTH).with(BlockStateProperties.LIT, true).modelForState().modelFile(existing.on).rotationY(0).addModel()
			.partialState().with(GenericEntityBlock.FACING, Direction.EAST).with(BlockStateProperties.LIT, true).modelForState().modelFile(existing.on).rotationY(90).addModel()
			.partialState().with(GenericEntityBlock.FACING, Direction.SOUTH).with(BlockStateProperties.LIT, true).modelForState().modelFile(existing.on).rotationY(180).addModel()
			.partialState().with(GenericEntityBlock.FACING, Direction.WEST).with(BlockStateProperties.LIT, true).modelForState().modelFile(existing.on).rotationY(270).addModel();
	}
	
	private void omniDirBlock(Block block, ModelFile model) {
		getVariantBuilder(block)
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

	private ResourceLocation key(Block block) {
		return ForgeRegistries.BLOCKS.getKey(block);
	}

	private String name(Block block) {
		return key(block).getPath();
	}
	
	private ExistingModelFile existingBlock(String loc, ExistingFileHelper exFileHelper) {
		return new ExistingModelFile(new ResourceLocation(References.ID + ":block/" + loc), exFileHelper);
	}

}
