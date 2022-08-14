package matteroverdrive.registry;

import java.util.function.Function;
import java.util.function.Supplier;

import matteroverdrive.References;
import matteroverdrive.common.block.BlockColored;
import matteroverdrive.common.block.BlockCustomGlass;
import matteroverdrive.common.block.BlockMachine;
import matteroverdrive.common.block.BlockOverdrive;
import matteroverdrive.common.block.BlockTritaniumCrate;
import matteroverdrive.common.block.OverdriveBlockColors;
import matteroverdrive.common.block.cable.types.BlockMatterConduit;
import matteroverdrive.common.block.cable.types.BlockMatterNetworkCable;
import matteroverdrive.common.block.charger.BlockAndroidChargerChild;
import matteroverdrive.common.block.charger.BlockAndroidChargerParent;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.block.type.TypeMatterConduit;
import matteroverdrive.common.block.type.TypeMatterNetworkCable;
import matteroverdrive.common.blockitem.BlockItemColored;
import matteroverdrive.common.tile.TileCharger;
import matteroverdrive.common.tile.TileChunkloader;
import matteroverdrive.common.tile.TileInscriber;
import matteroverdrive.common.tile.TileMatterDecomposer;
import matteroverdrive.common.tile.TileMatterRecycler;
import matteroverdrive.common.tile.TileMicrowave;
import matteroverdrive.common.tile.TileSpacetimeAccelerator;
import matteroverdrive.common.tile.TileTritaniumCrate;
import matteroverdrive.common.tile.TileSolarPanel;
import matteroverdrive.core.block.OverdriveBlockProperties;
import matteroverdrive.core.registers.BulkRegister;
import matteroverdrive.core.registers.IBulkRegistryObject;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import matteroverdrive.common.tile.TileTritaniumCrate.CrateColors;
import matteroverdrive.common.tile.matter_network.TileMatterAnalyzer;
import matteroverdrive.common.tile.matter_network.TilePatternMonitor;
import matteroverdrive.common.tile.matter_network.TilePatternStorage;
import matteroverdrive.common.tile.matter_network.matter_replicator.TileMatterReplicator;
import matteroverdrive.common.tile.transporter.TileTransporter;

public class BlockRegistry {

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, References.ID);
	
	/**
	 * REGISTRY ORDER NOTES:
	 * 
	 * Register decoration blocks, then crates, then machines
	 */
	
	
	//Decoration Blocks
	
	public static final RegistryObject<Block> BLOCK_REGULAR_TRITANIUM_PLATING = registerBlock("tritanium_plating",
			() -> new BlockOverdrive(Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1F, 100F),
					false));
	public static final BulkRegister<Block> BLOCK_COLORED_TRITANIUM_PLATING = bulkBlock(
			color -> registerColoredBlock(((OverdriveBlockColors) color).id("tritanium_plating_"),
					() -> new BlockColored(
							Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1F, 100F),
							((OverdriveBlockColors) color).color, false),
					((OverdriveBlockColors) color).color),
			OverdriveBlockColors.values());
	public static final BulkRegister<Block> BLOCK_FLOOR_TILE = bulkBlock(
			color -> registerColoredBlock(((OverdriveBlockColors) color).id("floor_tile_"),
					() -> new BlockColored(
							Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1F, 100F),
							((OverdriveBlockColors) color).color, false),
					((OverdriveBlockColors) color).color),
			OverdriveBlockColors.values());
	public static final BulkRegister<Block> BLOCK_FLOOR_TILES = bulkBlock(
			color -> registerColoredBlock(((OverdriveBlockColors) color).id("floor_tiles_"),
					() -> new BlockColored(
							Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1F, 100F),
							((OverdriveBlockColors) color).color, false),
					((OverdriveBlockColors) color).color),
			OverdriveBlockColors.values());

	public static final RegistryObject<Block> BLOCK_INDUSTRIAL_GLASS = registerBlock("industrial_glass",
			() -> new BlockCustomGlass(0.3F, 0.3F));

	public static final RegistryObject<Block> BLOCK_VENT_OPEN = registerBlock("vent_open",
			() -> new BlockOverdrive(Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1F, 100F),
					false));

	public static final RegistryObject<Block> BLOCK_VENT_CLOSED = registerBlock("vent_closed",
			() -> new BlockOverdrive(Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1F, 100F),
					false));
	
	
	//Crates
	
	public static final BulkRegister<Block> BLOCK_TRITANIUM_CRATES = bulkBlock(crate -> registerBlock(
			((CrateColors) crate).id(),
			() -> new BlockTritaniumCrate(OverdriveBlockProperties
					.from(Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1F, 100F).noOcclusion())
					.setCanBeWaterlogged().setHasFacing(false))),
			TileTritaniumCrate.CrateColors.values());

	
	//Machines
	
	public static final RegistryObject<Block> BLOCK_SOLAR_PANEL = registerBlock(TypeMachine.SOLAR_PANEL.id(),
			() -> new BlockMachine<TileSolarPanel>(TileSolarPanel::new, TypeMachine.SOLAR_PANEL,
					TileRegistry.TILE_SOLAR_PANEL));

	public static final RegistryObject<Block> BLOCK_MATTER_DECOMPOSER = registerBlock(
			TypeMachine.MATTER_DECOMPOSER.id(), () -> new BlockMachine<TileMatterDecomposer>(
					TileMatterDecomposer::new, TypeMachine.MATTER_DECOMPOSER,
					TileRegistry.TILE_MATTER_DECOMPOSER));

	public static final RegistryObject<Block> BLOCK_MATTER_RECYCLER = registerBlock(TypeMachine.MATTER_RECYCLER.id(),
			() -> new BlockMachine<TileMatterRecycler>(
					TileMatterRecycler::new, TypeMachine.MATTER_RECYCLER, TileRegistry.TILE_MATTER_RECYCLER));

	public static final RegistryObject<Block> BLOCK_CHARGER_CHILD = registerBlock("charger_child",
			() -> new BlockAndroidChargerChild());

	public static final RegistryObject<Block> BLOCK_CHARGER = registerBlock(TypeMachine.CHARGER.id(),
			() -> new BlockAndroidChargerParent<TileCharger>(TileCharger::new, TypeMachine.CHARGER,
					TileRegistry.TILE_CHARGER));

	public static final RegistryObject<Block> BLOCK_MICROWAVE = registerBlock(TypeMachine.MICROWAVE.id(),
			() -> new BlockMachine<TileMicrowave>(TileMicrowave::new, TypeMachine.MICROWAVE,
					TileRegistry.TILE_MICROWAVE));

	public static final RegistryObject<Block> BLOCK_INSCRIBER = registerBlock(TypeMachine.INSCRIBER.id(),
			() -> new BlockMachine<TileInscriber>(TileInscriber::new, TypeMachine.INSCRIBER,
					TileRegistry.TILE_INSCRIBER));

	public static final BulkRegister<Block> BLOCK_MATTER_CONDUITS = bulkBlock(
			conduit -> registerBlock(((TypeMatterConduit) conduit).id(),
					() -> new BlockMatterConduit((TypeMatterConduit) conduit)),
			TypeMatterConduit.values());

	public static final RegistryObject<Block> BLOCK_TRANSPORTER = registerBlock(TypeMachine.TRANSPORTER.id(),
			() -> new BlockMachine<TileTransporter>(TileTransporter::new, TypeMachine.TRANSPORTER,
					TileRegistry.TILE_TRANSPORTER));

	public static final RegistryObject<Block> BLOCK_SPACETIME_ACCELERATOR = registerBlock(
			TypeMachine.SPACETIME_ACCELERATOR.id(),
			() -> new BlockMachine<TileSpacetimeAccelerator>(TileSpacetimeAccelerator::new,
					TypeMachine.SPACETIME_ACCELERATOR, TileRegistry.TILE_SPACETIME_ACCELERATOR));

	public static final BulkRegister<Block> BLOCK_MATTER_NETWORK_CABLES = bulkBlock(
			cable -> registerBlock(cable.id(), () -> new BlockMatterNetworkCable((TypeMatterNetworkCable) cable)),
			TypeMatterNetworkCable.values());

	public static final RegistryObject<Block> BLOCK_CHUNKLOADER = registerBlock(TypeMachine.CHUNKLOADER.id(),
			() -> new BlockMachine<TileChunkloader>(TileChunkloader::new, TypeMachine.CHUNKLOADER,
					TileRegistry.TILE_CHUNKLOADER));

	public static final RegistryObject<Block> BLOCK_MATTER_ANALYZER = registerBlock(TypeMachine.MATTER_ANALYZER.id(),
			() -> new BlockMachine<TileMatterAnalyzer>(TileMatterAnalyzer::new, TypeMachine.MATTER_ANALYZER,
					TileRegistry.TILE_MATTER_ANALYZER));

	public static final RegistryObject<Block> BLOCK_PATTERN_STORAGE = registerBlock(TypeMachine.PATTERN_STORAGE.id(),
			() -> new BlockMachine<TilePatternStorage>(TilePatternStorage::new, TypeMachine.PATTERN_STORAGE,
					TileRegistry.TILE_PATTERN_STORAGE));

	public static final RegistryObject<Block> BLOCK_PATTERN_MONITOR = registerBlock(TypeMachine.PATTERN_MONITOR.id(),
			() -> new BlockMachine<TilePatternMonitor>(TilePatternMonitor::new, TypeMachine.PATTERN_MONITOR,
					TileRegistry.TILE_PATTERN_MONITOR));

	public static final RegistryObject<Block> BLOCK_MATTER_REPLICATOR = registerBlock(
			TypeMachine.MATTER_REPLICATOR.id(), () -> new BlockMachine<TileMatterReplicator>(TileMatterReplicator::new,
					TypeMachine.MATTER_REPLICATOR, TileRegistry.TILE_MATTER_REPLICATOR));
	
	
	
	
	
	
	
	
	//Functional Methods
	
	private static RegistryObject<Block> registerBlock(String name, Supplier<Block> supplier) {
		return registerBlock(name, supplier, new Item.Properties().tab(References.MAIN));
	}

	private static RegistryObject<Block> registerBlock(String name, Supplier<Block> supplier,
			net.minecraft.world.item.Item.Properties properties) {
		RegistryObject<Block> block = BLOCKS.register(name, supplier);
		ItemRegistry.ITEMS.register(name, () -> new BlockItem(block.get(), properties));
		return block;
	}

	private static RegistryObject<Block> registerColoredBlock(String name, Supplier<Block> supplier, int color) {
		return registerColoredBlock(name, supplier, new Item.Properties().tab(References.MAIN), color);
	}

	private static RegistryObject<Block> registerColoredBlock(String name, Supplier<Block> supplier,
			net.minecraft.world.item.Item.Properties properties, int color) {
		RegistryObject<Block> block = BLOCKS.register(name, supplier);
		ItemRegistry.ITEMS.register(name, () -> new BlockItemColored(block.get(), properties, color));
		return block;
	}

	private static BulkRegister<Block> bulkBlock(Function<IBulkRegistryObject, RegistryObject<Block>> factory,
			IBulkRegistryObject[] bulkValues) {
		return new BulkRegister<>(factory, bulkValues);
	}
}
