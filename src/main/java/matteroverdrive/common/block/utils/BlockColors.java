package matteroverdrive.common.block.utils;

import matteroverdrive.core.utils.UtilsRendering;

public enum BlockColors {

	RED(255, 0, 0, 1), GREEN(0, 255, 0, 1), BLUE(0, 0, 255, 1), BLACK(0, 0, 0, 1), BROWN(165, 42, 42, 1),
	PURPLE(128, 0, 128, 1), CYAN(0, 255, 255, 1), LIGHT_GREY(211, 211, 211, 1), DARK_GREY(169, 169, 169, 1),
	PINK(255, 192, 203, 1), MAGENTA(255, 0, 255, 1), LIME_GREEN(50, 205, 50, 1), YELLOW(255, 255, 0, 1),
	LIGHT_BLUE(135, 206, 250, 1), ORANGE(255, 165, 0, 1), WHITE(255, 255, 255, 1);

	public final int color;

	private BlockColors(int r, int g, int b, int a) {
		color = UtilsRendering.getRGBA(a, r, g, b);
	}

}
