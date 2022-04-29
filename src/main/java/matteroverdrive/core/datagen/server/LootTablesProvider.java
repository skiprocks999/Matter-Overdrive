package matteroverdrive.core.datagen.server;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.tile.TileMatterDecomposer;
import matteroverdrive.common.tile.TileMatterRecycler;
import matteroverdrive.common.tile.TileSolarPanel;
import matteroverdrive.common.tile.TileTritaniumCrate;
import matteroverdrive.core.datagen.utils.AbstractLootTableProvider;
import matteroverdrive.core.tile.GenericTile;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

public class LootTablesProvider extends AbstractLootTableProvider {

	public LootTablesProvider(DataGenerator generator) {
		super(generator);
	}

	@Override
	protected void addTables() {

		for (RegistryObject<Block> crate : DeferredRegisters.TRITANIUM_CRATES.getAll()) {
			this.<TileTritaniumCrate>addITable(crate, DeferredRegisters.TILE_TRITANIUM_CRATE);
		}
		this.<TileSolarPanel>addIETable(DeferredRegisters.BLOCK_SOLAR_PANEL, DeferredRegisters.TILE_SOLAR_PANEL);
		this.<TileMatterDecomposer>addIEMTable(DeferredRegisters.BLOCK_MATTER_DECOMPOSER,
				DeferredRegisters.TILE_MATTER_DECOMPOSER);
		this.<TileMatterRecycler>addIETable(DeferredRegisters.BLOCK_MATTER_RECYCLER,
				DeferredRegisters.TILE_MATTER_RECYCLER);

	}

	private <T extends GenericTile> void addITable(RegistryObject<Block> reg,
			RegistryObject<BlockEntityType<T>> tilereg) {
		Block block = reg.get();
		lootTables.put(block, itemOnlyTable(block.getRegistryName().getPath(), block, tilereg.get()));
	}

	private <T extends GenericTile> void addIETable(RegistryObject<Block> reg,
			RegistryObject<BlockEntityType<T>> tilereg) {
		Block block = reg.get();
		lootTables.put(block, itemAndEnergyTable(block.getRegistryName().getPath(), block, tilereg.get()));
	}

	private <T extends GenericTile> void addIEMTable(RegistryObject<Block> reg,
			RegistryObject<BlockEntityType<T>> tilereg) {
		Block block = reg.get();
		lootTables.put(block, itemEnergyMatterTable(block.getRegistryName().getPath(), block, tilereg.get()));
	}

}
