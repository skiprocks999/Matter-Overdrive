package matteroverdrive;

import java.util.function.Supplier;

import org.apache.commons.compress.utils.Sets;

import matteroverdrive.common.block.BlockColored;
import matteroverdrive.common.block.BlockLightableMachine;
import matteroverdrive.common.block.BlockMachine;
import matteroverdrive.common.block.BlockOverdrive;
import matteroverdrive.common.block.BlockTritaniumCrate;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.block.utils.BlockColors;
import matteroverdrive.common.blockitem.BlockItemColored;
import matteroverdrive.common.inventory.InventoryMatterDecomposer;
import matteroverdrive.common.inventory.InventorySolarPanel;
import matteroverdrive.common.inventory.InventoryTritaniumCrate;
import matteroverdrive.common.item.tools.electric.ItemEnergyWeapon;
import matteroverdrive.common.tile.TileMatterDecomposer;
import matteroverdrive.common.tile.TileSolarPanel;
import matteroverdrive.common.tile.TileTritaniumCrate;
import matteroverdrive.core.registers.BulkRegistryObject;
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

	public static final RegistryObject<Block> REGULAR_TRITANIUM_PLATING = registerBlock("tritanium_plating",
			() -> new BlockOverdrive(Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1F, 100F),
					false));
	public static final BulkRegistryObject<Block> COLORED_TRITANIUM_PLATING = new BulkRegistryObject<>(
			color -> registerColoredBlock("tritanium_plating" + "_" + color.toString().toLowerCase(),
					() -> new BlockColored(
							Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1F, 100F),
							((BlockColors) color).color, false),
					((BlockColors) color).color),
			BlockColors.values());
	public static final BulkRegistryObject<Block> FLOOR_TILE = new BulkRegistryObject<>(
			color -> registerColoredBlock("floor_tile" + "_" + color.toString().toLowerCase(),
					() -> new BlockColored(
							Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1F, 100F),
							((BlockColors) color).color, false),
					((BlockColors) color).color),
			BlockColors.values());
	public static final BulkRegistryObject<Block> FLOOR_TILES = new BulkRegistryObject<>(
			color -> registerColoredBlock("floor_tiles" + "_" + color.toString().toLowerCase(),
					() -> new BlockColored(
							Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1F, 100F),
							((BlockColors) color).color, false),
					((BlockColors) color).color),
			BlockColors.values());
	public static final BulkRegistryObject<Block> TRITANIUM_CRATES = new BulkRegistryObject<>(crate -> registerBlock(
			"tritanium_crate" + "_" + crate.toString().toLowerCase(),
			() -> new BlockTritaniumCrate(
					Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1F, 100F).noOcclusion())),
			TileTritaniumCrate.CrateColors.values());
	public static final RegistryObject<Block> BLOCK_SOLAR_PANEL = registerBlock(TypeMachine.solar_panel.toString(),
			() -> new BlockMachine<TileSolarPanel>(TileSolarPanel::new, TypeMachine.solar_panel,
					DeferredRegisters.TILE_SOLAR_PANEL));
	public static final RegistryObject<Block> BLOCK_MATTER_DECOMPOSER = registerBlock(
			TypeMachine.matter_decomposer.toString(),
			() -> new BlockLightableMachine<TileMatterDecomposer>(TileMatterDecomposer::new,
					TypeMachine.matter_decomposer, DeferredRegisters.TILE_MATTER_DECOMPOSER));

	/* ITEMS */

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

	/* TILES */

	public static final RegistryObject<BlockEntityType<TileTritaniumCrate>> TILE_TRITANIUM_CRATE = TILES
			.register("tritanium_crate", () -> new BlockEntityType<>(TileTritaniumCrate::new,
					Sets.newHashSet(TRITANIUM_CRATES.<Block>getObjectsAsArray(new Block[0])), null));
	public static final RegistryObject<BlockEntityType<TileSolarPanel>> TILE_SOLAR_PANEL = TILES.register(
			TypeMachine.solar_panel.toString(),
			() -> new BlockEntityType<>(TileSolarPanel::new, Sets.newHashSet(BLOCK_SOLAR_PANEL.get()), null));
	public static final RegistryObject<BlockEntityType<TileMatterDecomposer>> TILE_MATTER_DECOMPOSER = TILES
			.register(TypeMachine.matter_decomposer.toString(), () -> new BlockEntityType<>(TileMatterDecomposer::new,
					Sets.newHashSet(BLOCK_MATTER_DECOMPOSER.get()), null));

	/* MENUS */

	public static final RegistryObject<MenuType<InventoryTritaniumCrate>> MENU_TRITANIUM_CRATE = CONTAINERS
			.register("tritanium_crate", () -> new MenuType<>(InventoryTritaniumCrate::new));
	public static final RegistryObject<MenuType<InventorySolarPanel>> MENU_SOLAR_PANEL = CONTAINERS
			.register("solar_panel", () -> new MenuType<>(InventorySolarPanel::new));
	public static final RegistryObject<MenuType<InventoryMatterDecomposer>> MENU_MATTER_DECOMPOSER = CONTAINERS
			.register("matter_decomposer", () -> new MenuType<>(InventoryMatterDecomposer::new));

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

}
