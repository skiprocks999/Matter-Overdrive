package matteroverdrive.core.screen.component;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.References;
import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.utils.OverdriveScreenComponent;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.resources.ResourceLocation;

public class ScreenComponentHotbarBar extends OverdriveScreenComponent {

	private static final int HEIGHT = 5;
	private static final int WIDTH = 169;

	public ScreenComponentHotbarBar(final GenericScreen<?> gui, final int x, final int y, final int[] screenNumbers) {
		super(new ResourceLocation(References.ID + ":textures/gui/base/hotbar_bar.png"), gui, x, y, 
				WIDTH, HEIGHT, screenNumbers);
	}

	@Override
	public void renderBackground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		UtilsRendering.bindTexture(resource);
		blit(stack, this.x, this.y, 0, 0, this.width, this.height);
	}

}
