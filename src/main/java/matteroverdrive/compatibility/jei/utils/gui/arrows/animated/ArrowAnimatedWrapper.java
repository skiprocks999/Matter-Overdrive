package matteroverdrive.compatibility.jei.utils.gui.arrows.animated;

import matteroverdrive.compatibility.jei.utils.gui.ScreenObjectWrapper;
import mezz.jei.api.gui.drawable.IDrawableAnimated.StartDirection;
import net.minecraft.resources.ResourceLocation;

public abstract class ArrowAnimatedWrapper extends ScreenObjectWrapper {

	private ScreenObjectWrapper STATIC_ARROW;

	public ArrowAnimatedWrapper(ResourceLocation texture, int xStart, int yStart, int textX, int textY, int height, int width, ScreenObjectWrapper staticArrow) {
		super(texture, xStart, yStart, textX, textY, height, width);
		STATIC_ARROW = staticArrow;
	}

	public abstract StartDirection getStartDirection();

	public ScreenObjectWrapper getStaticArrow() {
		return STATIC_ARROW;
	}

}
