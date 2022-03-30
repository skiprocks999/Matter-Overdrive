package matteroverdrive;

import java.util.HashMap;
import java.util.function.Supplier;

import matteroverdrive.common.item.tools.electric.ItemEnergyWeapon;
import matteroverdrive.core.item.IType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;
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
	
	
	public static final RegistryObject<Item> ITEM_IONSNIPER = ITEMS.register("ion_sniper", () -> new ItemEnergyWeapon(new Item.Properties().tab(References.MAIN).rarity(Rarity.UNCOMMON), 10000, true, true, 1000));
	public static final RegistryObject<Item> ITEM_PHASERRIFLE = ITEMS.register("phaser_rifle", () -> new ItemEnergyWeapon(new Item.Properties().tab(References.MAIN).rarity(Rarity.UNCOMMON), 10000, true, true, 1000));
	public static final RegistryObject<Item> ITEM_PHASER = ITEMS.register("phaser", () -> new ItemEnergyWeapon(new Item.Properties().tab(References.MAIN).rarity(Rarity.UNCOMMON), 10000, true, true, 1000));
	public static final RegistryObject<Item> ITEM_PLASMASHOTGUN = ITEMS.register("plasma_shotgun", () -> new ItemEnergyWeapon(new Item.Properties().tab(References.MAIN).rarity(Rarity.UNCOMMON), 10000, true, true, 1000));
	public static final RegistryObject<Item> OMNI_TOOL = ITEMS.register("omni_tool", () -> new ItemEnergyWeapon(new Item.Properties().tab(References.MAIN).rarity(Rarity.UNCOMMON), 10000, true, true, 1000));
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static void registerTypedBlockItem(IType[] array) {
		for (IType subtype : array) {
			ITEMS.register(subtype.regName(), supplier(new BlockItem(TYPED_BLOCKS.get(subtype), new Item.Properties().tab(References.MAIN)), subtype));
		}
	}

	private static void registerTypedItem(IType[] array) {
		for (IType subtype : array) {
			ITEMS.register(subtype.regName(), supplier(new Item(new Item.Properties().tab(References.MAIN)), subtype));
		}
	}
	
	private static <T extends IForgeRegistryEntry<T>> Supplier<? extends T> supplier(T entry) {
		return () -> entry;
	}
	
	private static <T extends IForgeRegistryEntry<T>> Supplier<? extends T> supplier(T entry, IType en) {
		if (entry instanceof Block bl) {
			TYPED_BLOCKS.put(en, bl);
		} else if (entry instanceof Item it) {
			TYPED_ITEMS.put(en, it);
		}
		return supplier(entry);
	}
	
}
