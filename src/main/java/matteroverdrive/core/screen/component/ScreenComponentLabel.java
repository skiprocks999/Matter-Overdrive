package matteroverdrive.core.screen.component;

import java.awt.Rectangle;
import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.core.screen.IScreenWrapper;
import matteroverdrive.core.screen.component.utils.ScreenComponent;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ScreenComponentLabel extends ScreenComponent {

	private final Supplier<Component> component;
	private final int fontColor;

	public ScreenComponentLabel(final IScreenWrapper gui, final int x, final int y, final int[] screenNumbers,
			final Component component, final int color) {
		this(gui, x, y, screenNumbers, () -> component, color);
	}
	
	public ScreenComponentLabel(final IScreenWrapper gui, final int x, final int y, final int[] screenNumbers,
			final Supplier<Component> component, final int color) {
		super(new ResourceLocation(""), gui, x, y, screenNumbers);
		this.component = component;
		fontColor = color;
	}

	@Override
	public Rectangle getBounds(int guiWidth, int guiHeight) {
		return new Rectangle(0, 0, 0, 0);
	}

	@Override
	public void renderBackground(PoseStack stack, int xAxis, int yAxis, int guiWidth, int guiHeight) {
		Font font = gui.getFontRenderer();
		font.draw(stack, component.get(), guiWidth + this.xLocation, guiHeight + this.yLocation, fontColor);

	}

}
