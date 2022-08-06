package matteroverdrive.client.render.tile;

import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import matteroverdrive.common.tile.matter_network.matter_replicator.TileMatterReplicator;
import matteroverdrive.core.render.AbstractTileRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class RendererMatterReplicator extends AbstractTileRenderer<TileMatterReplicator> {

	private static final int SEED = 64;

	private int simLifespan = 0;

	private final Random random;

	public RendererMatterReplicator(Context context) {
		super(context);
		random = new Random();
	}

	@Override
	public void render(TileMatterReplicator replicator, float ticks, PoseStack matrix, MultiBufferSource buffer,
			int light, int overlay) {
		ItemStack stack = ItemStack.EMPTY;
		boolean shouldSpin = false;
		if (replicator.clientRunning && replicator.clientCurrentOrder != null) {
			stack = new ItemStack(replicator.clientCurrentOrder.getItem());
			shouldSpin = true;
		} else {
			stack = replicator.outputItem;
		}
		if (!stack.isEmpty()) {
			// Copy Pasta time
			matrix.pushPose();

			matrix.translate(0.5, 0.3, 0.5);

			ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();

			random.setSeed((long) (Item.getId(stack.getItem()) + stack.getDamageValue()));

			BakedModel model = renderer.getModel(stack, replicator.getLevel(), (LivingEntity) null, SEED);

			boolean isGui3D = model.isGui3d();

			float spin = ((float) simLifespan + ticks) / 20.0F;

			if (shouldSpin) {
				matrix.mulPose(Vector3f.YP.rotation(spin));
			}

			if (!isGui3D) {
				float f7 = -0.0F;
				float f8 = -0.0F;
				float f9 = -0.0F;
				matrix.translate((double) f7, (double) f8, (double) f9);
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
