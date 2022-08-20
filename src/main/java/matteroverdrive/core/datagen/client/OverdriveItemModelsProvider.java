package matteroverdrive.core.datagen.client;

import matteroverdrive.References;
import matteroverdrive.common.block.OverdriveBlockColors;
import matteroverdrive.common.item.ItemUpgrade.UpgradeType;
import matteroverdrive.common.item.type.TypeIsolinearCircuit;
import matteroverdrive.registry.BlockRegistry;
import matteroverdrive.registry.ItemRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class OverdriveItemModelsProvider extends ItemModelProvider {

	public OverdriveItemModelsProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, References.ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		withExistingParent(blockPath(BlockRegistry.BLOCK_REGULAR_TRITANIUM_PLATING), modLoc("block/tritanium_plating"));
		for (OverdriveBlockColors color : OverdriveBlockColors.values()) {
			withExistingParent(blockPath(BlockRegistry.BLOCK_COLORED_TRITANIUM_PLATING.get(color)),
					modLoc("block/tritanium_plating_colorless"));
			withExistingParent(blockPath(BlockRegistry.BLOCK_FLOOR_TILE.get(color)),
					modLoc("block/floor_tile_colorless"));
			withExistingParent(blockPath(BlockRegistry.BLOCK_FLOOR_TILES.get(color)),
					modLoc("block/floor_tiles_colorless"));
		}
		modSlab("solar_panel", "block/base", "block/base", "block/solar_panel");
		withExistingParent(blockPath(BlockRegistry.BLOCK_MATTER_DECOMPOSER), modLoc("block/matter_decomposer"));
		withExistingParent(blockPath(BlockRegistry.BLOCK_MATTER_RECYCLER), modLoc("block/matter_recycler"));
		simpleBlock(BlockRegistry.BLOCK_CHARGER_CHILD, "block/charger_child");
		withExistingParent(blockPath(BlockRegistry.BLOCK_TRANSPORTER), modLoc("block/transporter"));
		withExistingParent(blockPath(BlockRegistry.BLOCK_SPACETIME_ACCELERATOR), modLoc("block/spacetime_accelerator"));

		withExistingParent(blockPath(BlockRegistry.BLOCK_INDUSTRIAL_GLASS), modLoc("block/industrial_glass"));

		withExistingParent(blockPath(BlockRegistry.BLOCK_VENT_OPEN), modLoc("block/vent_open"));

		withExistingParent(blockPath(BlockRegistry.BLOCK_VENT_CLOSED), modLoc("block/vent_closed"));

		simpleItem(ItemRegistry.ITEM_RAW_MATTER_DUST, "item/raw_matter_dust");
		simpleItem(ItemRegistry.ITEM_MATTER_DUST, "item/matter_dust");
		simpleItem(ItemRegistry.ITEM_TRITANIUM_PLATE, "item/tritanium_plate");
		simpleItem(ItemRegistry.ITEM_BASE_UPGRADE, "item/upgrade/upgrade_base");
		for (UpgradeType type : UpgradeType.values()) {
			simpleItem(ItemRegistry.ITEM_UPGRADES.get(type), "item/upgrade/upgrade_" + type.toString().toLowerCase());
		}
		for (TypeIsolinearCircuit circuit : TypeIsolinearCircuit.values()) {
			simpleItem(ItemRegistry.ITEM_ISOLINEAR_CIRCUITS.get(circuit), "item/isolinear_circuit/" + circuit.id());
		}

		simpleItem(ItemRegistry.ITEM_LEAD_PLATE, "item/lead_plate");
	}

	private String blockPath(RegistryObject<Block> block) {
		return ForgeRegistries.BLOCKS.getKey(block.get()).getPath();
	}

	private void modSlab(String name, String side, String bottom, String top) {
		slab(name, new ResourceLocation(References.ID, side), new ResourceLocation(References.ID, bottom),
				new ResourceLocation(References.ID, top));
	}

	private void simpleItem(RegistryObject<Item> item, String textureLoc) {
		singleTexture(ForgeRegistries.ITEMS.getKey(item.get()).getPath(), new ResourceLocation("item/generated"),
				"layer0", new ResourceLocation(References.ID, textureLoc));
	}

	private void simpleBlock(RegistryObject<Block> block, String textureLoc) {
		cubeAll(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), new ResourceLocation(References.ID, textureLoc));
	}

}
