package matteroverdrive.common.block;

import matteroverdrive.core.registers.IBulkRegistryObject;
import matteroverdrive.core.utils.UtilsRendering;

public enum OverdriveBlockColors implements IBulkRegistryObject {

	BLACK(30, 30, 30, 1), BLUE(0, 0, 255, 1), BROWN(139, 69, 19, 1), CYAN(0, 255, 255, 1), DARK_GREY(169, 169, 169, 1),
	GREEN(0, 128, 0, 1), LIGHT_BLUE(135, 206, 250, 1), LIGHT_GREY(211, 211, 211, 1), LIME_GREEN(0, 255, 0, 1),
	MAGENTA(255, 0, 255, 1), ORANGE(255, 165, 0, 1), PINK(255, 192, 203, 1), PURPLE(128, 0, 128, 1), RED(255, 0, 0, 1),
	WHITE(255, 255, 255, 1), YELLOW(255, 255, 0, 1);

	public final int color;

	private OverdriveBlockColors(int r, int g, int b, int a) {
		color = UtilsRendering.getRGBA(a, r, g, b);
	}

	@Override
	public String id() {
		return this.toString().toLowerCase();
	}

}
