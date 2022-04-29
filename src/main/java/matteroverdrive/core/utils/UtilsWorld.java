package matteroverdrive.core.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

public class UtilsWorld {

	public static double distanceBetweenPositions(BlockPos a, BlockPos b) {
		return Mth.sqrt((float) (Mth.square(a.getX() - b.getX()) + Mth.square(a.getY() - b.getY())
				+ Mth.square(a.getZ() - b.getZ())));
	}

}
