package matteroverdrive.core.datagen.client;

import matteroverdrive.References;
import matteroverdrive.client.ClientRegister;
import matteroverdrive.common.block.type.TypeMatterNetworkCable;
import matteroverdrive.common.item.ItemUpgrade.UpgradeType;
import matteroverdrive.common.item.tools.ItemMatterContainer;
import matteroverdrive.common.item.tools.ItemMatterContainer.ContainerType;
import matteroverdrive.common.item.tools.electric.ItemBattery;
import matteroverdrive.common.item.tools.electric.ItemBattery.BatteryType;
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

	public static final int BATTERY_MODEL_COUNT = 6;
	public static final int MATTER_CONTAINER_MODEL_COUNT = 9;
	
	public OverdriveItemModelsProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, References.ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		withExistingParent(blockPath(BlockRegistry.BLOCK_CHARGER), blockLoc("charger_item"));
		
		layeredItem(ItemRegistry.ITEM_MATTER_DUST, Parent.GENERATED, itemLoc("matter_dust"));
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
		
		toggleableItem(ItemRegistry.ITEM_TRANSPORTER_FLASHDRIVE, "_stored", Parent.GENERATED, Parent.GENERATED, new ResourceLocation[] {
				itemLoc("flashdrive/flashdrive_transporter_empty")
		}, new ResourceLocation[] {
				itemLoc("flashdrive/flashdrive_transporter_stored")
				});
		
		generateBatteries();
		generateMatterContainers();
		
	}
	
	private void generateBatteries() {
		ResourceLocation batteryBase = itemLoc("battery/battery");
		String battBarBase = "battery/battery_overlay";
		ItemModelBuilder[] batteries = new ItemModelBuilder[BATTERY_MODEL_COUNT];
		batteries[0] = layeredBuilder("item/battery/battery0", Parent.GENERATED, batteryBase);
		for(int i = 1; i < BATTERY_MODEL_COUNT; i++) {
			batteries[i] = layeredBuilder("item/battery/battery" + i, Parent.GENERATED, batteryBase, itemLoc(battBarBase + (i - 1)));
		}
		for(RegistryObject<Item> battery : ItemRegistry.ITEM_BATTERIES.getAll()) {
			if(((ItemBattery)battery.get()).type == BatteryType.CREATIVE) {
				withExistingParent(name(battery), Parent.CREATIVE_BATTERY.loc());
			} else {
				ItemModelBuilder bat = withExistingParent(name(battery), Parent.BATTERY.loc());
				for(int i = 1; i < BATTERY_MODEL_COUNT; i++) {
					bat = bat.override().model(batteries[i]).predicate(ClientRegister.CHARGE, (float)i).end();
				}
			}
		}
	}
	
	private void generateMatterContainers() {
		ResourceLocation containerBase = itemLoc("matter_container/container");
		ResourceLocation containerStripe = itemLoc("matter_container/container_bottom_overlay");
		String containerBarBase = "matter_container/container_overlay";
		ItemModelBuilder[] matterContainers = new ItemModelBuilder[MATTER_CONTAINER_MODEL_COUNT];
		matterContainers[0] = layeredBuilder("item/matter_container/matter_container0", Parent.GENERATED, containerBase, containerStripe);
		for(int i = 1; i < MATTER_CONTAINER_MODEL_COUNT; i++) {
			matterContainers[i] = layeredBuilder("item/matter_container/matter_container" + i, Parent.GENERATED, containerBase, containerStripe, itemLoc(containerBarBase + (i - 1)));
		}
		for(RegistryObject<Item> container : ItemRegistry.ITEM_MATTER_CONTAINERS.getAll()) {
			if(((ItemMatterContainer)container.get()).container == ContainerType.CREATIVE) {
				withExistingParent(name(container), Parent.CREATIVE_MATTER_CONTAINER.loc());
			} else {
				ItemModelBuilder bat = withExistingParent(name(container), Parent.MATTER_CONTAINER.loc());
				for(int i = 1; i < MATTER_CONTAINER_MODEL_COUNT; i++) {
					bat = bat.override().model(matterContainers[i]).predicate(ClientRegister.CHARGE, (float)i).end();
				}
			}
		}
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
		
		GENERATED(true), BATTERY("item/battery/battery0"), CREATIVE_BATTERY("item/battery/battery5"),
		MATTER_CONTAINER("item/matter_container/matter_container0"), CREATIVE_MATTER_CONTAINER("item/matter_container/matter_container8");
		
		private final boolean isVanilla;
		private final String loc;
		
		private Parent(boolean isVanilla) {
			this.isVanilla = isVanilla;
			loc = "";
		}
		
		private Parent(String loc) {
			isVanilla = false;
			this.loc = loc;
		}
		
		public ResourceLocation loc() {
			return isVanilla ? new ResourceLocation(toString().toLowerCase()) : new ResourceLocation(References.ID, loc);
		}
	}
	
}
