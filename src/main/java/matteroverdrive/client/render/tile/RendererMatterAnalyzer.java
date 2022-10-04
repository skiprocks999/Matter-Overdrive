package matteroverdrive.client.render.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import matteroverdrive.client.render.tile.utils.AbstractTileRenderer;
import matteroverdrive.common.tile.matter_network.TileMatterAnalyzer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class RendererMatterAnalyzer extends AbstractTileRenderer<TileMatterAnalyzer> {

	private int simLifespan = 0;
	
	public RendererMatterAnalyzer(Context context) {
		super(context);
	}

	@Override
	public void render(TileMatterAnalyzer tile, float ticks, PoseStack matrix, MultiBufferSource buffer, int light,
			int overlay) {
		
		if(!tile.isRunning() || tile.scannedItemProp.get().isEmpty()) {
			simLifespan = 0;
			return;
		}
		
		matrix.pushPose();
		
		matrix.translate(0.5D, 0.9D, 0.5D);
		
		ItemStack stack = tile.scannedItemProp.get();
		
		ItemRenderer renderer = getItemRenderer();

		random.setSeed((long) (Item.getId(stack.getItem()) + stack.getDamageValue()));

		BakedModel model = renderer.getModel(stack, tile.getLevel(), (LivingEntity) null, ITEM_RENDERER_SEED);

		boolean isGui3D = model.isGui3d();

		float spin = ((float) simLifespan + ticks) / 20.0F;

		matrix.mulPose(Vector3f.YP.rotation(spin));

		if (!isGui3D) {
			matrix.translate(-0.0D, -0.0D, -0.0D);
		}

		renderer.render(stack, ItemTransforms.TransformType.GROUND, false, matrix, buffer, light,
				OverlayTexture.NO_OVERLAY, model);

		simLifespan++;
		if (simLifespan >= 6000) {
			simLifespan = 0;
		}

		matrix.popPose();
		
	}

}
