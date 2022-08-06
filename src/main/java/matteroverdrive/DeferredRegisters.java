package matteroverdrive;

import java.util.function.Function;
import java.util.function.Supplier;

import matteroverdrive.common.block.*;
import org.apache.commons.compress.utils.Sets;

import matteroverdrive.client.particle.replicator.ParticleOptionReplicator;
import matteroverdrive.client.particle.shockwave.ParticleOptionShockwave;
import matteroverdrive.client.particle.vent.ParticleOptionVent;
import matteroverdrive.common.block.cable.dualside.BlockMatterNetworkCable;
import matteroverdrive.common.block.cable.serverside.BlockMatterConduit;
import matteroverdrive.common.block.charger.BlockAndroidChargerParent;
import matteroverdrive.common.block.machine.old.BlockMachine;
import matteroverdrive.common.block.machine.old.variants.BlockLightableMachine;
import matteroverdrive.common.block.machine.old.variants.BlockVerticalMachine;
import matteroverdrive.common.block.charger.BlockAndroidChargerChild;
import matteroverdrive.common.block.type.BlockColors;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.block.type.TypeMatterConduit;
import matteroverdrive.common.block.type.TypeMatterNetworkCable;
import matteroverdrive.common.block_item.BlockItemColored;
import matteroverdrive.common.inventory.InventoryInscriber;
import matteroverdrive.common.inventory.InventoryMatterAnalyzer;
import matteroverdrive.common.inventory.InventoryMatterDecomposer;
import matteroverdrive.common.inventory.InventoryMatterRecycler;
import matteroverdrive.common.inventory.InventoryMatterReplicator;
import matteroverdrive.common.inventory.InventoryMicrowave;
import matteroverdrive.common.inventory.InventoryPatternMonitor;
import matteroverdrive.common.inventory.InventoryPatternStorage;
import matteroverdrive.common.inventory.InventoryChunkloader;
import matteroverdrive.common.inventory.InventorySolarPanel;
import matteroverdrive.common.inventory.InventorySpacetimeAccelerator;
import matteroverdrive.common.inventory.InventoryTransporter;
import matteroverdrive.common.inventory.InventoryTritaniumCrate;
import matteroverdrive.common.item.ItemPatternDrive;
import matteroverdrive.common.item.ItemUpgrade;
import matteroverdrive.common.item.ItemUpgrade.UpgradeType;
import matteroverdrive.common.item.tools.ItemMatterContainer;
import matteroverdrive.common.item.tools.ItemMatterContainer.ContainerType;
import matteroverdrive.common.item.tools.ItemTransporterFlashdrive;
import matteroverdrive.common.item.tools.electric.ItemBattery;
import matteroverdrive.common.item.tools.electric.ItemBattery.BatteryType;
import matteroverdrive.common.item.tools.electric.ItemEnergyWeapon;
import matteroverdrive.common.item.tools.electric.ItemMatterScanner;
import matteroverdrive.common.item.type.TypeIsolinearCircuit;
import matteroverdrive.common.tile.TileInscriber;
import matteroverdrive.common.tile.TileMatterConduit;
import matteroverdrive.common.tile.TileMatterDecomposer;
import matteroverdrive.common.tile.TileMatterRecycler;
import matteroverdrive.common.tile.TileMicrowave;
import matteroverdrive.common.tile.TileChunkloader;
import matteroverdrive.common.tile.TileSolarPanel;
import matteroverdrive.common.tile.TileSpacetimeAccelerator;
import matteroverdrive.common.tile.TileTritaniumCrate;
import matteroverdrive.common.tile.TileTritaniumCrate.CrateColors;
import matteroverdrive.common.tile.matter_network.TileMatterAnalyzer;
import matteroverdrive.common.tile.matter_network.TileMatterNetworkCable;
import matteroverdrive.common.tile.matter_network.TilePatternMonitor;
import matteroverdrive.common.tile.matter_network.TilePatternStorage;
import matteroverdrive.common.tile.matter_network.matter_replicator.TileMatterReplicator;
import matteroverdrive.common.tile.transporter.TileTransporter;
import matteroverdrive.core.registers.BulkRegister;
import matteroverdrive.core.registers.IBulkRegistryObject;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DeferredRegisters {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, References.ID);
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, References.ID);
	public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister
			.create(ForgeRegistries.BLOCK_ENTITY_TYPES, References.ID);
	public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES,
			References.ID);
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, References.ID);
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,
			References.ID);
	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister
			.create(ForgeRegistries.PARTICLE_TYPES, References.ID);

	/* BLOCKS */

	public static final RegistryObject<Block> BLOCK_REGULAR_TRITANIUM_PLATING = registerBlock("tritanium_plating",
			() -> new BlockOverdrive(Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1F, 100F),
					false));
	public static final BulkRegister<Block> BLOCK_COLORED_TRITANIUM_PLATING = bulkBlock(
			color -> registerColoredBlock(((BlockColors) color).id("tritanium_plating_"),
					() -> new BlockColored(
							Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1F, 100F),
							((BlockColors) color).color, false),
					((BlockColors) color).color),
			BlockColors.values());
	public static final BulkRegister<Block> BLOCK_FLOOR_TILE = bulkBlock(
			color -> registerColoredBlock(((BlockColors) color).id("floor_tile_"),
					() -> new BlockColored(
							Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1F, 100F),
							((BlockColors) color).color, false),
					((BlockColors) color).color),
			BlockColors.values());
	public static final BulkRegister<Block> BLOCK_FLOOR_TILES = bulkBlock(
			color -> registerColoredBlock(((BlockColors) color).id("floor_tiles_"),
					() -> new BlockColored(
							Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1F, 100F),
							((BlockColors) color).color, false),
					((BlockColors) color).color),
			BlockColors.values());
	public static final BulkRegister<Block> BLOCK_TRITANIUM_CRATES = bulkBlock(
			crate -> registerBlock(((CrateColors) crate).id(), () -> new BlockTritaniumCrate(
					Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1F, 100F).noOcclusion())),
			TileTritaniumCrate.CrateColors.values());
	public static final RegistryObject<Block> BLOCK_SOLAR_PANEL = registerBlock(TypeMachine.SOLAR_PANEL.id(),
			() -> new BlockMachine<TileSolarPanel>(TileSolarPanel::new, TypeMachine.SOLAR_PANEL,
					DeferredRegisters.TILE_SOLAR_PANEL));
	public static final RegistryObject<Block> BLOCK_MATTER_DECOMPOSER = registerBlock(
			TypeMachine.MATTER_DECOMPOSER.id(),
			() -> new BlockLightableMachine<TileMatterDecomposer>(TileMatterDecomposer::new,
					TypeMachine.MATTER_DECOMPOSER, DeferredRegisters.TILE_MATTER_DECOMPOSER));
	public static final RegistryObject<Block> BLOCK_MATTER_RECYCLER = registerBlock(TypeMachine.MATTER_RECYCLER.id(),
			() -> new BlockLightableMachine<TileMatterRecycler>(TileMatterRecycler::new, TypeMachine.MATTER_RECYCLER,
					DeferredRegisters.TILE_MATTER_RECYCLER));
	public static final RegistryObject<BlockAndroidChargerChild> BLOCK_CHARGER_CHILD = registerBlockNew("charger_child", BlockAndroidChargerChild::new);
	public static final RegistryObject<BlockAndroidChargerParent> BLOCK_CHARGER = registerBlockNew(TypeMachine.CHARGER.id(), BlockAndroidChargerParent::new);

	//public static final RegistryObject<BlockAndroidChargerChild> BLOCK_CHARGER_CHILD = BLOCKS.register("charger_child", BlockAndroidChargerChild::new);
	//public static final RegistryObject<BlockAndroidChargerParent> BLOCK_CHARGER = BLOCKS.register(TypeMachine.CHARGER.id(), BlockAndroidChargerParent::new);
	public static final RegistryObject<Block> BLOCK_MICROWAVE = registerBlock(TypeMachine.MICROWAVE.id(),
			() -> new BlockLightableMachine<TileMicrowave>(TileMicrowave::new, TypeMachine.MICROWAVE,
					DeferredRegisters.TILE_MICROWAVE));
	public static final RegistryObject<Block> BLOCK_INSCRIBER = registerBlock(TypeMachine.INSCRIBER.id(),
			() -> new BlockMachine<TileInscriber>(TileInscriber::new, TypeMachine.INSCRIBER,
					DeferredRegisters.TILE_INSCRIBER));
	public static final BulkRegister<Block> BLOCK_MATTER_CONDUITS = bulkBlock(
			conduit -> registerBlock(((TypeMatterConduit) conduit).id(),
					() -> new BlockMatterConduit((TypeMatterConduit) conduit)),
			TypeMatterConduit.values());
	public static final RegistryObject<Block> BLOCK_TRANSPORTER = registerBlock(TypeMachine.TRANSPORTER.id(),
			() -> new BlockMachine<TileTransporter>(TileTransporter::new, TypeMachine.TRANSPORTER,
					DeferredRegisters.TILE_TRANSPORTER));
	public static final RegistryObject<Block> BLOCK_SPACETIME_ACCELERATOR = registerBlock(
			TypeMachine.SPACETIME_ACCELERATOR.id(),
			() -> new BlockMachine<TileSpacetimeAccelerator>(TileSpacetimeAccelerator::new,
					TypeMachine.SPACETIME_ACCELERATOR, DeferredRegisters.TILE_SPACETIME_ACCELERATOR));
	public static final RegistryObject<Block> BLOCK_INDUSTRIAL_GLASS = registerBlock("industrial_glass",
			() -> new BlockCustomGlass(0.3F, 0.3F));
	public static final RegistryObject<Block> BLOCK_VENT_OPEN = registerBlock("vent_open",
			() -> new BlockOverdrive(Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1F, 100F),
					false));
	public static final RegistryObject<Block> BLOCK_VENT_CLOSED = registerBlock("vent_closed",
			() -> new BlockOverdrive(Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1F, 100F),
					false));
	public static final BulkRegister<Block> BLOCK_MATTER_NETWORK_CABLES = bulkBlock(
			cable -> registerBlock(cable.id(), () -> new BlockMatterNetworkCable((TypeMatterNetworkCable) cable)),
			TypeMatterNetworkCable.values());
	public static final RegistryObject<Block> BLOCK_CHUNKLOADER = registerBlock(
			TypeMachine.CHUNKLOADER.id(),
			() -> new BlockMachine<TileChunkloader>(TileChunkloader::new,
					TypeMachine.CHUNKLOADER, DeferredRegisters.TILE_CHUNKLOADER));
	public static final RegistryObject<Block> BLOCK_MATTER_ANALYZER = registerBlock(
			TypeMachine.MATTER_ANALYZER.id(),
			() -> new BlockLightableMachine<TileMatterAnalyzer>(TileMatterAnalyzer::new, TypeMachine.MATTER_ANALYZER, DeferredRegisters.TILE_MATTER_ANALYZER));
	public static final RegistryObject<Block> BLOCK_PATTERN_STORAGE = registerBlock(
			TypeMachine.PATTERN_STORAGE.id(),
			() -> new BlockMachine<TilePatternStorage>(TilePatternStorage::new, TypeMachine.PATTERN_STORAGE, DeferredRegisters.TILE_PATTERN_STORAGE));
	public static final RegistryObject<Block> BLOCK_PATTERN_MONITOR = registerBlock(
			TypeMachine.PATTERN_MONITOR.id(),
			() -> new BlockVerticalMachine<TilePatternMonitor>(TilePatternMonitor::new, TypeMachine.PATTERN_MONITOR, DeferredRegisters.TILE_PATTERN_MONITOR));
	public static final RegistryObject<Block> BLOCK_MATTER_REPLICATOR = registerBlock(TypeMachine.MATTER_REPLICATOR.id(), 
			() -> new BlockLightableMachine<TileMatterReplicator>(TileMatterReplicator::new, TypeMachine.MATTER_REPLICATOR, DeferredRegisters.TILE_MATTER_REPLICATOR));
	
	/* ITEMS */

	public static final RegistryObject<Item> ITEM_RAW_MATTER_DUST = ITEMS.register("raw_matter_dust",
			() -> new Item(new Item.Properties().tab(References.MAIN)));
	public static final RegistryObject<Item> ITEM_MATTER_DUST = ITEMS.register("matter_dust",
			() -> new Item(new Item.Properties().tab(References.MAIN)));
	public static final RegistryObject<Item> ITEM_BASE_UPGRADE = ITEMS.register("upgrade_base",
			() -> new Item(new Item.Properties().tab(References.MAIN).stacksTo(16)));
	public static final BulkRegister<Item> ITEM_UPGRADES = bulkItem(
			upgrade -> ITEMS.register(((UpgradeType) upgrade).id(), () -> new ItemUpgrade((UpgradeType) upgrade)),
			UpgradeType.values());
	public static final RegistryObject<Item> ITEM_ION_SNIPER = ITEMS.register("ion_sniper",
			() -> new ItemEnergyWeapon(new Item.Properties().tab(References.MAIN).rarity(Rarity.UNCOMMON), 10000, true,
					true, 1000));
	public static final RegistryObject<Item> ITEM_PHASER_RIFLE = ITEMS.register("phaser_rifle",
			() -> new ItemEnergyWeapon(new Item.Properties().tab(References.MAIN).rarity(Rarity.UNCOMMON), 10000, true,
					true, 1000));
	public static final RegistryObject<Item> ITEM_PHASER = ITEMS.register("phaser",
			() -> new ItemEnergyWeapon(new Item.Properties().tab(References.MAIN).rarity(Rarity.UNCOMMON), 10000, true,
					true, 1000));
	public static final RegistryObject<Item> ITEM_PLASMA_SHOTGUN = ITEMS.register("plasma_shotgun",
			() -> new ItemEnergyWeapon(new Item.Properties().tab(References.MAIN).rarity(Rarity.UNCOMMON), 10000, true,
					true, 1000));
	public static final RegistryObject<Item> ITEM_OMNI_TOOL = ITEMS.register("omni_tool",
			() -> new ItemEnergyWeapon(new Item.Properties().tab(References.MAIN).rarity(Rarity.UNCOMMON), 10000, true,
					true, 1000));
	public static final BulkRegister<Item> ITEM_BATTERIES = bulkItem(
			battery -> ITEMS.register(((BatteryType) battery).id(), () -> new ItemBattery((BatteryType) battery)),
			BatteryType.values());
	public static final BulkRegister<Item> ITEM_MATTER_CONTAINERS = bulkItem(
			container -> ITEMS.register(container.id(), () -> new ItemMatterContainer((ContainerType) container)),
			ContainerType.values());
	public static final BulkRegister<Item> ITEM_ISOLINEAR_CIRCUITS = bulkItem(
			circuit -> ITEMS.register(((TypeIsolinearCircuit) circuit).id(),
					() -> new Item(new Item.Properties().tab(References.MAIN))),
			TypeIsolinearCircuit.values());
	public static final RegistryObject<Item> ITEM_TRANSPORTER_FLASHDRIVE = ITEMS.register("transporter_flashdrive",
			() -> new ItemTransporterFlashdrive());
	public static final RegistryObject<Item> ITEM_PATTERN_DRIVE = ITEMS.register("pattern_drive", () -> new ItemPatternDrive());
	public static final RegistryObject<Item> ITEM_MATTER_SCANNER = ITEMS.register("matter_scanner", () -> new ItemMatterScanner());

	public static final RegistryObject<Item> ITEM_TRITANIUM_PLATE = ITEMS.register("tritanium_plate",
			() -> new Item(new Item.Properties().tab(References.MAIN)));
	public static final RegistryObject<Item> ITEM_LEAD_PLATE = ITEMS.register("lead_plate", 
			() -> new Item(new Item.Properties().tab(References.MAIN).stacksTo(TileMatterReplicator.NEEDED_PLATES)));

	/* TILES */

	public static final RegistryObject<BlockEntityType<TileTritaniumCrate>> TILE_TRITANIUM_CRATE = TILES
			.register("tritanium_crate", () -> new BlockEntityType<>(TileTritaniumCrate::new,
					Sets.newHashSet(BLOCK_TRITANIUM_CRATES.<Block>getObjectsAsArray(new Block[0])), null));
	public static final RegistryObject<BlockEntityType<TileSolarPanel>> TILE_SOLAR_PANEL = TILES.register(
			TypeMachine.SOLAR_PANEL.id(),
			() -> new BlockEntityType<>(TileSolarPanel::new, Sets.newHashSet(BLOCK_SOLAR_PANEL.get()), null));
	public static final RegistryObject<BlockEntityType<TileMatterDecomposer>> TILE_MATTER_DECOMPOSER = TILES
			.register(TypeMachine.MATTER_DECOMPOSER.id(), () -> new BlockEntityType<>(TileMatterDecomposer::new,
					Sets.newHashSet(BLOCK_MATTER_DECOMPOSER.get()), null));
	public static final RegistryObject<BlockEntityType<TileMatterRecycler>> TILE_MATTER_RECYCLER = TILES.register(
			TypeMachine.MATTER_RECYCLER.id(),
			() -> new BlockEntityType<>(TileMatterRecycler::new, Sets.newHashSet(BLOCK_MATTER_RECYCLER.get()), null));
	//public static final RegistryObject<BlockEntityType<TileCharger>> OLD_TILE_CHARGER = TILES.register(
	//		TypeMachine.CHARGER.id(),
	//		() -> new BlockEntityType<>(TileCharger::new, Sets.newHashSet(BLOCK_CHARGER.get()), null));
	public static final RegistryObject<BlockEntityType<matteroverdrive.common.tile.TileCharger>> TILE_CHARGER = TILES.register(
					TypeMachine.CHARGER.id(),
					() -> new BlockEntityType<>(matteroverdrive.common.tile.TileCharger::new, Sets.newHashSet(BLOCK_CHARGER.get()), null));

	public static final RegistryObject<BlockEntityType<TileMicrowave>> TILE_MICROWAVE = TILES.register(
			TypeMachine.MICROWAVE.id(),
			() -> new BlockEntityType<>(TileMicrowave::new, Sets.newHashSet(BLOCK_MICROWAVE.get()), null));
	public static final RegistryObject<BlockEntityType<TileInscriber>> TILE_INSCRIBER = TILES.register(
			TypeMachine.INSCRIBER.id(),
			() -> new BlockEntityType<>(TileInscriber::new, Sets.newHashSet(BLOCK_INSCRIBER.get()), null));
	public static final RegistryObject<BlockEntityType<TileMatterConduit>> TILE_MATTER_CONDUIT = TILES
			.register("matter_conduit", () -> new BlockEntityType<>(TileMatterConduit::new,
					Sets.newHashSet(BLOCK_MATTER_CONDUITS.getObjectsAsArray(new Block[0])), null));
	public static final RegistryObject<BlockEntityType<TileTransporter>> TILE_TRANSPORTER = TILES.register(
			TypeMachine.TRANSPORTER.id(),
			() -> new BlockEntityType<>(TileTransporter::new, Sets.newHashSet(BLOCK_TRANSPORTER.get()), null));
	public static final RegistryObject<BlockEntityType<TileSpacetimeAccelerator>> TILE_SPACETIME_ACCELERATOR = TILES
			.register(TypeMachine.SPACETIME_ACCELERATOR.id(), () -> new BlockEntityType<>(TileSpacetimeAccelerator::new,
					Sets.newHashSet(BLOCK_SPACETIME_ACCELERATOR.get()), null));
	public static final RegistryObject<BlockEntityType<TileMatterNetworkCable>> TILE_MATTER_NETWORK_CABLE = TILES
			.register("network_cable", () -> new BlockEntityType<>(TileMatterNetworkCable::new,
					Sets.newHashSet(BLOCK_MATTER_NETWORK_CABLES.getObjectsAsArray(new Block[0])), null));
	public static final RegistryObject<BlockEntityType<TileChunkloader>> TILE_CHUNKLOADER = TILES
			.register(TypeMachine.CHUNKLOADER.id(),
				() -> new BlockEntityType<>(TileChunkloader::new, Sets.newHashSet(BLOCK_CHUNKLOADER.get()), null));
	public static final RegistryObject<BlockEntityType<TileMatterAnalyzer>> TILE_MATTER_ANALYZER = TILES
			.register(TypeMachine.MATTER_ANALYZER.id(),
					() -> new BlockEntityType<>(TileMatterAnalyzer::new, Sets.newHashSet(BLOCK_MATTER_ANALYZER.get()), null));
	public static final RegistryObject<BlockEntityType<TilePatternStorage>> TILE_PATTERN_STORAGE = TILES
			.register(TypeMachine.PATTERN_STORAGE.id(), 
					() -> new BlockEntityType<>(TilePatternStorage::new, Sets.newHashSet(BLOCK_PATTERN_STORAGE.get()), null));
	public static final RegistryObject<BlockEntityType<TilePatternMonitor>> TILE_PATTERN_MONITOR = TILES
			.register(TypeMachine.PATTERN_MONITOR.id(), 
					() -> new BlockEntityType<>(TilePatternMonitor::new, Sets.newHashSet(BLOCK_PATTERN_MONITOR.get()), null));
	public static final RegistryObject<BlockEntityType<TileMatterReplicator>> TILE_MATTER_REPLICATOR = TILES
			.register(TypeMachine.MATTER_REPLICATOR.id(), 
					() -> new BlockEntityType<>(TileMatterReplicator::new, Sets.newHashSet(BLOCK_MATTER_REPLICATOR.get()), null));

	/* MENUS */

	public static final RegistryObject<MenuType<InventoryTritaniumCrate>> MENU_TRITANIUM_CRATE = CONTAINERS
			.register("tritanium_crate", () -> new MenuType<>(InventoryTritaniumCrate::new));
	public static final RegistryObject<MenuType<InventorySolarPanel>> MENU_SOLAR_PANEL = CONTAINERS
			.register(TypeMachine.SOLAR_PANEL.id(), () -> new MenuType<>(InventorySolarPanel::new));
	public static final RegistryObject<MenuType<InventoryMatterDecomposer>> MENU_MATTER_DECOMPOSER = CONTAINERS
			.register(TypeMachine.MATTER_DECOMPOSER.id(), () -> new MenuType<>(InventoryMatterDecomposer::new));
	public static final RegistryObject<MenuType<InventoryMatterRecycler>> MENU_MATTER_RECYCLER = CONTAINERS
			.register(TypeMachine.MATTER_RECYCLER.id(), () -> new MenuType<>(InventoryMatterRecycler::new));
	//public static final RegistryObject<MenuType<InventoryCharger>> MENU_CHARGER = CONTAINERS
	//		.register(TypeMachine.CHARGER.id(), () -> new MenuType<>(InventoryCharger::new));
	public static final RegistryObject<MenuType<InventoryMicrowave>> MENU_MICROWAVE = CONTAINERS
			.register(TypeMachine.MICROWAVE.id(), () -> new MenuType<>(InventoryMicrowave::new));
	public static final RegistryObject<MenuType<InventoryInscriber>> MENU_INSCRIBER = CONTAINERS
			.register(TypeMachine.INSCRIBER.id(), () -> new MenuType<>(InventoryInscriber::new));
	public static final RegistryObject<MenuType<InventoryTransporter>> MENU_TRANSPORTER = CONTAINERS
			.register(TypeMachine.TRANSPORTER.id(), () -> new MenuType<>(InventoryTransporter::new));
	public static final RegistryObject<MenuType<InventorySpacetimeAccelerator>> MENU_SPACETIME_ACCELERATOR = CONTAINERS
			.register(TypeMachine.SPACETIME_ACCELERATOR.id(), () -> new MenuType<>(InventorySpacetimeAccelerator::new));
	public static final RegistryObject<MenuType<InventoryChunkloader>> MENU_CHUNKLOADER = CONTAINERS
			.register(TypeMachine.CHUNKLOADER.id(), () -> new MenuType<>(InventoryChunkloader::new));
	public static final RegistryObject<MenuType<InventoryPatternStorage>> MENU_PATTERN_STORAGE = CONTAINERS
			.register(TypeMachine.PATTERN_STORAGE.id(), () -> new MenuType<>(InventoryPatternStorage::new));
	public static final RegistryObject<MenuType<InventoryMatterReplicator>> MENU_MATTER_REPLICATOR = CONTAINERS
			.register(TypeMachine.MATTER_REPLICATOR.id(), () -> new MenuType<>(InventoryMatterReplicator::new));
	public static final RegistryObject<MenuType<InventoryPatternMonitor>> MENU_PATTERN_MONITOR = CONTAINERS
			.register(TypeMachine.PATTERN_MONITOR.id(), () -> new MenuType<>(InventoryPatternMonitor::new));
	public static final RegistryObject<MenuType<InventoryMatterAnalyzer>> MENU_MATTER_ANALYZER = CONTAINERS
			.register(TypeMachine.MATTER_ANALYZER.id(), () -> new MenuType<>(InventoryMatterAnalyzer::new));

	/* Particles */

	public static final RegistryObject<ParticleOptionReplicator> PARTICLE_REPLICATOR = PARTICLES.register("replicator",
			() -> new ParticleOptionReplicator());
	public static final RegistryObject<ParticleOptionShockwave> PARTICLE_SHOCKWAVE = PARTICLES.register("shockwave",
			() -> new ParticleOptionShockwave());
	public static final RegistryObject<ParticleOptionVent> PARTICLE_VENT = PARTICLES.register("vent", 
			() -> new ParticleOptionVent());

	// Functional Methods

	private static RegistryObject<Block> registerBlock(String name, Supplier<Block> supplier) {
		return registerBlock(name, supplier, new Item.Properties().tab(References.MAIN));
	}

	private static RegistryObject<Block> registerBlock(String name, Supplier<Block> supplier,
			net.minecraft.world.item.Item.Properties properties) {
		RegistryObject<Block> block = BLOCKS.register(name, supplier);
		ITEMS.register(name, () -> new BlockItem(block.get(), properties));
		return block;
	}
	
	private static <T extends Block> RegistryObject<T> registerBlockNew(String name, Supplier<T> supplier) {
		return registerBlockNew(name, supplier, new Item.Properties().tab(References.MAIN));
	}

	private static <T extends Block> RegistryObject<T> registerBlockNew(String name, Supplier<T> supplier,
			net.minecraft.world.item.Item.Properties properties) {
		RegistryObject<T> block = BLOCKS.register(name, supplier);
		ITEMS.register(name, () -> new BlockItem(block.get(), properties));
		return block;
	}

	private static RegistryObject<Block> registerColoredBlock(String name, Supplier<Block> supplier, int color) {
		return registerColoredBlock(name, supplier, new Item.Properties().tab(References.MAIN), color);
	}

	private static RegistryObject<Block> registerColoredBlock(String name, Supplier<Block> supplier,
			net.minecraft.world.item.Item.Properties properties, int color) {
		RegistryObject<Block> block = BLOCKS.register(name, supplier);
		ITEMS.register(name, () -> new BlockItemColored(block.get(), properties, color));
		return block;
	}

	private static BulkRegister<Block> bulkBlock(Function<IBulkRegistryObject, RegistryObject<Block>> factory,
			IBulkRegistryObject[] bulkValues) {
		return new BulkRegister<>(factory, bulkValues);
	}

	private static BulkRegister<Item> bulkItem(Function<IBulkRegistryObject, RegistryObject<Item>> factory,
			IBulkRegistryObject[] bulkValues) {
		return new BulkRegister<>(factory, bulkValues);
	}

}
