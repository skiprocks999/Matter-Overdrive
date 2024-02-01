package matteroverdrive.client.render.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import matteroverdrive.client.ClientReferences.AtlasTextures;
import matteroverdrive.client.ClientReferences.Colors;
import matteroverdrive.client.ClientRegister;
import matteroverdrive.client.render.tile.utils.AbstractTileRenderer;
import matteroverdrive.common.block.OverdriveBlockStates;
import matteroverdrive.common.block.OverdriveBlockStates.VerticalFacing;
import matteroverdrive.common.tile.matter_network.TilePatternMonitor;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;

public class RendererPatternMonitor extends AbstractTileRenderer<TilePatternMonitor> {

	// DUNSEW
	private static final float[][] GRID_COORDS = new float[][]{
		UtilsRendering.getCoordsFromAABB(new AABB(0.0D, 0.685D, 0.0D, 1.0D, 1.0D, 1.0D)),
		UtilsRendering.getCoordsFromAABB(new AABB(0.0D, 0.0D, 0.0D, 1.0D, 0.315D, 1.0D)),
		UtilsRendering.getCoordsFromAABB(new AABB(0.0D, 0.0D, 0.685D, 1.0D, 1.0D, 1.0D)),
		UtilsRendering.getCoordsFromAABB(new AABB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.315D)),
		UtilsRendering.getCoordsFromAABB(new AABB(0.0D, 0.0D, 0.0D, 0.315D, 1.0D, 1.0D)),
		UtilsRendering.getCoordsFromAABB(new AABB(0.685D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D))};

	private static final float[][] GLOW_COORDS = new float[][]{
		UtilsRendering.getCoordsFromAABB(new AABB(0.00D, 0.6185D, 0.00D, 1.00D, 1.0D, 1.00D)),
		UtilsRendering.getCoordsFromAABB(new AABB(0.00D, 0.0D, 0.00D, 1.00D, 0.365D, 1.00D)),
		UtilsRendering.getCoordsFromAABB(new AABB(0.00D, 0.00D, 0.6185D, 1.00D, 1.00D, 1.0D)),
		UtilsRendering.getCoordsFromAABB(new AABB(0.00D, 0.00D, 0.0D, 1.00D, 1.00D, 0.365D)),
		UtilsRendering.getCoordsFromAABB(new AABB(0.00D, 0.00D, 0.00D, 0.365D, 1.00D, 1.00D)),
		UtilsRendering.getCoordsFromAABB(new AABB(0.6185D, 0.00D, 0.00D, 1.0D, 1.00D, 1.00D))};

	private static final float[][] BARS_COORDS = new float[][]{
		UtilsRendering.getCoordsFromAABB(new AABB(0.00D, 0.585D, 0.00D, 1.00D, 1.0D, 1.00D)),
		UtilsRendering.getCoordsFromAABB(new AABB(0.00D, 0.0D, 0.00D, 1.00D, 0.415D, 1.00D)),
		UtilsRendering.getCoordsFromAABB(new AABB(0.00D, 0.00D, 0.585D, 1.00D, 1.00D, 1.0D)),
		UtilsRendering.getCoordsFromAABB(new AABB(0.00D, 0.00D, 0.0D, 1.00D, 1.00D, 0.415D)),
		UtilsRendering.getCoordsFromAABB(new AABB(0.00D, 0.00D, 0.00D, 0.415D, 1.00D, 1.00D)),
		UtilsRendering.getCoordsFromAABB(new AABB(0.585D, 0.00D, 0.00D, 1.0D, 1.00D, 1.00D))};

	public RendererPatternMonitor(Context context) {
		super(context);
	}

	@Override
	public void render(TilePatternMonitor tile, float ticks, PoseStack matrix, MultiBufferSource buffer, int light,
										 int overlay) {
		BlockState state = tile.getBlockState();
		if (state.hasProperty(BlockStateProperties.LIT) && state.getValue(BlockStateProperties.LIT)) {
			matrix.pushPose();

			TextureAtlasSprite holoGrid = ClientRegister.CACHED_TEXTUREATLASSPRITES.get(AtlasTextures.HOLO_GRID);
			float[] holo_uv = {holoGrid.getU0(), holoGrid.getU1(), holoGrid.getV0(), holoGrid.getV1()};
			float[] holoColor = Colors.HOLO.getFloatArrModAlpha(0.2F);

			TextureAtlasSprite holoGlow = ClientRegister.CACHED_TEXTUREATLASSPRITES.get(AtlasTextures.HOLO_GLOW);
			float[] glow_uv = {holoGlow.getU0(), holoGlow.getU1(), holoGlow.getV0(), holoGlow.getV1()};

			float propAlpha = (float) Math.abs(Math.cos((double) (getGameTime() % 80) / 80.0D * Math.PI));
			if (propAlpha < 0.1F)
				propAlpha = 0.1F;
			float[] glowColor = Colors.HOLO.getFloatArrModAlpha(propAlpha);

			TextureAtlasSprite holoBars = ClientRegister.CACHED_TEXTUREATLASSPRITES.get(AtlasTextures.HOLO_PATTERN_MONITOR);
			float[] barsColor = Colors.HOLO.getFloatArr();

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
						holoBars = ClientRegister.CACHED_TEXTUREATLASSPRITES.get(AtlasTextures.HOLO_PATTERN_MONITOR_90);
					} else if (rot == 180.0F) {
						holoBars = ClientRegister.CACHED_TEXTUREATLASSPRITES.get(AtlasTextures.HOLO_PATTERN_MONITOR_180);
					} else if (rot == 270.0F) {
						holoBars = ClientRegister.CACHED_TEXTUREATLASSPRITES.get(AtlasTextures.HOLO_PATTERN_MONITOR_270);
					}
				} else {
					if (rot == 90.0F) {
						holoBars = ClientRegister.CACHED_TEXTUREATLASSPRITES.get(AtlasTextures.HOLO_PATTERN_MONITOR_90);
					} else if (rot == 0.0F) {
						holoBars = ClientRegister.CACHED_TEXTUREATLASSPRITES.get(AtlasTextures.HOLO_PATTERN_MONITOR_180);
					} else if (rot == 270.0F) {
						holoBars = ClientRegister.CACHED_TEXTUREATLASSPRITES.get(AtlasTextures.HOLO_PATTERN_MONITOR_270);
					}
				}
			}

			float[] bars_uv = {holoBars.getU0(), holoBars.getU1(), holoBars.getV0(), holoBars.getV1()};

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

		updateOrderTally(tile, matrix);
	}

	public static void updateOrderTally(TilePatternMonitor tile, PoseStack matrix) {
		Direction horizontalFacing = tile.getFacing();
		VerticalFacing vertical = tile.getBlockState().getValue(OverdriveBlockStates.VERTICAL_FACING);
		Direction verticalFacing = vertical.mapped;
		Direction facing;
		if (verticalFacing == null) {
			facing = horizontalFacing;
		} else {
			facing = verticalFacing;
		}

		// Render number of orders in front of monitor.

		int numLocalOrders = tile.getNumOrdersFromReplicators();

		if (numLocalOrders > 0) {
			String orderString = String.format("%d", numLocalOrders);

			Minecraft instance = Minecraft.getInstance();

			matrix.pushPose();

			matrix.scale(0.075f, 0.075f, 0.075f);

			matrix.mulPose(Vector3f.ZP.rotationDegrees(180));

			switch (facing) {
				case NORTH:
					matrix.translate(-9.0f, -10.5f, 6.5f);

					break;
				case SOUTH:
					matrix.translate(-4.0f, -10.0f, 6.5f);

					matrix.mulPose(Vector3f.YP.rotationDegrees(180));

					break;
				case EAST:
					matrix.translate(-6.5f, -10.0f, 9.0f);

					matrix.mulPose(Vector3f.YP.rotationDegrees(90));

					break;
				case WEST:
					matrix.translate(-6.5f, -10.0f, 4.0f);

					matrix.mulPose(Vector3f.YP.rotationDegrees(270));

					break;
			}

			instance.font.draw(matrix, orderString, 0f, 0f, Colors.HOLO.getColor());

	//		System.out.println("At the end of the font draw " + tile);

			matrix.popPose();
		}
	}
}
