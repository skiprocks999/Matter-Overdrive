package matteroverdrive.client.renderer.tile.utils;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class AbstractTileRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {

	public AbstractTileRenderer(BlockEntityRendererProvider.Context context) {

	}

}
