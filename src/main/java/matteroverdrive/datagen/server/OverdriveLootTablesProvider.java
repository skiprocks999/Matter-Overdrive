package matteroverdrive.datagen.server;

import matteroverdrive.common.block.OverdriveBlockColors;
import matteroverdrive.common.block.type.TypeMatterConduit;
import matteroverdrive.common.block.type.TypeMatterNetworkCable;
import matteroverdrive.common.tile.TileCharger;
import matteroverdrive.common.tile.TileChunkloader;
import matteroverdrive.common.tile.TileMatterDecomposer;
import matteroverdrive.common.tile.TileMatterRecycler;
import matteroverdrive.common.tile.TileMicrowave;
import matteroverdrive.common.tile.TileSolarPanel;
import matteroverdrive.common.tile.TileSpacetimeAccelerator;
import matteroverdrive.common.tile.TileTritaniumCrate;
import matteroverdrive.common.tile.matter_network.TileMatterAnalyzer;
import matteroverdrive.common.tile.matter_network.TilePatternMonitor;
import matteroverdrive.common.tile.matter_network.TilePatternStorage;
import matteroverdrive.common.tile.matter_network.matter_replicator.TileMatterReplicator;
import matteroverdrive.common.tile.transporter.TileTransporter;
import matteroverdrive.core.tile.GenericTile;
import matteroverdrive.datagen.utils.AbstractLootTableProvider;
import matteroverdrive.registry.BlockRegistry;
import matteroverdrive.registry.TileRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class OverdriveLootTablesProvider extends AbstractLootTableProvider {

	public OverdriveLootTablesProvider(DataGenerator generator) {
		super(generator);
	}

	@Override
	protected void addTables() {
		
		addSimpleBlock(BlockRegistry.BLOCK_REGULAR_TRITANIUM_PLATING);
		
		for (OverdriveBlockColors color : OverdriveBlockColors.values()) {
			addSimpleBlock(BlockRegistry.BLOCK_COLORED_TRITANIUM_PLATING.get(color));
			addSimpleBlock(BlockRegistry.BLOCK_FLOOR_TILE.get(color));
			addSimpleBlock(BlockRegistry.BLOCK_FLOOR_TILES.get(color));
		}
		
		addSilkTouchOnlyTable(BlockRegistry.BLOCK_INDUSTRIAL_GLASS);
		addSimpleBlock(BlockRegistry.BLOCK_VENT_CLOSED);
		addSimpleBlock(BlockRegistry.BLOCK_VENT_OPEN);
		
		for (RegistryObject<Block> crate : BlockRegistry.BLOCK_TRITANIUM_CRATES.getAll()) {
			this.<TileTritaniumCrate>addITable(crate, TileRegistry.TILE_TRITANIUM_CRATE);
		}
		this.<TileSolarPanel>addIETable(BlockRegistry.BLOCK_SOLAR_PANEL, TileRegistry.TILE_SOLAR_PANEL);
		this.<TileMatterDecomposer>addIEMTable(BlockRegistry.BLOCK_MATTER_DECOMPOSER,
				TileRegistry.TILE_MATTER_DECOMPOSER);
		this.<TileMatterRecycler>addIETable(BlockRegistry.BLOCK_MATTER_RECYCLER, TileRegistry.TILE_MATTER_RECYCLER);
		this.<TileCharger>addIETable(BlockRegistry.BLOCK_CHARGER, TileRegistry.TILE_CHARGER);
		this.<TileMicrowave>addIETable(BlockRegistry.BLOCK_MICROWAVE, TileRegistry.TILE_MICROWAVE);
		this.<TileTransporter>addIETable(BlockRegistry.BLOCK_TRANSPORTER, TileRegistry.TILE_TRANSPORTER);
		this.<TileSpacetimeAccelerator>addIEMTable(BlockRegistry.BLOCK_SPACETIME_ACCELERATOR,
				TileRegistry.TILE_SPACETIME_ACCELERATOR);
		this.<TileChunkloader>addIEMTable(BlockRegistry.BLOCK_CHUNKLOADER, TileRegistry.TILE_CHUNKLOADER);
		this.<TileMatterAnalyzer>addIETable(BlockRegistry.BLOCK_MATTER_ANALYZER, TileRegistry.TILE_MATTER_ANALYZER);
		this.<TilePatternStorage>addIETable(BlockRegistry.BLOCK_PATTERN_STORAGE, TileRegistry.TILE_PATTERN_STORAGE);
		this.<TilePatternMonitor>addITable(BlockRegistry.BLOCK_PATTERN_MONITOR, TileRegistry.TILE_PATTERN_MONITOR);
		this.<TileMatterReplicator>addIEMTable(BlockRegistry.BLOCK_MATTER_REPLICATOR,
				TileRegistry.TILE_MATTER_REPLICATOR);

		for(TypeMatterNetworkCable type : TypeMatterNetworkCable.values()) {
			addSimpleBlock(BlockRegistry.BLOCK_MATTER_NETWORK_CABLES.get(type));
		}
		
		for(TypeMatterConduit type : TypeMatterConduit.values()) {
			addSimpleBlock(BlockRegistry.BLOCK_MATTER_CONDUITS.get(type));
		}
		
		
	}

	private <T extends GenericTile> void addITable(RegistryObject<Block> reg,
			RegistryObject<BlockEntityType<T>> tilereg) {
		Block block = reg.get();
		lootTables.put(block, itemOnlyTable(name(block), block, tilereg.get()));
	}

	private <T extends GenericTile> void addIETable(RegistryObject<Block> reg,
			RegistryObject<BlockEntityType<T>> tilereg) {
		Block block = reg.get();
		lootTables.put(block, itemAndEnergyTable(name(block), block, tilereg.get()));
	}

	private <T extends GenericTile> void addIEMTable(RegistryObject<Block> reg,
			RegistryObject<BlockEntityType<T>> tilereg) {
		Block block = reg.get();
		lootTables.put(block,itemEnergyMatterTable(name(block), block, tilereg.get()));
	}

	/**
	 * Adds the block to the loottables silk touch only
	 *
	 * @author SeaRobber69
	 * @param reg The block that will be added
	 */
	private void addSilkTouchOnlyTable(RegistryObject<Block> reg) {
		Block block = reg.get();
		lootTables.put(block, createSilkTouchOnlyTable(name(block), block));
	}
	
	private void addSimpleBlock(RegistryObject<Block> reg) {
		Block block = reg.get();
		lootTables.put(block, createSimpleBlockTable(name(block), block));
	}
	
	private String name(Block block) {
		return ForgeRegistries.BLOCKS.getKey(block).getPath();
	}

}
