package matteroverdrive.client.render.rllhandler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import matteroverdrive.client.ClientRegister;
import matteroverdrive.client.render.shaders.MORenderTypes;
import matteroverdrive.common.item.tools.electric.ItemMatterScanner;
import matteroverdrive.core.eventhandler.client.AbstractRenderLevelLastHandler;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class RLLHandlerMatterScanner extends AbstractRenderLevelLastHandler {
	
	private static final float[] GRID_COORDS = UtilsRendering.getCoordsFromAABB(UtilsRendering.AABB_BLOCK.inflate(0.05));
	
	// DUNSEW
	private static final float[][] SPINNER_COORDS = new float[][] {
		UtilsRendering.getCoordsFromAABB(new AABB(0.35, - 0.01, 0.35, 0.65, 0, 0.65)),
		UtilsRendering.getCoordsFromAABB(new AABB(0.35, 1, 0.35, 0.65,1.01, 0.65)),
		UtilsRendering.getCoordsFromAABB(new AABB(0.35, 0.35, -0.01, 0.65, 0.65, 0)),
		UtilsRendering.getCoordsFromAABB(new AABB(0.35, 0.35, 1.0, 0.65, 0.65, 1.01)),
		UtilsRendering.getCoordsFromAABB(new AABB(1.0, 0.35, 0.35, 1.01, 0.65, 0.65)),
		UtilsRendering.getCoordsFromAABB(new AABB(-0.01, 0.35, 0.35, 0, 0.65, 0.65))
	};
	
	@Override
	public void handleRendering(Minecraft minecraft, LevelRenderer renderer, PoseStack matrix, Matrix4f projMatrix,
			float partialTick, long startNano) {

		Player player = minecraft.player;
		BlockHitResult trace = Item.getPlayerPOVHitResult(player.level, player, net.minecraft.world.level.ClipContext.Fluid.ANY);
		boolean[] scannerStatus = scannerHeldOnUse(player);
		if(trace.getType() != Type.MISS && trace.getType() != Type.ENTITY && scannerStatus[0] && scannerStatus[1]) {
			
			MultiBufferSource.BufferSource buffer = minecraft.renderBuffers().bufferSource();
			VertexConsumer builder = buffer.getBuffer(Sheets.translucentCullBlockSheet());
			
			matrix.pushPose();
			BlockPos pos = trace.getBlockPos();
			
			Vec3 cam = minecraft.gameRenderer.getMainCamera().getPosition();
			matrix.translate(-cam.x() + (double)pos.getX(), -cam.y() + (double)pos.getY(), -cam.z() + (double)pos.getZ());
			
			Direction traceDir = trace.getDirection();
			
			rotateMatrixForScanner(matrix, player.getDirection(), traceDir);
			
			TextureAtlasSprite holoGrid = ClientRegister.CACHED_TEXTUREATLASSPRITES.get(ClientRegister.TEXTURE_HOLO_GRID);
			float[] holo_uv = {holoGrid.getU0(), holoGrid.getU1(), holoGrid.getV0(), holoGrid.getV1()};
			float[] holo_color = UtilsRendering.getColorArray(UtilsRendering.TEXT_BLUE);
			holo_color[3] = 0.2F;
			
			TextureAtlasSprite spinner = ClientRegister.CACHED_TEXTUREATLASSPRITES.get(ClientRegister.TEXTURE_SPINNER);
			float[] spinner_uv = {spinner.getU0(), spinner.getU1(), spinner.getV0(), spinner.getV1()};
			float cutoff = getCuttoffFloat(player, scannerStatus[2]);
			float[] spinner_color = getSpinnerColor(scannerStatus[2], cutoff);
		
			Matrix4f matrix4f = matrix.last().pose();
			Matrix3f matrix3f = matrix.last().normal();
			
			switch(traceDir) {
			case DOWN:
				UtilsRendering.renderBottomOfBox(builder, GRID_COORDS, holo_color, holo_uv, matrix4f, matrix3f, 255, OverlayTexture.NO_OVERLAY);
				break;
			case UP:
				UtilsRendering.renderTopOfBox(builder, GRID_COORDS, holo_color, holo_uv, matrix4f, matrix3f, 255, OverlayTexture.NO_OVERLAY);
				break;
			case NORTH:
				UtilsRendering.renderNorthOfBox(builder, GRID_COORDS, holo_color, holo_uv, matrix4f, matrix3f, 255, OverlayTexture.NO_OVERLAY);
				break;
			case SOUTH:
				UtilsRendering.renderSouthOfBox(builder, GRID_COORDS, holo_color, holo_uv, matrix4f, matrix3f, 255, OverlayTexture.NO_OVERLAY);
				break;
			case EAST:
				UtilsRendering.renderEastOfBox(builder, GRID_COORDS, holo_color, holo_uv, matrix4f, matrix3f, 255, OverlayTexture.NO_OVERLAY);
				break;
			case WEST:
				UtilsRendering.renderWestOfBox(builder, GRID_COORDS, holo_color, holo_uv, matrix4f, matrix3f, 255, OverlayTexture.NO_OVERLAY);
				break;
			}
			
			buffer.endBatch(Sheets.translucentCullBlockSheet());
			
			builder = buffer.getBuffer(MORenderTypes.getRenderTypeAlphaCutoff(cutoff));
			
			switch(traceDir) {
			case DOWN:
				UtilsRendering.renderBottomOfBox(builder, SPINNER_COORDS[0], spinner_color, spinner_uv, matrix4f, matrix3f, 255, OverlayTexture.NO_OVERLAY);
				break;
			case UP:
				UtilsRendering.renderTopOfBox(builder, SPINNER_COORDS[1], spinner_color, spinner_uv, matrix4f, matrix3f, 255, OverlayTexture.NO_OVERLAY);
				break;
			case NORTH:
				UtilsRendering.renderNorthOfBox(builder, SPINNER_COORDS[2], spinner_color, spinner_uv, matrix4f, matrix3f, 255, OverlayTexture.NO_OVERLAY);
				break;
			case SOUTH:
				UtilsRendering.renderSouthOfBox(builder, SPINNER_COORDS[3], spinner_color, spinner_uv, matrix4f, matrix3f, 255, OverlayTexture.NO_OVERLAY);
				break;
			case EAST:
				UtilsRendering.renderEastOfBox(builder, SPINNER_COORDS[4], spinner_color, spinner_uv, matrix4f, matrix3f, 255, OverlayTexture.NO_OVERLAY);
				break;
			case WEST:
				UtilsRendering.renderWestOfBox(builder, SPINNER_COORDS[5], spinner_color, spinner_uv, matrix4f, matrix3f, 255, OverlayTexture.NO_OVERLAY);
				break;
			}
			
			buffer.endBatch(MORenderTypes.GREATER_ALPHA);
			
			matrix.popPose();
		}
		
	}
	
	private boolean[] scannerHeldOnUse(Player player) {
		ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
		boolean held = false;
		boolean on = false;
		boolean inUse = false;
		if(stack.getItem() instanceof ItemMatterScanner scanner) {
			held = true;
			on = scanner.isOn(stack);
			inUse = player.isUsingItem() && player.getUseItem().getItem() instanceof ItemMatterScanner;
		} else {
			stack = player.getItemInHand(InteractionHand.OFF_HAND);
			if(stack.getItem() instanceof ItemMatterScanner scanner) {
				held = true;
				on = scanner.isOn(stack);
				inUse = player.isUsingItem() && player.getUseItem().getItem() instanceof ItemMatterScanner;
			}
		}
		return new boolean[] {held, on, inUse};
	}
	
	private void rotateMatrixForScanner(PoseStack matrix, Direction playerDir, Direction traceDir) {
		if(traceDir == Direction.UP || traceDir == Direction.DOWN) {
			switch(playerDir) {
			case SOUTH:
				matrix.mulPose(new Quaternion(new Vector3f(0, 1, 0), 180.0F, true));
				matrix.translate(-1, 0, -1);
				break;
			case EAST:
				matrix.mulPose(new Quaternion(new Vector3f(0, 1, 0), 270.0F, true));
				matrix.translate(0, 0, -1);
				break;
			case WEST:
				matrix.mulPose(new Quaternion(new Vector3f(0, 1, 0), 90.0F, true));
				matrix.translate(-1 , 0, 0);
				break;
			default:
				break;
			}
		}
		
	}
	
	private float[] getSpinnerColor(boolean isInUse, float ratio) {
		if(isInUse) {
			
			float iRatio = 1.0F - ratio;
			
			return new float[] { UtilsRendering.FLOAT_HOLO_RED[0] * ratio + UtilsRendering.FLOAT_HOLO_GREEN[0] * iRatio, UtilsRendering.FLOAT_HOLO_RED[1] * ratio + UtilsRendering.FLOAT_HOLO_GREEN[1] * iRatio,
					UtilsRendering.FLOAT_HOLO_RED[2] * ratio + UtilsRendering.FLOAT_HOLO_GREEN[2] * iRatio, UtilsRendering.FLOAT_HOLO_RED[3] * ratio + UtilsRendering.FLOAT_HOLO_GREEN[3] * iRatio };

		} else {
			return UtilsRendering.FLOAT_TEXT_BLUE;
		}
	}
	
	private float getCuttoffFloat(Player player, boolean isInUse) {
		if(isInUse) {
			int count = player.getUseItemRemainingTicks();
			int maxCount = player.getUseItem().getUseDuration();

			return ((float) count / (float) maxCount);
		} else {
			return 0.0F;
		}
	}

}
