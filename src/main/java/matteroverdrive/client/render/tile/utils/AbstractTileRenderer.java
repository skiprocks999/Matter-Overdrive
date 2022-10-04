package matteroverdrive.client.render.tile.utils;

import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class AbstractTileRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {

	protected static final int ITEM_RENDERER_SEED = 64;
	
	protected BlockEntityRendererProvider.Context context;
	protected final Random random;

	public AbstractTileRenderer(BlockEntityRendererProvider.Context context) {
		this.context = context;
		random = new Random();
	}

	@Override
	public abstract void render(T tile, float ticks, PoseStack matrix, MultiBufferSource buffer, int light,
			int overlay);

	public long getGameTime() {
		return Minecraft.getInstance().level.getGameTime();
	}
	
	public ItemRenderer getItemRenderer() {
		return Minecraft.getInstance().getItemRenderer();
	}

}
