package matteroverdrive.core.datagen.server;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.core.datagen.utils.AbstractLootTableProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;

public class LootTablesProvider extends AbstractLootTableProvider {

	public LootTablesProvider(DataGenerator generator) {
		super(generator);
	}

	@Override
	protected void addTables() {
		for (Block block : DeferredRegisters.TRITANIUM_CRATES.<Block>getAllObjects()) {
			lootTables.put(block, createStandardTable(block.getRegistryName().getPath(), block,
					DeferredRegisters.TILE_TRITANIUMCRATE.get()));
		}
	}

}
