package matteroverdrive.compatibility.jei.utils.gui.arrows.animated;

import matteroverdrive.compatibility.jei.utils.gui.ScreenObjectWrapper;
import matteroverdrive.core.screen.component.utils.ITexture;
import mezz.jei.api.gui.drawable.IDrawableAnimated.StartDirection;

public abstract class ArrowAnimatedWrapper extends ScreenObjectWrapper {

	private ScreenObjectWrapper STATIC_ARROW;

	public ArrowAnimatedWrapper(ITexture texture, int xStart, int yStart, int textX, int textY, int height, int width, ScreenObjectWrapper staticArrow) {
		super(texture, xStart, yStart, textX, textY, height, width);
		STATIC_ARROW = staticArrow;
	}

	public abstract StartDirection getStartDirection();

	public ScreenObjectWrapper getStaticArrow() {
		return STATIC_ARROW;
	}

}
