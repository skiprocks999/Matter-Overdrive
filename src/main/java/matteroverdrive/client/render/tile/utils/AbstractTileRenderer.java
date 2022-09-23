package matteroverdrive.client.render.tile.utils;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class AbstractTileRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {

	protected BlockEntityRendererProvider.Context context;

	public AbstractTileRenderer(BlockEntityRendererProvider.Context context) {
		this.context = context;
	}

	@Override
	public abstract void render(T tile, float ticks, PoseStack matrix, MultiBufferSource buffer, int light,
			int overlay);

	public long getGameTime() {
		return Minecraft.getInstance().level.getGameTime();
	}

}
