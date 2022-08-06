package matteroverdrive.client.render.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;

import matteroverdrive.client.ClientRegister;
import matteroverdrive.common.block.machine.old.variants.BlockLightableMachine;
import matteroverdrive.common.block.states.OverdriveBlockStates;
import matteroverdrive.common.block.states.OverdriveBlockStates.VerticalFacing;
import matteroverdrive.common.tile.matter_network.TilePatternMonitor;
import matteroverdrive.core.render.AbstractTileRenderer;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class RendererPatternMonitor extends AbstractTileRenderer<TilePatternMonitor> {

	// DUNSEW
	private static final float[][] GRID_COORDS = new float[][] {
			UtilsRendering.getCoordsFromAABB(new AABB(0.0D, 0.685D, 0.0D, 1.0D, 1.0D, 1.0D)),
			UtilsRendering.getCoordsFromAABB(new AABB(0.0D, 0.0D, 0.0D, 1.0D, 0.315D, 1.0D)),
			UtilsRendering.getCoordsFromAABB(new AABB(0.0D, 0.0D, 0.685D, 1.0D, 1.0D, 1.0D)),
			UtilsRendering.getCoordsFromAABB(new AABB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.315D)),
			UtilsRendering.getCoordsFromAABB(new AABB(0.0D, 0.0D, 0.0D, 0.315D, 1.0D, 1.0D)),
			UtilsRendering.getCoordsFromAABB(new AABB(0.685D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)) };

	private static final float[][] GLOW_COORDS = new float[][] {
			UtilsRendering.getCoordsFromAABB(new AABB(0.00D, 0.6185D, 0.00D, 1.00D, 1.0D, 1.00D)),
			UtilsRendering.getCoordsFromAABB(new AABB(0.00D, 0.0D, 0.00D, 1.00D, 0.365D, 1.00D)),
			UtilsRendering.getCoordsFromAABB(new AABB(0.00D, 0.00D, 0.6185D, 1.00D, 1.00D, 1.0D)),
			UtilsRendering.getCoordsFromAABB(new AABB(0.00D, 0.00D, 0.0D, 1.00D, 1.00D, 0.365D)),
			UtilsRendering.getCoordsFromAABB(new AABB(0.00D, 0.00D, 0.00D, 0.365D, 1.00D, 1.00D)),
			UtilsRendering.getCoordsFromAABB(new AABB(0.6185D, 0.00D, 0.00D, 1.0D, 1.00D, 1.00D)) };

	private static final float[][] BARS_COORDS = new float[][] {
			UtilsRendering.getCoordsFromAABB(new AABB(0.00D, 0.585D, 0.00D, 1.00D, 1.0D, 1.00D)),
			UtilsRendering.getCoordsFromAABB(new AABB(0.00D, 0.0D, 0.00D, 1.00D, 0.415D, 1.00D)),
			UtilsRendering.getCoordsFromAABB(new AABB(0.00D, 0.00D, 0.585D, 1.00D, 1.00D, 1.0D)),
			UtilsRendering.getCoordsFromAABB(new AABB(0.00D, 0.00D, 0.0D, 1.00D, 1.00D, 0.415D)),
			UtilsRendering.getCoordsFromAABB(new AABB(0.00D, 0.00D, 0.00D, 0.415D, 1.00D, 1.00D)),
			UtilsRendering.getCoordsFromAABB(new AABB(0.585D, 0.00D, 0.00D, 1.0D, 1.00D, 1.00D)) };

	public RendererPatternMonitor(Context context) {
		super(context);
	}

	@Override
	public void render(TilePatternMonitor tile, float ticks, PoseStack matrix, MultiBufferSource buffer, int light,
			int overlay) {
		BlockState state = tile.getBlockState();
		if (state.hasProperty(BlockLightableMachine.LIT) && state.getValue(BlockLightableMachine.LIT)) {
			matrix.pushPose();

			TextureAtlasSprite holoGrid = ClientRegister.CACHED_TEXTUREATLASSPRITES
					.get(ClientRegister.TEXTURE_HOLO_GRID);
			float[] holo_uv = { holoGrid.getU0(), holoGrid.getU1(), holoGrid.getV0(), holoGrid.getV1() };
			float[] holoColor = UtilsRendering.getColorArray(UtilsRendering.TEXT_BLUE);
			holoColor[3] = 0.2F;

			TextureAtlasSprite holoGlow = ClientRegister.CACHED_TEXTUREATLASSPRITES
					.get(ClientRegister.TEXTURE_HOLO_GLOW);
			float[] glow_uv = { holoGlow.getU0(), holoGlow.getU1(), holoGlow.getV0(), holoGlow.getV1() };
			float[] glowColor = UtilsRendering.getColorArray(UtilsRendering.TEXT_BLUE);
			float propAlpha = (float) Math.abs(Math.cos((double) (getGameTime() % 80) / 80.0D * Math.PI));
			if (propAlpha < 0.1F)
				propAlpha = 0.1F;
			glowColor[3] = propAlpha;

			TextureAtlasSprite holoBars = ClientRegister.CACHED_TEXTUREATLASSPRITES
					.get(ClientRegister.TEXTURE_HOLO_PATTERN_MONITOR);
			float[] barsColor = UtilsRendering.getColorArray(UtilsRendering.TEXT_BLUE);

			Matrix4f matrix4f = matrix.last().pose();
			Matrix3f matrix3f = matrix.last().normal();

			VertexConsumer builder = buffer.getBuffer(Sheets.translucentCullBlockSheet());

			Direction horizontalFacing = tile.getFacing();
			VerticalFacing vertical = tile.getBlockState().getValue(OverdriveBlockStates.VERTICAL_FACING);
			Direction verticalFacing = vertical.mapped;
			Direction facing;
			if (verticalFacing == null) {
				facing = horizontalFacing;
			} else {
				facing = verticalFacing;
				float rot = horizontalFacing.toYRot();
				if (facing == Direction.UP) {
					if (rot == 90.0F) {
						holoBars = ClientRegister.CACHED_TEXTUREATLASSPRITES
								.get(ClientRegister.TEXTURE_HOLO_PATTERN_MONITOR_90);
					} else if (rot == 180.0F) {
						holoBars = ClientRegister.CACHED_TEXTUREATLASSPRITES
								.get(ClientRegister.TEXTURE_HOLO_PATTERN_MONITOR_180);
					} else if (rot == 270.0F) {
						holoBars = ClientRegister.CACHED_TEXTUREATLASSPRITES
								.get(ClientRegister.TEXTURE_HOLO_PATTERN_MONITOR_270);
					}
				} else {
					if (rot == 90.0F) {
						holoBars = ClientRegister.CACHED_TEXTUREATLASSPRITES
								.get(ClientRegister.TEXTURE_HOLO_PATTERN_MONITOR_90);
					} else if (rot == 0.0F) {
						holoBars = ClientRegister.CACHED_TEXTUREATLASSPRITES
								.get(ClientRegister.TEXTURE_HOLO_PATTERN_MONITOR_180);
					} else if (rot == 270.0F) {
						holoBars = ClientRegister.CACHED_TEXTUREATLASSPRITES
								.get(ClientRegister.TEXTURE_HOLO_PATTERN_MONITOR_270);
					}
				}

			}

			float[] bars_uv = { holoBars.getU0(), holoBars.getU1(), holoBars.getV0(), holoBars.getV1() };

			switch (facing) {
			case DOWN:
				UtilsRendering.renderBottomOfBox(builder, BARS_COORDS[0], barsColor, bars_uv, matrix4f, matrix3f, 255,
						OverlayTexture.NO_OVERLAY);
				UtilsRendering.renderBottomOfBox(builder, GLOW_COORDS[0], glowColor, glow_uv, matrix4f, matrix3f, 255,
						OverlayTexture.NO_OVERLAY);
				UtilsRendering.renderBottomOfBox(builder, GRID_COORDS[0], holoColor, holo_uv, matrix4f, matrix3f, 255,
						OverlayTexture.NO_OVERLAY);
				break;
			case UP:
				UtilsRendering.renderTopOfBox(builder, BARS_COORDS[1], barsColor, bars_uv, matrix4f, matrix3f, 255,
						OverlayTexture.NO_OVERLAY);
				UtilsRendering.renderTopOfBox(builder, GLOW_COORDS[1], glowColor, glow_uv, matrix4f, matrix3f, 255,
						OverlayTexture.NO_OVERLAY);
				UtilsRendering.renderTopOfBox(builder, GRID_COORDS[1], holoColor, holo_uv, matrix4f, matrix3f, 255,
						OverlayTexture.NO_OVERLAY);
				break;
			case NORTH:
				UtilsRendering.renderNorthOfBox(builder, BARS_COORDS[2], barsColor, bars_uv, matrix4f, matrix3f, 255,
						OverlayTexture.NO_OVERLAY);
				UtilsRendering.renderNorthOfBox(builder, GLOW_COORDS[2], glowColor, glow_uv, matrix4f, matrix3f, 255,
						OverlayTexture.NO_OVERLAY);
				UtilsRendering.renderNorthOfBox(builder, GRID_COORDS[2], holoColor, holo_uv, matrix4f, matrix3f, 255,
						OverlayTexture.NO_OVERLAY);
				break;
			case SOUTH:
				UtilsRendering.renderSouthOfBox(builder, BARS_COORDS[3], barsColor, bars_uv, matrix4f, matrix3f, 255,
						OverlayTexture.NO_OVERLAY);
				UtilsRendering.renderSouthOfBox(builder, GLOW_COORDS[3], glowColor, glow_uv, matrix4f, matrix3f, 255,
						OverlayTexture.NO_OVERLAY);
				UtilsRendering.renderSouthOfBox(builder, GRID_COORDS[3], holoColor, holo_uv, matrix4f, matrix3f, 255,
						OverlayTexture.NO_OVERLAY);
				break;
			case EAST:
				UtilsRendering.renderEastOfBox(builder, BARS_COORDS[4], barsColor, bars_uv, matrix4f, matrix3f, 255,
						OverlayTexture.NO_OVERLAY);
				UtilsRendering.renderEastOfBox(builder, GLOW_COORDS[4], glowColor, glow_uv, matrix4f, matrix3f, 255,
						OverlayTexture.NO_OVERLAY);
				UtilsRendering.renderEastOfBox(builder, GRID_COORDS[4], holoColor, holo_uv, matrix4f, matrix3f, 255,
						OverlayTexture.NO_OVERLAY);
				break;
			case WEST:
				UtilsRendering.renderWestOfBox(builder, BARS_COORDS[5], barsColor, bars_uv, matrix4f, matrix3f, 255,
						OverlayTexture.NO_OVERLAY);
				UtilsRendering.renderWestOfBox(builder, GLOW_COORDS[5], glowColor, glow_uv, matrix4f, matrix3f, 255,
						OverlayTexture.NO_OVERLAY);
				UtilsRendering.renderWestOfBox(builder, GRID_COORDS[5], holoColor, holo_uv, matrix4f, matrix3f, 255,
						OverlayTexture.NO_OVERLAY);
				break;
			}

			matrix.popPose();

		}

	}

}
