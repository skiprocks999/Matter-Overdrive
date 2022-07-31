package matteroverdrive.core.datagen.server;

import matteroverdrive.DeferredRegisters;
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
import matteroverdrive.core.datagen.utils.AbstractLootTableProvider;
import matteroverdrive.core.tile.types.old.GenericTile;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class LootTablesProvider extends AbstractLootTableProvider {

	public LootTablesProvider(DataGenerator generator) {
		super(generator);
	}

	@Override
	protected void addTables() {

		for (RegistryObject<Block> crate : DeferredRegisters.BLOCK_TRITANIUM_CRATES.getAll()) {
			this.<TileTritaniumCrate>addITable(crate, DeferredRegisters.TILE_TRITANIUM_CRATE);
		}
		this.<TileSolarPanel>addIETable(DeferredRegisters.BLOCK_SOLAR_PANEL, DeferredRegisters.TILE_SOLAR_PANEL);
		this.<TileMatterDecomposer>addIEMTable(DeferredRegisters.BLOCK_MATTER_DECOMPOSER,
				DeferredRegisters.TILE_MATTER_DECOMPOSER);
		this.<TileMatterRecycler>addIETable(DeferredRegisters.BLOCK_MATTER_RECYCLER,
				DeferredRegisters.TILE_MATTER_RECYCLER);
		this.<TileCharger>addIETable(DeferredRegisters.BLOCK_CHARGER, DeferredRegisters.TILE_CHARGER);
		this.<TileMicrowave>addIETable(DeferredRegisters.BLOCK_MICROWAVE, DeferredRegisters.TILE_MICROWAVE);
		this.<TileTransporter>addIETable(DeferredRegisters.BLOCK_TRANSPORTER, DeferredRegisters.TILE_TRANSPORTER);
		this.<TileSpacetimeAccelerator>addIEMTable(DeferredRegisters.BLOCK_SPACETIME_ACCELERATOR, DeferredRegisters.TILE_SPACETIME_ACCELERATOR);
		this.<TileChunkloader>addIEMTable(DeferredRegisters.BLOCK_CHUNKLOADER, DeferredRegisters.TILE_CHUNKLOADER);
		this.<TileMatterAnalyzer>addIETable(DeferredRegisters.BLOCK_MATTER_ANALYZER, DeferredRegisters.TILE_MATTER_ANALYZER);
		this.<TilePatternStorage>addIETable(DeferredRegisters.BLOCK_PATTERN_STORAGE, DeferredRegisters.TILE_PATTERN_STORAGE);
		this.<TilePatternMonitor>addITable(DeferredRegisters.BLOCK_PATTERN_MONITOR, DeferredRegisters.TILE_PATTERN_MONITOR);
		this.<TileMatterReplicator>addIEMTable(DeferredRegisters.BLOCK_MATTER_REPLICATOR, DeferredRegisters.TILE_MATTER_REPLICATOR);

		this.addSilkTouchOnlyTable(DeferredRegisters.BLOCK_INDUSTRIAL_GLASS);
	}

	private <T extends GenericTile> void addITable(RegistryObject<Block> reg,
			RegistryObject<BlockEntityType<T>> tilereg) {
		Block block = reg.get();
		lootTables.put(block, itemOnlyTable(ForgeRegistries.BLOCKS.getKey(block).getPath(), block, tilereg.get()));
	}

	private <T extends GenericTile> void addIETable(RegistryObject<Block> reg,
			RegistryObject<BlockEntityType<T>> tilereg) {
		Block block = reg.get();
		lootTables.put(block, itemAndEnergyTable(ForgeRegistries.BLOCKS.getKey(block).getPath(), block, tilereg.get()));
	}

	private <T extends GenericTile> void addIEMTable(RegistryObject<Block> reg,
			RegistryObject<BlockEntityType<T>> tilereg) {
		Block block = reg.get();
		lootTables.put(block, itemEnergyMatterTable(ForgeRegistries.BLOCKS.getKey(block).getPath(), block, tilereg.get()));
	}

	/**
	 * Adds the block to the loottables silk touch only
	 *
	 * @author SeaRobber69
	 * @param reg The block that will be added
	 */
	private void addSilkTouchOnlyTable(RegistryObject<Block> reg) {
		Block block = reg.get();
		lootTables.put(block, createSilkTouchOnlyTable(ForgeRegistries.BLOCKS.getKey(block).getPath(), block));
	}

}
