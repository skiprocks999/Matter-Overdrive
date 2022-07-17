package matteroverdrive.core.datagen.client;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.References;
import matteroverdrive.common.block.type.BlockColors;
import matteroverdrive.common.item.ItemUpgrade.UpgradeType;
import matteroverdrive.common.item.type.TypeIsolinearCircuit;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class OverdriveItemModelsProvider extends ItemModelProvider {

	public OverdriveItemModelsProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, References.ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		withExistingParent(blockPath(DeferredRegisters.BLOCK_REGULAR_TRITANIUM_PLATING),
				modLoc("block/tritanium_plating"));
		for (BlockColors color : BlockColors.values()) {
			withExistingParent(blockPath(DeferredRegisters.BLOCK_COLORED_TRITANIUM_PLATING.get(color)),
					modLoc("block/tritanium_plating_colorless"));
			withExistingParent(blockPath(DeferredRegisters.BLOCK_FLOOR_TILE.get(color)),
					modLoc("block/floor_tile_colorless"));
			withExistingParent(blockPath(DeferredRegisters.BLOCK_FLOOR_TILES.get(color)),
					modLoc("block/floor_tiles_colorless"));
		}
		modSlab("solar_panel", "block/base", "block/base", "block/solar_panel");
		withExistingParent(blockPath(DeferredRegisters.BLOCK_MATTER_DECOMPOSER), modLoc("block/matter_decomposer"));
		withExistingParent(blockPath(DeferredRegisters.BLOCK_MATTER_RECYCLER), modLoc("block/matter_recycler"));
		simpleBlock(DeferredRegisters.BLOCK_CHARGER_CHILD, "block/charger_child");
		withExistingParent(blockPath(DeferredRegisters.BLOCK_TRANSPORTER), modLoc("block/transporter"));
		withExistingParent(blockPath(DeferredRegisters.BLOCK_SPACETIME_ACCELERATOR), modLoc("block/spacetime_accelerator"));

		simpleItem(DeferredRegisters.ITEM_RAW_MATTER_DUST, "item/raw_matter_dust");
		simpleItem(DeferredRegisters.ITEM_MATTER_DUST, "item/matter_dust");
		simpleItem(DeferredRegisters.ITEM_TRITANIUM_PLATE, "item/tritanium_plate");
		simpleItem(DeferredRegisters.ITEM_BASE_UPGRADE, "item/upgrade/upgrade_base");
		for (UpgradeType type : UpgradeType.values()) {
			simpleItem(DeferredRegisters.ITEM_UPGRADES.get(type),
					"item/upgrade/upgrade_" + type.toString().toLowerCase());
		}
		for (TypeIsolinearCircuit circuit : TypeIsolinearCircuit.values()) {
			simpleItem(DeferredRegisters.ITEM_ISOLINEAR_CIRCUITS.get(circuit),
					"item/isolinear_circuit/" + circuit.id());
		}

		withExistingParent(blockPath(DeferredRegisters.BLOCK_INDUSTRIAL_GLASS),
				modLoc("block/industrial_glass"));

		withExistingParent(blockPath(DeferredRegisters.BLOCK_VENT_OPEN),
				modLoc("block/vent_open"));

		withExistingParent(blockPath(DeferredRegisters.BLOCK_VENT_CLOSED),
				modLoc("block/vent_closed"));
	}

	private String blockPath(RegistryObject<Block> block) {
		return block.get().getRegistryName().getPath();
	}

	private void modSlab(String name, String side, String bottom, String top) {
		slab(name, new ResourceLocation(References.ID, side), new ResourceLocation(References.ID, bottom),
				new ResourceLocation(References.ID, top));
	}

	private void simpleItem(RegistryObject<Item> item, String textureLoc) {
		singleTexture(item.get().getRegistryName().getPath(), new ResourceLocation("item/generated"), "layer0",
				new ResourceLocation(References.ID, textureLoc));
	}

	private void simpleBlock(RegistryObject<Block> block, String textureLoc) {
		cubeAll(block.get().getRegistryName().getPath(), new ResourceLocation(References.ID, textureLoc));
	}

}
