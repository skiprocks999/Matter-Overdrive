package matteroverdrive.core.datagen.client;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.References;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class OverdriveBlockModelsProvider extends BlockModelProvider {

	public OverdriveBlockModelsProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, References.ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		blockTopBottom(DeferredRegisters.BLOCK_TRANSPORTER, "block/transporter/transporter_top", "block/transporter/transporter_bottom", "block/transporter/transporter_side");
	}
	
	private void blockTopBottom(RegistryObject<Block> block, String top, String bottom, String side) {
		cubeBottomTop(block.get().getRegistryName().getPath(), new ResourceLocation(References.ID, side), new ResourceLocation(References.ID, bottom), new ResourceLocation(References.ID, top));
	}
	


}
