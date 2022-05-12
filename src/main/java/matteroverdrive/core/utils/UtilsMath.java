/**
 * Methods taken from Matter Overdrive 1.12.2 Source Code
 * 
 * > randomSpherePoint
 * > easeIn
 * 
 * If you would like credit for these methods feel free to let me know
 * and I can add it accordingly
 * 
 */
package matteroverdrive.core.utils;

import java.util.Random;

import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

public class UtilsMath {

	public static boolean between(double var, double lower, double upper) {
		return var >= lower && var < upper;
	}

	public static int getDistanceBetween(BlockPos pos1, BlockPos pos2) {
		return (int) Math.ceil(Mth.sqrt((float) pos1.distSqr(pos2)));
	}

	public static Vector3f randomSpherePoint(double x0, double y0, double z0, Vector3d radius, Random rand) {
		double u = rand.nextDouble();
		double v = rand.nextDouble();
		double theta = 2 * Math.PI * u;
		double phi = Math.acos(2 * v - 1);
		double x = x0 + (radius.x * Math.sin(phi) * Math.cos(theta));
		double y = y0 + (radius.y * Math.sin(phi) * Math.sin(theta));
		double z = z0 + (radius.z * Math.cos(phi));
		return new Vector3f((float) x, (float) y, (float) z);
	}
	
    public static double easeIn(double time, double fromValue, double toValue, double maxTime) {
        return toValue * (time /= maxTime) * time * time * time + fromValue;
    }

}
