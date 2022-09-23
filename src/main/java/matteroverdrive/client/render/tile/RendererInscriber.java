package matteroverdrive.client.render.tile;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import matteroverdrive.client.render.tile.utils.AbstractTileRenderer;
import matteroverdrive.common.tile.TileInscriber;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.world.item.ItemStack;

public class RendererInscriber extends AbstractTileRenderer<TileInscriber> {

	public RendererInscriber(Context context) {
		super(context);
	}

	@Override
	public void render(TileInscriber inscriber, float tick, PoseStack pose, MultiBufferSource source, int light,
			int overlay) {
		if (inscriber.getInventoryCap() != null) {
			ItemStack stack = null;
			List<ItemStack> items = inscriber.getInventoryCap().getItems();
			if (!items.get(0).isEmpty()) {
				stack = items.get(0);
			} else if (!items.get(2).isEmpty()) {
				stack = items.get(2);
			}
			if (stack != null) {
				pose.pushPose();
				pose.translate(0.5f, 0.68f, 0.5f);
				pose.scale(0.45f, 0.45f, 0.45f);
				switch (inscriber.getFacing()) {
				case NORTH, SOUTH:
					pose.mulPose(new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), -90.0F, true));
					pose.mulPose(new Quaternion(new Vector3f(0.0F, 0.0F, 1.0F), 90.0F, true));
					break;
				case EAST, WEST:
					pose.mulPose(new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), -90.0F, true));
					break;
				default:
					break;
				}

				UtilsRendering.renderItem(stack, light, overlay, pose, source);
				pose.popPose();
			}
		}
	}

}
