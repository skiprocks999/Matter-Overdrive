package matteroverdrive.client.render.item;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class VariableAlphaItemRenderer extends ItemRenderer {

	private float alpha = 1.0F;

	public VariableAlphaItemRenderer(ItemRenderer renderer) {
		super(renderer.textureManager, renderer.itemModelShaper.getModelManager(), renderer.itemColors,
				renderer.blockEntityRenderer);
	}

	public void setAlpha(float alpha) {
		this.alpha = Mth.clamp(alpha, 0.0F, 1.0F);
	}

	@Override
	public void renderQuadList(PoseStack matrix, VertexConsumer consumer, List<BakedQuad> quads, ItemStack stack,
			int light, int overlay) {
		boolean itemNotEmpty = !stack.isEmpty();
		PoseStack.Pose pose = matrix.last();

		for (BakedQuad bakedquad : quads) {
			int color = -1;
			if (itemNotEmpty && bakedquad.isTinted()) {
				color = this.itemColors.getColor(stack, bakedquad.getTintIndex());
			}

			float[] colorArr = UtilsRendering.getColorArray(color);
			consumer.putBulkData(pose, bakedquad, colorArr[0], colorArr[1], colorArr[2], alpha, light, overlay, true);
		}

	}

}
