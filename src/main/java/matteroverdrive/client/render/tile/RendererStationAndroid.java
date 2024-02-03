package matteroverdrive.client.render.tile;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import matteroverdrive.client.ClientReferences.Colors;
import matteroverdrive.client.render.shaders.MORenderTypes;
import matteroverdrive.client.render.tile.utils.RendererStationBase;
import matteroverdrive.common.tile.station.TileAndroidStation;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class RendererStationAndroid extends RendererStationBase<TileAndroidStation> {

  private final PlayerModel<?> playerModel;

  public RendererStationAndroid(Context rendererContext) {
    super(rendererContext);
    this.playerModel = new PlayerModel<>(rendererContext.bakeLayer(ModelLayers.PLAYER), false);
    this.playerModel.young = false;
  }

  @Override
  public void drawAdditional(PoseStack stack, MultiBufferSource bufferIn, TileAndroidStation tile, double x, double y, double z, float partialTicks) {
    Player player = Minecraft.getInstance().player;
    if (player != null && tile.isUsableByPlayer(player)) {
      stack.pushPose();
      stack.translate(0.5,  2,  0.5);
      stack.mulPose(Vector3f.XP.rotationDegrees(180));
      
      float[] holoArr = UtilsRendering.getColorArray(Colors.HOLO.getColor());
      
      UtilsRendering.setShaderColor(Colors.HOLO.getFloatArrModAlpha(0.625f));
      float playerPosX = Mth.clampedLerp((float) player.xo, (float) player.position().x, partialTicks);
      float playerPosZ = Mth.clampedLerp((float) player.zo, (float) player.position().z, partialTicks);
      float angle = (float) Math.toDegrees(Math.atan2(playerPosX - (tile.getBlockPos().getX() + 0.5), playerPosZ - (tile.getBlockPos().getZ() + 0.5)) + Math.PI);

      stack.mulPose(Vector3f.YP.rotationDegrees(180));
      stack.mulPose(Vector3f.YN.rotationDegrees(angle));

      VertexConsumer consumer = bufferIn.getBuffer(MORenderTypes.ANDROID_STATION);

      var modelStack = RenderSystem.getModelViewStack();
      modelStack.pushPose();

      modelStack.mulPoseMatrix(stack.last().pose());

      RenderSystem.applyModelViewMatrix();

      playerModel.renderToBuffer(new PoseStack(), consumer, 0, OverlayTexture.NO_OVERLAY, Colors.HOLO.getRFloat(), Colors.HOLO.getGFloat(), Colors.HOLO.getBFloat(), 0.625F);

      // This fixes mojank ;) we don't actually use it. This forces a upload to buffer so the values are not lost.
      bufferIn.getBuffer(RenderType.translucentMovingBlock());

      modelStack.popPose();
      RenderSystem.applyModelViewMatrix();
      stack.popPose();
    }
  }

}
