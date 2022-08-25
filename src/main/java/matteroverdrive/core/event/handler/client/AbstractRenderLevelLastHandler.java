package matteroverdrive.core.event.handler.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;

public abstract class AbstractRenderLevelLastHandler {

	public AbstractRenderLevelLastHandler() {

	}

	public abstract void handleRendering(Minecraft minecraft, LevelRenderer renderer, PoseStack matrix,
			Matrix4f projMatrix, float partialTick, long startNano);

}
