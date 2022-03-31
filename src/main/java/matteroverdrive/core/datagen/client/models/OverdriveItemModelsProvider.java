package matteroverdrive.core.datagen.client.models;

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
		for(BlockColors color : BlockColors.values()) {
			withExistingParent(DeferredRegisters.FLOOT_TILE.get(color).get().getRegistryName().getPath(), modLoc("block/floor_tile_colorless"));
		}
	}

}
