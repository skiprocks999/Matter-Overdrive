package matteroverdrive.core.screen.component;

import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.utils.OverdriveScreenComponent;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public class ScreenComponentLabel extends OverdriveScreenComponent {

	private final Supplier<Component> component;
	private final int fontColor;

	public ScreenComponentLabel(final GenericScreen<?> gui, final int x, final int y, final int[] screenNumbers,
			final Component component, final int color) {
		this(gui, x, y, screenNumbers, () -> component, color);
	}

	public ScreenComponentLabel(final GenericScreen<?> gui, final int x, final int y, final int[] screenNumbers,
			final Supplier<Component> component, final int color) {
		super(OverdriveTextures.NONE, gui, x, y, 0, 0, screenNumbers);
		this.component = component;
		fontColor = color;
	}

	@Override
	public void renderBackground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		Font font = gui.getFontRenderer();
		font.draw(stack, component.get(), this.x, this.y, fontColor);
	}

}
