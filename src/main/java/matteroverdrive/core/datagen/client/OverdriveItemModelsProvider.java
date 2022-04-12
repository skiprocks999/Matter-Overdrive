package matteroverdrive.core.datagen.client;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.References;
import matteroverdrive.common.block.utils.BlockColors;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class OverdriveItemModelsProvider extends ItemModelProvider {

	public OverdriveItemModelsProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, References.ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		for (BlockColors color : BlockColors.values()) {
			withExistingParent(DeferredRegisters.REGULAR_TRITANIUM_PLATING.get().getRegistryName().getPath(),
					modLoc("block/tritanium_plating"));
			withExistingParent(DeferredRegisters.COLORED_TRITANIUM_PLATING.get(color).get().getRegistryName().getPath(),
					modLoc("block/tritanium_plating_colorless"));
			withExistingParent(DeferredRegisters.FLOOR_TILE.get(color).get().getRegistryName().getPath(),
					modLoc("block/floor_tile_colorless"));
			withExistingParent(DeferredRegisters.FLOOR_TILES.get(color).get().getRegistryName().getPath(),
					modLoc("block/floor_tiles_colorless"));
		}
	}

}
