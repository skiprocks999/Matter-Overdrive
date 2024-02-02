package matteroverdrive.client.render.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import matteroverdrive.client.render.item.VariableAlphaItemRenderer;
import matteroverdrive.client.render.tile.utils.AbstractTileRenderer;
import matteroverdrive.common.tile.matter_network.matter_replicator.TileMatterReplicator;
import matteroverdrive.common.tile.matter_network.matter_replicator.utils.QueuedReplication;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RendererMatterReplicator extends AbstractTileRenderer<TileMatterReplicator> {

	private int simLifespan = 0;

	public RendererMatterReplicator(Context context) {
		super(context);
	}

	@Override
	public void render(TileMatterReplicator replicator, float ticks, @NotNull PoseStack matrix,
										 @NotNull MultiBufferSource buffer, int light, int overlay) {
		ItemStack stack;
		boolean shouldSpin = false;
		QueuedReplication replication = replicator.getCurrentOrder();
		if (replicator.isRunning() && !replication.isEmpty()) {
			stack = new ItemStack(replication.getItem());
			shouldSpin = true;
		} else {
			stack = replicator.getInventoryCap().getStackInSlot(2);
		}
		if (!stack.isEmpty()) {
			// Copy Pasta time
			matrix.pushPose();

			matrix.translate(0.5, 0.3, 0.5);
			VariableAlphaItemRenderer renderer = new VariableAlphaItemRenderer(getItemRenderer());

			random.setSeed(Item.getId(stack.getItem()) + stack.getDamageValue());

			BakedModel model = renderer.getModel(stack, replicator.getLevel(), null, ITEM_RENDERER_SEED);

			boolean isGui3D = model.isGui3d();

			float spin = ((float) simLifespan + ticks) / 20.0F;

			if (shouldSpin) {
				matrix.mulPose(Vector3f.YP.rotation(spin));
				float processingTime = (float) (replicator.getProcessingTime() == 0 ? 1.0F : replicator.getProcessingTime());
				renderer.setAlpha((float) replicator.getProgress() / processingTime);
			} else {
				renderer.setAlpha(1.0F);
			}

			if (!isGui3D) {
				matrix.translate(-0.0D, -0.0D, -0.0D);
			}

			if (!shouldSpin) {
				matrix.mulPose(Vector3f.YP.rotationDegrees(90));
			}

			renderer.render(stack, ItemTransforms.TransformType.GROUND, false, matrix, buffer, light,
					OverlayTexture.NO_OVERLAY, model);

			simLifespan++;
			if (simLifespan >= 6000) {
				simLifespan = 0;
			}

			matrix.popPose();
		} else {
			simLifespan = 0;
		}
	}

}
