package matteroverdrive.compatibility.jei.utils.gui.arrows.animated;

import matteroverdrive.compatibility.jei.utils.gui.arrows.stat.ArrowRightStaticWrapper;
import mezz.jei.api.gui.drawable.IDrawableAnimated.StartDirection;

public class ArrowRightAnimatedWrapper extends ArrowAnimatedWrapper {

	public ArrowRightAnimatedWrapper(int xStart, int yStart) {
		super(JeiTexture.PROGRESS_BARS, xStart, yStart, 78, 0, 22, 15, new ArrowRightStaticWrapper(xStart, yStart));
	}

	@Override
	public StartDirection getStartDirection() {
		return StartDirection.LEFT;
	}

}
