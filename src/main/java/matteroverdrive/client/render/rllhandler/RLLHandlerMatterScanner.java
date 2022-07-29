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
import matteroverdrive.core.matter.MatterRegister;
import matteroverdrive.core.utils.UtilsNbt;
import matteroverdrive.core.utils.UtilsRendering;
import matteroverdrive.core.utils.UtilsText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class RLLHandlerMatterScanner extends AbstractRenderLevelLastHandler {
	
	private static final float[] GRID_COORDS = UtilsRendering.getCoordsFromAABB(UtilsRendering.AABB_BLOCK.inflate(0.001));
	
	// DUNSEW
	private static final float[][] SPINNER_COORDS = new float[][] {
		UtilsRendering.getCoordsFromAABB(new AABB(0.35, - 0.01, 0.35, 0.65, 0, 0.65).inflate(0.001)),
		UtilsRendering.getCoordsFromAABB(new AABB(0.35, 1, 0.35, 0.65,1.01, 0.65).inflate(0.001)),
		UtilsRendering.getCoordsFromAABB(new AABB(0.35, 0.35, -0.01, 0.65, 0.65, 0).inflate(0.001)),
		UtilsRendering.getCoordsFromAABB(new AABB(0.35, 0.35, 1.0, 0.65, 0.65, 1.01).inflate(0.001)),
		UtilsRendering.getCoordsFromAABB(new AABB(1.0, 0.35, 0.35, 1.01, 0.65, 0.65).inflate(0.001)),
		UtilsRendering.getCoordsFromAABB(new AABB(-0.01, 0.35, 0.35, 0, 0.65, 0.65).inflate(0.001))
	};
	
	@Override
	public void handleRendering(Minecraft minecraft, LevelRenderer renderer, PoseStack matrix, Matrix4f projMatrix,
			float partialTick, int startNano) {

		Player player = minecraft.player;
		BlockHitResult trace = Item.getPlayerPOVHitResult(player.level, player, net.minecraft.world.level.ClipContext.Fluid.ANY);
		ScannerDataWrapper scannerStatus = scannerHeldOnUse(player);
		if(trace.getType() != Type.MISS && trace.getType() != Type.ENTITY && scannerStatus.inUse && scannerStatus.on) {
			
			Level world = minecraft.level;
			
			MultiBufferSource.BufferSource buffer = minecraft.renderBuffers().bufferSource();
			VertexConsumer builder;
			
			BlockPos pos = trace.getBlockPos();
			Vec3 cam = minecraft.gameRenderer.getMainCamera().getPosition();
			Direction traceDir = trace.getDirection();
			Font font = minecraft.font;
			BlockState state = world.getBlockState(pos);
			
			/* Render Block Name */
			
			matrix.pushPose();
			
			translateToPos(matrix, pos, cam);
		
			Component text = state.getBlock().asItem().getDescription();
			
			int[] shift = moveMatrixForText(matrix, traceDir, player.getDirection(), font.width(text), 1.5D, 15.0D, 0.5);
			
			font.drawInBatch(text, shift[0], shift[1], UtilsRendering.TEXT_BLUE, false, matrix.last().pose(), buffer, true, 0, 255);
			
			matrix.popPose();
			
			/* Render Block Matter Val */
			matrix.pushPose();
			
			translateToPos(matrix, pos, cam);
			
			Double val = MatterRegister.INSTANCE.getClientMatterValue(new ItemStack(state.getBlock()));
			
			text = val == null
					? UtilsText.tooltip("matterval", UtilsText.tooltip("nomatter"))
					: UtilsText.tooltip("matterval", Component.literal(UtilsText.formatMatterValue(val)));
			
			shift = moveMatrixForText(matrix, traceDir, player.getDirection(), font.width(text), 3.5D, 13.0D, 0.75);
			
			font.drawInBatch(text, shift[0], shift[1], UtilsRendering.TEXT_BLUE, false, matrix.last().pose(), buffer, true, 0, 255);
			
			matrix.popPose();
			
			/* Render Stored Percentage */
			
			matrix.pushPose();
			
			translateToPos(matrix, pos, cam);
			
			text = Component.literal(UtilsText.SINGLE_DECIMAL.format(scannerStatus.percent) + "%");
			
			shift = moveMatrixForText(matrix, traceDir, player.getDirection(), font.width(text), 11.5D, 5.0D, 0.75);
			
			font.drawInBatch(text, shift[0], shift[1], UtilsRendering.TEXT_BLUE, false, matrix.last().pose(), buffer, true, 0, 255);
			
			matrix.popPose();
			
			/* Render Spinner and Grid */
			
			matrix.pushPose();
			
			translateToPos(matrix, pos, cam);
			
			rotateMatrixForScanner(matrix, player.getDirection(), traceDir);
			
			TextureAtlasSprite holoGrid = ClientRegister.CACHED_TEXTUREATLASSPRITES.get(ClientRegister.TEXTURE_HOLO_GRID);
			float[] holo_uv = {holoGrid.getU0(), holoGrid.getU1(), holoGrid.getV0(), holoGrid.getV1()};
			float[] holo_color = UtilsRendering.getColorArray(UtilsRendering.TEXT_BLUE);
			holo_color[3] = 0.2F;
			
			TextureAtlasSprite spinner = ClientRegister.CACHED_TEXTUREATLASSPRITES.get(ClientRegister.TEXTURE_SPINNER);
			float[] spinner_uv = {spinner.getU0(), spinner.getU1(), spinner.getV0(), spinner.getV1()};
			float cutoff = getCuttoffFloat(player, scannerStatus.held, scannerStatus.stack);
			float[] spinner_color = getSpinnerColor(scannerStatus.held, cutoff);
		
			Matrix4f matrix4f = matrix.last().pose();
			Matrix3f matrix3f = matrix.last().normal();
			
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
			
			builder = buffer.getBuffer(Sheets.translucentCullBlockSheet());
			
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
			
			matrix.popPose();
			
		}
		
	}
	
	//giving Neo a run for his money
	private int[] moveMatrixForText(PoseStack matrix, Direction traceDir, Direction playerDir, double txtWidth,
			double yUp, double yDown, double xOver) {
		
		switch(traceDir) {
		case SOUTH:
			matrix.translate(0, 0, 1.001);
			break;
		case NORTH:
			matrix.mulPose(new Quaternion(new Vector3f(0, 1, 0), 180.0F, true));
			matrix.translate(-1.0, 0, 0.001);
			break;
		case WEST:
			matrix.mulPose(new Quaternion(new Vector3f(0, 1, 0), 270.0F, true));
			matrix.translate(0, 0, 0.001);
			break;
		case EAST:
			matrix.mulPose(new Quaternion(new Vector3f(0, 1, 0), 90.0F, true));
			matrix.translate(-1 , 0, 1.001);
			break;
		default:
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
				if(traceDir == Direction.UP) {
					matrix.mulPose(new Quaternion(new Vector3f(1, 0, 0), -90.0F, true));
					matrix.translate(0, 0, 1.001);
				} else {
					matrix.mulPose(new Quaternion(new Vector3f(1, 0, 0), 90.0F, true));	
					matrix.translate(0, 0, 0.001);
				}
				
			}
			break;
		}
		
		

		//Fit text to 1 block wide face
		double baseScale = 1.0D / 16.153846153846153846153846153846D;
		double widthScale = txtWidth / 16.0D;
		double textScale = 5.2D;
		
		double addScale = textScale > widthScale ? textScale : widthScale;
		
		float actualScale = (float) ((float) baseScale / addScale);
		
		matrix.scale(actualScale, -actualScale, actualScale);
		
		//shift text to middle of block face
		
		double baseShift = 4.0D / 26.0D * addScale;
		
		double pixelConstant = 0.90933584803987606089182271318874D;
		
		int x = 0;
		
		if(textScale > widthScale) {
			double pixelWidth = txtWidth / addScale / pixelConstant;
			double xShift = 8.0D - pixelWidth / 2;
			x = (int) (baseShift + (xShift + xOver) * addScale);
		} else {
			x = (int) baseShift;
		}
		
		int y = 0;
		
		if(traceDir == Direction.UP) {
			y = -(int) (baseShift - yUp * addScale);
		} else {
			y = -(int) (yDown * addScale - baseShift);
		}
		
		
		
		return new int[] {x, y};
	}

	private ScannerDataWrapper scannerHeldOnUse(Player player) {
		ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
		boolean held = false;
		boolean on = false;
		boolean inUse = false;
		int perc = 0;
		if(stack.getItem() instanceof ItemMatterScanner scanner) {
			held = true;
			on = scanner.isOn(stack);
			inUse = scanner.isHeld(stack);
			perc = stack.getOrCreateTag().getInt(UtilsNbt.PERCENTAGE);
		} else {
			stack = player.getItemInHand(InteractionHand.OFF_HAND);
			if(stack.getItem() instanceof ItemMatterScanner scanner) {
				held = true;
				on = scanner.isOn(stack);
				inUse = scanner.isHeld(stack);
				perc = stack.getOrCreateTag().getInt(UtilsNbt.PERCENTAGE);
			}
		}
		return new ScannerDataWrapper(held, on, inUse, perc, stack);
	}
	
	private void rotateMatrixForScanner(PoseStack matrix, Direction playerDir, Direction traceDir) {
		if(traceDir == Direction.UP ) {
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
		} else if (traceDir == Direction.DOWN) {
			switch(playerDir) {
			case NORTH:
				matrix.mulPose(new Quaternion(new Vector3f(0, 1, 0), 180.0F, true));
				matrix.translate(-1, 0, -1);
				break;
			case WEST:
				matrix.mulPose(new Quaternion(new Vector3f(0, 1, 0), 270.0F, true));
				matrix.translate(0, 0, -1);
				break;
			case EAST:
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
	
	private float getCuttoffFloat(Player player, boolean isInUse, ItemStack stack) {
		if(isInUse) {
			ItemMatterScanner scanner = (ItemMatterScanner) stack.getItem();
			int count = scanner.getTimeRemaining(stack);
			int maxCount = scanner.getUseDuration(stack);
			
			return maxCount == 0 ? 0 : (float) count / (float) maxCount;
		} else {
			return 0.0F;
		}
	}
	
	private void translateToPos(PoseStack matrix, BlockPos pos, Vec3 cam) {
		matrix.translate(-cam.x() + (double)pos.getX(), -cam.y() + (double)pos.getY(), -cam.z() + (double)pos.getZ());
	}
	
	private static final class ScannerDataWrapper {
		
		private boolean inUse;
		private boolean on;
		private boolean held;
		private int percent;
		private ItemStack stack;
		
		private ScannerDataWrapper(boolean inUse, boolean on, boolean held, int percent, ItemStack stack) {
			this.inUse = inUse;
			this.on = on;
			this.held = held;
			this.percent = percent;
			this.stack = stack;
		}
		
	}

}
