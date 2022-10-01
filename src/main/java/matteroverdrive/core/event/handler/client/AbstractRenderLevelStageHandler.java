package matteroverdrive.core.event.handler.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;

public abstract class AbstractRenderLevelStageHandler {

	public AbstractRenderLevelStageHandler() {

	}

	public abstract void handleRendering(Minecraft minecraft, LevelRenderer renderer, PoseStack matrix,
			Matrix4f projMatrix, float partialTick, long startNano);
	
	public abstract boolean isStageCorrect(Stage stage);

}
