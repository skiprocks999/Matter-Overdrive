package matteroverdrive;

import java.util.function.Supplier;

import matteroverdrive.common.block.utils.ColoredRegistryObject;
import matteroverdrive.common.item.tools.electric.ItemEnergyWeapon;
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

	public static final RegistryObject<Block> TRITANIUM_PLATING = registerBlock("tritanium_plating", () -> new Block(Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1F, 100F)));
	public static final ColoredRegistryObject<Block> COLORED_TRITANIUM_PLATING = new ColoredRegistryObject<>(color -> registerBlock("tritanium_plating" + "_" + color.toString().toLowerCase(), () -> new Block(Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1F, 100F))));



	public static final RegistryObject<Item> ITEM_IONSNIPER = ITEMS.register("ion_sniper", () -> new ItemEnergyWeapon(new Item.Properties().tab(References.MAIN).rarity(Rarity.UNCOMMON), 10000, true, true, 1000));
	public static final RegistryObject<Item> ITEM_PHASERRIFLE = ITEMS.register("phaser_rifle", () -> new ItemEnergyWeapon(new Item.Properties().tab(References.MAIN).rarity(Rarity.UNCOMMON), 10000, true, true, 1000));
	public static final RegistryObject<Item> ITEM_PHASER = ITEMS.register("phaser", () -> new ItemEnergyWeapon(new Item.Properties().tab(References.MAIN).rarity(Rarity.UNCOMMON), 10000, true, true, 1000));
	public static final RegistryObject<Item> ITEM_PLASMASHOTGUN = ITEMS.register("plasma_shotgun", () -> new ItemEnergyWeapon(new Item.Properties().tab(References.MAIN).rarity(Rarity.UNCOMMON), 10000, true, true, 1000));
	public static final RegistryObject<Item> OMNI_TOOL = ITEMS.register("omni_tool", () -> new ItemEnergyWeapon(new Item.Properties().tab(References.MAIN).rarity(Rarity.UNCOMMON), 10000, true, true, 1000));

	private static RegistryObject<Block> registerBlock(String name, Supplier<Block> supplier) {
		return registerBlock(name, supplier, new Item.Properties().tab(References.MAIN));
	}

	private static RegistryObject<Block> registerBlock(String name, Supplier<Block> supplier, net.minecraft.world.item.Item.Properties properties) {
		RegistryObject<Block> block = BLOCKS.register(name, supplier);
		ITEMS.register(name, () -> new BlockItem(block.get(), properties));
		return block;
	}

}
