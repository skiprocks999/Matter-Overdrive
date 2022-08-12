package matteroverdrive.registry;

import matteroverdrive.References;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryCharger;
import matteroverdrive.common.inventory.InventoryChunkloader;
import matteroverdrive.common.inventory.InventoryInscriber;
import matteroverdrive.common.inventory.InventoryMatterAnalyzer;
import matteroverdrive.common.inventory.InventoryMatterDecomposer;
import matteroverdrive.common.inventory.InventoryMatterRecycler;
import matteroverdrive.common.inventory.InventoryMatterReplicator;
import matteroverdrive.common.inventory.InventoryMicrowave;
import matteroverdrive.common.inventory.InventoryPatternMonitor;
import matteroverdrive.common.inventory.InventoryPatternStorage;
import matteroverdrive.common.inventory.InventorySolarPanel;
import matteroverdrive.common.inventory.InventorySpacetimeAccelerator;
import matteroverdrive.common.inventory.InventoryTransporter;
import matteroverdrive.common.inventory.InventoryTritaniumCrate;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MenuRegistry {

	public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES,
			References.ID);
	
	public static final RegistryObject<MenuType<InventoryTritaniumCrate>> MENU_TRITANIUM_CRATE = MENUS
			.register("tritanium_crate", () -> new MenuType<>(InventoryTritaniumCrate::new));
	public static final RegistryObject<MenuType<InventorySolarPanel>> MENU_SOLAR_PANEL = MENUS
			.register(TypeMachine.SOLAR_PANEL.id(), () -> new MenuType<>(InventorySolarPanel::new));
	public static final RegistryObject<MenuType<InventoryMatterDecomposer>> MENU_MATTER_DECOMPOSER = MENUS
			.register(TypeMachine.MATTER_DECOMPOSER.id(), () -> new MenuType<>(InventoryMatterDecomposer::new));
	public static final RegistryObject<MenuType<InventoryMatterRecycler>> MENU_MATTER_RECYCLER = MENUS
			.register(TypeMachine.MATTER_RECYCLER.id(), () -> new MenuType<>(InventoryMatterRecycler::new));
	public static final RegistryObject<MenuType<InventoryCharger>> MENU_CHARGER = MENUS
			.register(TypeMachine.CHARGER.id(), () -> new MenuType<>(InventoryCharger::new));
	public static final RegistryObject<MenuType<InventoryMicrowave>> MENU_MICROWAVE = MENUS
			.register(TypeMachine.MICROWAVE.id(), () -> new MenuType<>(InventoryMicrowave::new));
	public static final RegistryObject<MenuType<InventoryInscriber>> MENU_INSCRIBER = MENUS
			.register(TypeMachine.INSCRIBER.id(), () -> new MenuType<>(InventoryInscriber::new));
	public static final RegistryObject<MenuType<InventoryTransporter>> MENU_TRANSPORTER = MENUS
			.register(TypeMachine.TRANSPORTER.id(), () -> new MenuType<>(InventoryTransporter::new));
	public static final RegistryObject<MenuType<InventorySpacetimeAccelerator>> MENU_SPACETIME_ACCELERATOR = MENUS
			.register(TypeMachine.SPACETIME_ACCELERATOR.id(), () -> new MenuType<>(InventorySpacetimeAccelerator::new));
	public static final RegistryObject<MenuType<InventoryChunkloader>> MENU_CHUNKLOADER = MENUS
			.register(TypeMachine.CHUNKLOADER.id(), () -> new MenuType<>(InventoryChunkloader::new));
	public static final RegistryObject<MenuType<InventoryPatternStorage>> MENU_PATTERN_STORAGE = MENUS
			.register(TypeMachine.PATTERN_STORAGE.id(), () -> new MenuType<>(InventoryPatternStorage::new));
	public static final RegistryObject<MenuType<InventoryMatterReplicator>> MENU_MATTER_REPLICATOR = MENUS
			.register(TypeMachine.MATTER_REPLICATOR.id(), () -> new MenuType<>(InventoryMatterReplicator::new));
	public static final RegistryObject<MenuType<InventoryPatternMonitor>> MENU_PATTERN_MONITOR = MENUS
			.register(TypeMachine.PATTERN_MONITOR.id(), () -> new MenuType<>(InventoryPatternMonitor::new));
	public static final RegistryObject<MenuType<InventoryMatterAnalyzer>> MENU_MATTER_ANALYZER = MENUS
			.register(TypeMachine.MATTER_ANALYZER.id(), () -> new MenuType<>(InventoryMatterAnalyzer::new));
	
}
