package matteroverdrive.core.datagen.client;

import matteroverdrive.References;
import matteroverdrive.client.ClientRegister;
import matteroverdrive.common.block.type.TypeMatterConduit;
import matteroverdrive.common.block.type.TypeMatterNetworkCable;
import matteroverdrive.common.item.ItemUpgrade.UpgradeType;
import matteroverdrive.common.item.type.TypeIsolinearCircuit;
import matteroverdrive.registry.BlockRegistry;
import matteroverdrive.registry.ItemRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
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
		withExistingParent(blockPath(BlockRegistry.BLOCK_CHARGER), blockLoc("charger_item"));
		withExistingParent(blockPath(BlockRegistry.BLOCK_MATTER_CONDUITS.get(TypeMatterConduit.HEAVY)), blockLoc("cable/matter_conduit_heavy_none_seamless_ns"));
		withExistingParent(blockPath(BlockRegistry.BLOCK_MATTER_CONDUITS.get(TypeMatterConduit.REGULAR)), blockLoc("cable/matter_conduit_regular_none_seamless_ns"));
		withExistingParent(blockPath(BlockRegistry.BLOCK_MATTER_NETWORK_CABLES.get(TypeMatterNetworkCable.REGULAR)), blockLoc("cable/network_cable_regular_none_seamless_ns"));
		
		layeredItem(ItemRegistry.ITEM_RAW_MATTER_DUST, Parent.GENERATED, itemLoc("raw_matter_dust"));
		layeredItem(ItemRegistry.ITEM_RAW_MATTER_DUST, Parent.GENERATED, itemLoc("raw_matter_dust"));
		layeredItem(ItemRegistry.ITEM_TRITANIUM_PLATE, Parent.GENERATED, itemLoc("tritanium_plate"));
		layeredItem(ItemRegistry.ITEM_BASE_UPGRADE, Parent.GENERATED, itemLoc("upgrade/upgrade_base"));
		for (UpgradeType type : UpgradeType.values()) {
			layeredItem(ItemRegistry.ITEM_UPGRADES.get(type), Parent.GENERATED, itemLoc("upgrade/upgrade_" + type.toString().toLowerCase()));
		}
		for (TypeIsolinearCircuit circuit : TypeIsolinearCircuit.values()) {
			layeredItem(ItemRegistry.ITEM_ISOLINEAR_CIRCUITS.get(circuit), Parent.GENERATED, itemLoc("isolinear_circuit/" + circuit.id()));
		}
		layeredItem(ItemRegistry.ITEM_LEAD_PLATE, Parent.GENERATED, itemLoc("lead_plate"));
		layeredItem(ItemRegistry.ITEM_PATTERN_DRIVE, Parent.GENERATED, itemLoc("pattern_drive/pattern_drive_base"),
				itemLoc("pattern_drive/bottom_light"), itemLoc("pattern_drive/middle_light"), itemLoc("pattern_drive/left_light"));
		
		toggleableItem(ItemRegistry.ITEM_MATTER_SCANNER, "_on", Parent.GENERATED, Parent.GENERATED, new ResourceLocation[] {
				itemLoc("matter_scanner/matter_scanner_off")
		}, new ResourceLocation[] {
				itemLoc("matter_scanner/matter_scanner_on")
				});
		
	}
	
	private void layeredItem(RegistryObject<Item> item, Parent parent, ResourceLocation...textures) {
		layeredItem(name(item), parent, textures);
	}
	
	private void layeredItem(String name, Parent parent, ResourceLocation...textures) {
		layeredBuilder(name, parent, textures);
	}
	
	private void toggleableItem(RegistryObject<Item> item, String toggle, Parent parentOff, Parent parentOn, ResourceLocation[] offText, ResourceLocation[] onText) {
		toggleableItem(name(item), toggle, parentOff, parentOn, offText, onText);
	}
	
	private void toggleableItem(String name, String toggle, Parent parentOff, Parent parentOn, ResourceLocation[] offText, ResourceLocation[] onText) {
		ItemModelBuilder off = layeredBuilder(name, parentOff, offText);
		ItemModelBuilder on = layeredBuilder(name + toggle, parentOn, onText);
		off.override().predicate(ClientRegister.CHARGE, 1.0F).model(on).end();
	}
	
	private ItemModelBuilder layeredBuilder(String name, Parent parent, ResourceLocation...textures) {
		if(textures == null || textures.length == 0) {
			throw new UnsupportedOperationException("You need to provide at least one texture");
		}
		ItemModelBuilder builder = withExistingParent(name, parent.loc());
		int counter = 0;
		for(ResourceLocation location : textures) {
			builder.texture("layer" + counter, location);
			counter++;
		}
		return builder;
	}

	private String blockPath(RegistryObject<Block> block) {
		return ForgeRegistries.BLOCKS.getKey(block.get()).getPath();
	}
	
	private ResourceLocation itemLoc(String texture) {
		return modLoc("item/" + texture);
	}
	
	private ResourceLocation blockLoc(String texture) {
		return modLoc("block/" + texture);
	}
	
	private String name(RegistryObject<Item> item) {
		return ForgeRegistries.ITEMS.getKey(item.get()).getPath();
	}
	
	private static enum Parent {
		
		GENERATED(true);
		
		private final boolean isVanilla;
		
		private Parent(boolean isVanilla) {
			this.isVanilla = isVanilla;
		}
		
		public ResourceLocation loc() {
			return isVanilla ? new ResourceLocation(toString().toLowerCase()) : new ResourceLocation(References.ID, toString().toLowerCase());
		}
	}
	
}
