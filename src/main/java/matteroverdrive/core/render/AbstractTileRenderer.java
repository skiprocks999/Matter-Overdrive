package matteroverdrive.core.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class AbstractTileRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {

	public AbstractTileRenderer(BlockEntityRendererProvider.Context context) {

	}
	
	public long getGameTime() {
		return Minecraft.getInstance().level.getGameTime();
	}

}
