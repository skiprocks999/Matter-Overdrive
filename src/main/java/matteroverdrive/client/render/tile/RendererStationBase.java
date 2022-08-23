package matteroverdrive.client.render.tile;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import matteroverdrive.ReferencesClient;
import matteroverdrive.client.render.shaders.MORenderTypes;
import matteroverdrive.common.tile.station.BaseStationTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.util.Mth;

import java.awt.*;

import static org.lwjgl.opengl.GL11.GL_ONE;

public class RendererStationBase<T extends BaseStationTile> implements BlockEntityRenderer<T> {

  public RendererStationBase(BlockEntityRenderDispatcher rendererDispatcherIn) {
    super();
  }

  private void drawHoloLights(PoseStack stack, MultiBufferSource bufferIn, T tile, double x, double y, double z) {
    RenderSystem.disableCull();
    RenderSystem.depthMask(false);


    float height = 5.5f * (1f / 16f);
    float hologramHeight = 1;
    float offset = 2f * (1f / 16f);
    float size = 14f * (1f / 16f);
    float topSize = 2 - 1;

    stack.pushPose();
    stack.translate(0,  height, 0);

    VertexConsumer consumer = bufferIn.getBuffer(MORenderTypes.BASE_STATION);

    Color color;
    if (tile.isUsableByPlayer(Minecraft.getInstance().player)) {
      color = ReferencesClient.Colors.HOLO;
    } else {
      color = ReferencesClient.Colors.HOLO_RED;
    }

    var red = color.getRed() / 255f;
    var green = color.getGreen() / 255f;
    var blue = color.getBlue() / 255f;

    var hologramTop = height + hologramHeight;

    Matrix4f matrix = stack.last().pose();

    this.addVertex(consumer, matrix, offset, 0f, offset,1, 1, red, green, blue, 1.0f);
    this.addVertex(consumer, matrix,-topSize, hologramTop, -topSize,1, 0, red, green, blue, 1.0f);
    this.addVertex(consumer, matrix,size + topSize, hologramTop, -topSize,0, 0, red, green, blue, 1.0f);
    this.addVertex(consumer, matrix, size, 0f, offset,0, 1, red, green, blue, 1.0f);

    this.addVertex(consumer, matrix,size, 0f, offset,1, 1, red, green, blue, 1.0f);
    this.addVertex(consumer, matrix,size + topSize, hologramTop, -topSize,1, 0, red, green, blue, 1.0f);
    this.addVertex(consumer, matrix,size + topSize, hologramTop, 1 + topSize,0, 0, red, green, blue, 1.0f);
    this.addVertex(consumer, matrix,size, 0f, size,0, 1, red, green, blue, 1.0f);

    this.addVertex(consumer, matrix, size, 0f, size,1, 1, red, green, blue, 1.0f);
    this.addVertex(consumer, matrix,size + topSize, hologramTop, 1 + topSize,1, 0, red, green, blue, 1.0f);
    this.addVertex(consumer, matrix,-topSize, hologramTop, 1 + topSize,0, 0, red, green, blue, 1.0f);
    this.addVertex(consumer, matrix,offset, 0f, size,0, 1, red, green, blue, 1.0f);

    this.addVertex(consumer, matrix, offset, 0f, size,1, 1, red, green, blue, 1.0f);
    this.addVertex(consumer, matrix,-topSize, hologramTop, 1 + topSize,1, 0, red, green, blue, 1.0f);
    this.addVertex(consumer, matrix,-topSize, hologramTop, -topSize,0, 0, red, green, blue, 1.0f);
    this.addVertex(consumer, matrix,offset, 0f, offset,0, 1, red, green, blue, 1.0f);
    RenderSystem.enableCull();
    RenderSystem.depthMask(false);
    stack.popPose();
  }

  /**
   *
   * This method was created due to data being sent over to the vertex consumer in the wrong order.
   *
   * To try and debug or fix the problems.
   *
   * @param consumer
   * @param matrix
   * @param x coordinates
   * @param y coordinates
   * @param z coordinates
   * @param ux uv x position 0-1
   * @param uy uv y position 0-1
   * @param red color 0-1
   * @param green color 0-1
   * @param blue color 0-1
   * @param alpha color 0-1
   */
  private void addVertex(VertexConsumer consumer, Matrix4f matrix, float x, float y, float z, float ux, float uy, float red, float green, float blue, float alpha) {
    consumer.vertex(matrix, x, y, z).uv(ux, uy).color(red, green, blue, alpha).endVertex();
  }


  public void drawHoloText(PoseStack stack, T tile, double x, double y, double z, float partialTicks) {
    LocalPlayer player = Minecraft.getInstance().player;
    if (!tile.isUsableByPlayer(player)) {
      stack.pushPose();
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(GL_ONE, GL_ONE);
      stack.translate( 0.5,  0.5,  0.5);
      stack.mulPose(Vector3f.YP.rotationDegrees(180));
      float playerPosX = (float) Mth.clampedLerp((float) player.xo, (float) player.getX(), partialTicks);
      float playerPosZ = (float) Mth.clampedLerp((float) player.zo, (float) player.getZ(), partialTicks);
      float angle = (float) Math.toDegrees(Math.atan2(playerPosX - (tile.getBlockPos().getX() + 0.5), playerPosZ - (tile.getBlockPos().getZ() + 0.5)) + Math.PI);
      stack.mulPose(Vector3f.YP.rotationDegrees(angle));

      RenderSystem.disableCull();

      stack.mulPose(Vector3f.XP.rotationDegrees(180));

      stack.scale(0.02f, 0.02f, 0.02f);
      String[] info = "Access Denied".split(" ");
      for (int i = 0; i < info.length; i++) {
        int width = Minecraft.getInstance().font.width(info[i]);
        stack.pushPose();
        stack.translate(-width / 2, -32, 0);
        Minecraft.getInstance().font.draw(stack, info[i], 0, i * 10, ReferencesClient.Colors.HOLO_RED.getRGB());
        stack.popPose();
      }

      RenderSystem.disableBlend();
      RenderSystem.enableCull();
      stack.popPose();
    }
  }

  public void drawAdditional(PoseStack stack, MultiBufferSource bufferIn, T tile, double x, double y, double z, float partialTicks) {}

  @Override
  public void render(T tile, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
    poseStack.pushPose();
    drawHoloLights(poseStack, buffer, tile, tile.getBlockPos().getX(), tile.getBlockPos().getY(), tile.getBlockPos().getZ());
    drawHoloText(poseStack, tile, tile.getBlockPos().getX(), tile.getBlockPos().getY(), tile.getBlockPos().getZ(), partialTicks);
    drawAdditional(poseStack, buffer, tile, tile.getBlockPos().getX(), tile.getBlockPos().getY(), tile.getBlockPos().getZ(), partialTicks);
    poseStack.popPose();
  }

}
