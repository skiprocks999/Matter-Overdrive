package matteroverdrive.registry;

import matteroverdrive.common.tile.station.TileAndroidStation;
import org.apache.commons.compress.utils.Sets;

import matteroverdrive.References;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.tile.TileCharger;
import matteroverdrive.common.tile.TileChunkloader;
import matteroverdrive.common.tile.TileInscriber;
import matteroverdrive.common.tile.TileMatterConduit;
import matteroverdrive.common.tile.TileMatterDecomposer;
import matteroverdrive.common.tile.TileMatterRecycler;
import matteroverdrive.common.tile.TileMicrowave;
import matteroverdrive.common.tile.TileSolarPanel;
import matteroverdrive.common.tile.TileSpacetimeAccelerator;
import matteroverdrive.common.tile.TileTritaniumCrate;
import matteroverdrive.common.tile.matter_network.TileDiscManipulator;
import matteroverdrive.common.tile.matter_network.TileMatterAnalyzer;
import matteroverdrive.common.tile.matter_network.TileMatterNetworkCable;
import matteroverdrive.common.tile.matter_network.TilePatternMonitor;
import matteroverdrive.common.tile.matter_network.TilePatternStorage;
import matteroverdrive.common.tile.matter_network.matter_replicator.TileMatterReplicator;
import matteroverdrive.common.tile.transporter.TileTransporter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class TileRegistry {

	public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister
			.create(ForgeRegistries.BLOCK_ENTITY_TYPES, References.ID);

	public static final RegistryObject<BlockEntityType<TileTritaniumCrate>> TILE_TRITANIUM_CRATE = TILES.register(
			"tritanium_crate",
			() -> new BlockEntityType<>(TileTritaniumCrate::new,
					Sets.newHashSet(BlockRegistry.BLOCK_TRITANIUM_CRATES.<Block>getObjectsAsArray(new Block[0])),
					null));
	public static final RegistryObject<BlockEntityType<TileSolarPanel>> TILE_SOLAR_PANEL = TILES
			.register(TypeMachine.SOLAR_PANEL.id(), () -> new BlockEntityType<>(TileSolarPanel::new,
					Sets.newHashSet(BlockRegistry.BLOCK_SOLAR_PANEL.get()), null));
	public static final RegistryObject<BlockEntityType<TileMatterDecomposer>> TILE_MATTER_DECOMPOSER = TILES
			.register(TypeMachine.MATTER_DECOMPOSER.id(), () -> new BlockEntityType<>(TileMatterDecomposer::new,
					Sets.newHashSet(BlockRegistry.BLOCK_MATTER_DECOMPOSER.get()), null));
	public static final RegistryObject<BlockEntityType<TileMatterRecycler>> TILE_MATTER_RECYCLER = TILES
			.register(TypeMachine.MATTER_RECYCLER.id(), () -> new BlockEntityType<>(TileMatterRecycler::new,
					Sets.newHashSet(BlockRegistry.BLOCK_MATTER_RECYCLER.get()), null));
	public static final RegistryObject<BlockEntityType<TileCharger>> TILE_CHARGER = TILES.register(
			TypeMachine.CHARGER.id(),
			() -> new BlockEntityType<>(TileCharger::new, Sets.newHashSet(BlockRegistry.BLOCK_CHARGER.get()), null));
	public static final RegistryObject<BlockEntityType<TileMicrowave>> TILE_MICROWAVE = TILES
			.register(TypeMachine.MICROWAVE.id(), () -> new BlockEntityType<>(TileMicrowave::new,
					Sets.newHashSet(BlockRegistry.BLOCK_MICROWAVE.get()), null));
	public static final RegistryObject<BlockEntityType<TileInscriber>> TILE_INSCRIBER = TILES
			.register(TypeMachine.INSCRIBER.id(), () -> new BlockEntityType<>(TileInscriber::new,
					Sets.newHashSet(BlockRegistry.BLOCK_INSCRIBER.get()), null));
	public static final RegistryObject<BlockEntityType<TileMatterConduit>> TILE_MATTER_CONDUIT = TILES
			.register("matter_conduit", () -> new BlockEntityType<>(TileMatterConduit::new,
					Sets.newHashSet(BlockRegistry.BLOCK_MATTER_CONDUITS.getObjectsAsArray(new Block[0])), null));
	public static final RegistryObject<BlockEntityType<TileTransporter>> TILE_TRANSPORTER = TILES
			.register(TypeMachine.TRANSPORTER.id(), () -> new BlockEntityType<>(TileTransporter::new,
					Sets.newHashSet(BlockRegistry.BLOCK_TRANSPORTER.get()), null));
	public static final RegistryObject<BlockEntityType<TileSpacetimeAccelerator>> TILE_SPACETIME_ACCELERATOR = TILES
			.register(TypeMachine.SPACETIME_ACCELERATOR.id(), () -> new BlockEntityType<>(TileSpacetimeAccelerator::new,
					Sets.newHashSet(BlockRegistry.BLOCK_SPACETIME_ACCELERATOR.get()), null));
	public static final RegistryObject<BlockEntityType<TileMatterNetworkCable>> TILE_MATTER_NETWORK_CABLE = TILES
			.register("network_cable", () -> new BlockEntityType<>(TileMatterNetworkCable::new,
					Sets.newHashSet(BlockRegistry.BLOCK_MATTER_NETWORK_CABLES.getObjectsAsArray(new Block[0])), null));
	public static final RegistryObject<BlockEntityType<TileChunkloader>> TILE_CHUNKLOADER = TILES
			.register(TypeMachine.CHUNKLOADER.id(), () -> new BlockEntityType<>(TileChunkloader::new,
					Sets.newHashSet(BlockRegistry.BLOCK_CHUNKLOADER.get()), null));
	public static final RegistryObject<BlockEntityType<TileMatterAnalyzer>> TILE_MATTER_ANALYZER = TILES
			.register(TypeMachine.MATTER_ANALYZER.id(), () -> new BlockEntityType<>(TileMatterAnalyzer::new,
					Sets.newHashSet(BlockRegistry.BLOCK_MATTER_ANALYZER.get()), null));
	public static final RegistryObject<BlockEntityType<TilePatternStorage>> TILE_PATTERN_STORAGE = TILES
			.register(TypeMachine.PATTERN_STORAGE.id(), () -> new BlockEntityType<>(TilePatternStorage::new,
					Sets.newHashSet(BlockRegistry.BLOCK_PATTERN_STORAGE.get()), null));
	public static final RegistryObject<BlockEntityType<TilePatternMonitor>> TILE_PATTERN_MONITOR = TILES
			.register(TypeMachine.PATTERN_MONITOR.id(), () -> new BlockEntityType<>(TilePatternMonitor::new,
					Sets.newHashSet(BlockRegistry.BLOCK_PATTERN_MONITOR.get()), null));
	public static final RegistryObject<BlockEntityType<TileMatterReplicator>> TILE_MATTER_REPLICATOR = TILES
			.register(TypeMachine.MATTER_REPLICATOR.id(), () -> new BlockEntityType<>(TileMatterReplicator::new,
					Sets.newHashSet(BlockRegistry.BLOCK_MATTER_REPLICATOR.get()), null));
	public static final RegistryObject<BlockEntityType<TileAndroidStation>> TILE_ANDROID_STATION = TILES
					.register(TypeMachine.ANDROID_STATION.id(), () -> new BlockEntityType<>(TileAndroidStation::new,
									Sets.newHashSet(BlockRegistry.BLOCK_ANDROID_STATION.get()), null));
	public static final RegistryObject<BlockEntityType<TileDiscManipulator>> TILE_DISC_MANIPULATOR = TILES
			.register(TypeMachine.DISC_MANIPULATOR.id(), () -> new BlockEntityType<>(TileDiscManipulator::new, Sets.newHashSet(BlockRegistry.BLOCK_DISC_MANIPULATOR.get()), null));

}
