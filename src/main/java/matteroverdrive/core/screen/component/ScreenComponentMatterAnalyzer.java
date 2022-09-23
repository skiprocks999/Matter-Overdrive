package matteroverdrive.core.screen.component;

import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.client.ClientReferences.Colors;
import matteroverdrive.common.tile.matter_network.TileMatterAnalyzer;
import matteroverdrive.core.inventory.GenericInventoryTile;
import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.utils.OverdriveScreenComponent;
import matteroverdrive.core.utils.UtilsMath;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.item.Item;

public class ScreenComponentMatterAnalyzer extends OverdriveScreenComponent {

	private static final int NUM_BARS = 26;

	private final Random random;

	private float[] values = new float[NUM_BARS];

	public ScreenComponentMatterAnalyzer(GenericScreen<?> gui, int x, int y, int[] screenNumbers) {
		super(OverdriveTextures.TABLET_SCREEN, gui, x, y, 118, 48, screenNumbers);
		random = new Random();
	}

	@Override
	public void renderBackground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		UtilsRendering.bindTexture(resource.getTexture());
		blit(stack, x, y, width, height, 0, 0, width, height, width, height);
	}

	@Override
	public void renderForeground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		TileMatterAnalyzer analyzer = (TileMatterAnalyzer) ((GenericInventoryTile<?>)gui.getMenu()).getTile();
		if (analyzer != null && analyzer.isRunning()) {
			int color = Colors.HOLO.getColor();
			
			UtilsRendering.setShader(GameRenderer::getPositionTexShader);
			UtilsRendering.bindTexture(OverdriveTextures.WHITE.getTexture());
			UtilsRendering.setShaderColor(color);

			int seed = Item.getId(analyzer.scannedItemProp.get().getItem());
			random.setSeed((long) seed);
			int progress = (int) Math.ceil(26.0D * (analyzer.getProgress() / analyzer.getProcessingTime()));

			int marginsTop = 8;
			int marginsLeft = 7;
			int maxHeight = 32;

			for (int i = 0; i < NUM_BARS; i++) {
				float newValue = 0;

				if (i < progress) {
					double noiseValue = (UtilsMath.noise(0, 0.05f * i, seed * seed * 10000) + 1.0) / 2.0;
					double contrastFactor = 2;
					noiseValue = contrastFactor * (noiseValue - 0.5) + 0.5;
					noiseValue = Math.pow(Math.min(noiseValue, 1), 2);
					noiseValue = noiseValue * 0.8 + random.nextDouble() * 0.2;

					newValue = (float) noiseValue;
					int height = Math.round(values[i] * maxHeight);
					int x1 = marginsLeft + i * 4 + x;
					int y1 = maxHeight + marginsTop + y;
					int x2 = x1 + 2;
					int y2 = maxHeight - height + marginsTop + y;
					fill(stack, x1, y1, x2, y2, color);
					fill(stack, x1, y2 - 1, x2, y2 - 2, color);
				}

				values[i] = UtilsMath.lerpF(values[i], newValue, 0.05f);
			}

			UtilsRendering.resetShaderColor();

		}
	}

}
