package matteroverdrive;

import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.compress.utils.Sets;

import matteroverdrive.common.block.BlockColored;
import matteroverdrive.common.block.BlockLightableMachine;
import matteroverdrive.common.block.BlockMachine;
import matteroverdrive.common.block.BlockMultiSubnode;
import matteroverdrive.common.block.BlockOverdrive;
import matteroverdrive.common.block.BlockTritaniumCrate;
import matteroverdrive.common.block.BlockMachineMultiblock;
import matteroverdrive.common.block.type.BlockColors;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.blockitem.BlockItemColored;
import matteroverdrive.common.inventory.InventoryCharger;
import matteroverdrive.common.inventory.InventoryInscriber;
import matteroverdrive.common.inventory.InventoryMatterDecomposer;
import matteroverdrive.common.inventory.InventoryMatterRecycler;
import matteroverdrive.common.inventory.InventoryMicrowave;
import matteroverdrive.common.inventory.InventorySolarPanel;
import matteroverdrive.common.inventory.InventoryTritaniumCrate;
import matteroverdrive.common.item.ItemUpgrade;
import matteroverdrive.common.item.ItemUpgrade.UpgradeType;
import matteroverdrive.common.item.tools.ItemMatterContainer;
import matteroverdrive.common.item.tools.electric.ItemBattery;
import matteroverdrive.common.item.tools.electric.ItemBattery.BatteryType;
import matteroverdrive.common.item.tools.electric.ItemEnergyWeapon;
import matteroverdrive.common.item.type.TypeIsolinearCircuit;
import matteroverdrive.common.tile.TileCharger;
import matteroverdrive.common.tile.TileInscriber;
import matteroverdrive.common.tile.TileMatterDecomposer;
import matteroverdrive.common.tile.TileMatterRecycler;
import matteroverdrive.common.tile.TileMicrowave;
import matteroverdrive.common.tile.TileSolarPanel;
import matteroverdrive.common.tile.TileTritaniumCrate;
import matteroverdrive.common.tile.TileTritaniumCrate.CrateColors;
import matteroverdrive.common.tile.generic.TileMultiSubnode;
import matteroverdrive.core.registers.BulkRegister;
import matteroverdrive.core.registers.IBulkRegistryObject;
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
			.create(ForgeRegistries.BLOCK_ENTITIES, References.ID);
	public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS,
			References.ID);
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, References.ID);
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES,
			References.ID);

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
	public static final RegistryObject<Block> BLOCK_MULTI_SUBNODE = registerBlock("multisubnode",
			() -> new BlockMultiSubnode());
	public static final RegistryObject<Block> BLOCK_CHARGER = registerBlock(TypeMachine.CHARGER.id(),
			() -> new BlockMachineMultiblock<TileCharger>(TileCharger::new, TypeMachine.CHARGER,
					DeferredRegisters.TILE_CHARGER, BlockMachineMultiblock.CHARGER_NODES));
	public static final RegistryObject<Block> BLOCK_MICROWAVE = registerBlock(TypeMachine.MICROWAVE.id(),
			() -> new BlockLightableMachine<TileMicrowave>(TileMicrowave::new, TypeMachine.MICROWAVE,
					DeferredRegisters.TILE_MICROWAVE));
	public static final RegistryObject<Block> BLOCK_INSCRIBER = registerBlock(TypeMachine.INSCRIBER.id(),
			() -> new BlockMachine<TileInscriber>(TileInscriber::new, TypeMachine.INSCRIBER,
					DeferredRegisters.TILE_INSCRIBER));

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
	public static final RegistryObject<Item> ITEM_MATTER_CONTAINER = ITEMS.register("matter_container",
			() -> new ItemMatterContainer());
	public static final BulkRegister<Item> ITEM_ISOLINEAR_CIRCUITS = bulkItem(
			circuit -> ITEMS.register(((TypeIsolinearCircuit) circuit).id(),
					() -> new Item(new Item.Properties().tab(References.MAIN))),
			TypeIsolinearCircuit.values());

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
	public static final RegistryObject<BlockEntityType<TileMultiSubnode>> TILE_MULTI_SUBNODE = TILES.register(
			"multisubnode",
			() -> new BlockEntityType<>(TileMultiSubnode::new, Sets.newHashSet(BLOCK_MULTI_SUBNODE.get()), null));
	public static final RegistryObject<BlockEntityType<TileCharger>> TILE_CHARGER = TILES.register(
			TypeMachine.CHARGER.id(),
			() -> new BlockEntityType<>(TileCharger::new, Sets.newHashSet(BLOCK_CHARGER.get()), null));
	public static final RegistryObject<BlockEntityType<TileMicrowave>> TILE_MICROWAVE = TILES.register(
			TypeMachine.MICROWAVE.id(),
			() -> new BlockEntityType<>(TileMicrowave::new, Sets.newHashSet(BLOCK_MICROWAVE.get()), null));
	public static final RegistryObject<BlockEntityType<TileInscriber>> TILE_INSCRIBER = TILES.register(
			TypeMachine.INSCRIBER.id(),
			() -> new BlockEntityType<>(TileInscriber::new, Sets.newHashSet(BLOCK_INSCRIBER.get()), null));

	/* MENUS */

	public static final RegistryObject<MenuType<InventoryTritaniumCrate>> MENU_TRITANIUM_CRATE = CONTAINERS
			.register("tritanium_crate", () -> new MenuType<>(InventoryTritaniumCrate::new));
	public static final RegistryObject<MenuType<InventorySolarPanel>> MENU_SOLAR_PANEL = CONTAINERS
			.register(TypeMachine.SOLAR_PANEL.id(), () -> new MenuType<>(InventorySolarPanel::new));
	public static final RegistryObject<MenuType<InventoryMatterDecomposer>> MENU_MATTER_DECOMPOSER = CONTAINERS
			.register(TypeMachine.MATTER_DECOMPOSER.id(), () -> new MenuType<>(InventoryMatterDecomposer::new));
	public static final RegistryObject<MenuType<InventoryMatterRecycler>> MENU_MATTER_RECYCLER = CONTAINERS
			.register(TypeMachine.MATTER_RECYCLER.id(), () -> new MenuType<>(InventoryMatterRecycler::new));
	public static final RegistryObject<MenuType<InventoryCharger>> MENU_CHARGER = CONTAINERS
			.register(TypeMachine.CHARGER.id(), () -> new MenuType<>(InventoryCharger::new));
	public static final RegistryObject<MenuType<InventoryMicrowave>> MENU_MICROWAVE = CONTAINERS
			.register(TypeMachine.MICROWAVE.id(), () -> new MenuType<>(InventoryMicrowave::new));
	public static final RegistryObject<MenuType<InventoryInscriber>> MENU_INSCRIBER = CONTAINERS
			.register(TypeMachine.INSCRIBER.id(), () -> new MenuType<>(InventoryInscriber::new));

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
