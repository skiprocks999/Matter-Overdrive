package matteroverdrive.client.render.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import matteroverdrive.client.ClientRegister;
import matteroverdrive.common.tile.TileCharger;
import matteroverdrive.core.render.AbstractTileRenderer;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;

public class RendererCharger extends AbstractTileRenderer<TileCharger> {

	public RendererCharger(Context context) {
		super(context);
	}

	@Override
	public void render(TileCharger charger, float partial, PoseStack stack, MultiBufferSource source, int light,
			int overlay) {

		Direction facing = charger.getFacingDirection();
		switch (facing) {
		case NORTH -> {
			stack.mulPose(new Quaternion(new Vector3f(0, 1, 0), 90.0F, true));
			stack.translate(0, 0.5, 1.0);
		}
		case SOUTH -> {
			stack.mulPose(new Quaternion(new Vector3f(0, 1, 0), 270.0F, true));
			stack.translate(1, 0.5, 0);
		}
		case EAST -> stack.translate(1.0, 0.5, 1.0);
		case WEST -> {
			stack.mulPose(new Quaternion(new Vector3f(0, 1, 0), 180.0F, true));
			stack.translate(0, 0.5, 0);
		}
		default -> {
		}
		}

		BakedModel ibakedmodel = UtilsRendering.getBakedModel(ClientRegister.MODEL_CHARGER);
		UtilsRendering.renderModel(ibakedmodel, charger, RenderType.solid(), stack, source, light, overlay);

	}

}
