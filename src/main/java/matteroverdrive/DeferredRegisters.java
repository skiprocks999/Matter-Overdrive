package matteroverdrive;

import java.util.HashMap;

import matteroverdrive.common.block.BlockColored;
import matteroverdrive.common.block.utils.BlockColors;
import matteroverdrive.common.item.tools.electric.ItemEnergyWeapon;
import matteroverdrive.core.item.IType;
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
	public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, References.ID);
	public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, References.ID);
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, References.ID);
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, References.ID);

	public static final HashMap<IType, Item> TYPED_ITEMS = new HashMap<>();
	public static final HashMap<IType, Block> TYPED_BLOCKS = new HashMap<>();
	
	public static final HashMap<String, Block> REGISTERED_BLOCKS = new HashMap<>();
	public static final HashMap<String, Item> REGISTERED_ITEMS = new HashMap<>();
	
	public static void init() {
		
		registerBlock("tritainium_plating", new Block(Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1F, 100F)));
		registerColoredBlock("tritainium_plating", Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1F, 100F));
	
		registerItem("tritanium_plating", new BlockItem(REGISTERED_BLOCKS.get("tritainiumplating"), new Item.Properties().tab(References.MAIN)));
		registerColoredBlockItem("tritainium_plating");
	}
	
	public static final RegistryObject<Item> ITEM_IONSNIPER = ITEMS.register("ion_sniper", () -> new ItemEnergyWeapon(new Item.Properties().tab(References.MAIN).rarity(Rarity.UNCOMMON), 10000, true, true, 1000));
	public static final RegistryObject<Item> ITEM_PHASERRIFLE = ITEMS.register("phaser_rifle", () -> new ItemEnergyWeapon(new Item.Properties().tab(References.MAIN).rarity(Rarity.UNCOMMON), 10000, true, true, 1000));
	public static final RegistryObject<Item> ITEM_PHASER = ITEMS.register("phaser", () -> new ItemEnergyWeapon(new Item.Properties().tab(References.MAIN).rarity(Rarity.UNCOMMON), 10000, true, true, 1000));
	public static final RegistryObject<Item> ITEM_PLASMASHOTGUN = ITEMS.register("plasma_shotgun", () -> new ItemEnergyWeapon(new Item.Properties().tab(References.MAIN).rarity(Rarity.UNCOMMON), 10000, true, true, 1000));
	public static final RegistryObject<Item> OMNI_TOOL = ITEMS.register("omni_tool", () -> new ItemEnergyWeapon(new Item.Properties().tab(References.MAIN).rarity(Rarity.UNCOMMON), 10000, true, true, 1000));
	
	
	
	private static void registerColoredBlock(String baseKey, Properties properties) {
		for(BlockColors color : BlockColors.values()) {
			registerBlock(baseKey + "_" + color.toString().toLowerCase(), new BlockColored(properties, color.color));
		}
	}
	
	private static void registerColoredBlockItem(String baseKey) {
		for(BlockColors color : BlockColors.values()) {
			String name = baseKey + "_" + color.toString().toLowerCase();
			registerItem(name, new BlockItem(REGISTERED_BLOCKS.get(name), new Item.Properties().tab(References.MAIN)));
		}
	}
	
	private static void registerBlock(String key, Block block) {
		BLOCKS.register(key, () -> block);
		REGISTERED_BLOCKS.put(key, block);
	}
	
	private static void registerItem(String key, Item item) {
		ITEMS.register(key, () -> item);
		REGISTERED_ITEMS.put(key, item);
	}
}
