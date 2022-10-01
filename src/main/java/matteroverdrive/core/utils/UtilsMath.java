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

import javax.annotation.Nullable;

import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

public class UtilsMath {

	static final int p[] = new int[512], permutation[] = { 151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194,
			233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26,
			197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168,
			68, 175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220,
			105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208,
			89, 18, 169, 200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250,
			124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42,
			223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39,
			253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238,
			210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157,
			184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243,
			141, 128, 195, 78, 66, 215, 61, 156, 180 };

	public static final double TWO_PI = Math.PI * 2;

	public static boolean between(double var, double lower, double upper) {
		return var >= lower && var < upper;
	}

	public static int getDistanceBetween(BlockPos pos1, BlockPos pos2) {
		return (int) Math.ceil(Mth.sqrt((float) pos1.distSqr(pos2)));
	}

	public static Vector3f randomSpherePoint(double x0, double y0, double z0, Vector3d radius, Random rand) {
		double u = rand.nextDouble();
		double v = rand.nextDouble();
		double theta = TWO_PI * u;
		double phi = Math.acos(2 * v - 1);
		return new Vector3f((float) (x0 + (radius.x * Math.sin(phi) * Math.cos(theta))),
				(float) (y0 + (radius.y * Math.sin(phi) * Math.sin(theta))), (float) (z0 + (radius.z * Math.cos(phi))));
	}

	public static double easeIn(double time, double fromValue, double toValue, double maxTime) {
		return toValue * (time /= maxTime) * time * time * time + fromValue;
	}

	public static Vector3f randomSpherePoint(float radius, Random rand) {
		double u = rand.nextDouble();
		double v = rand.nextDouble();
		double theta = TWO_PI * u;
		double phi = Math.acos(2 * v - 1);
		return new Vector3f((float) (radius * Math.sin(phi) * Math.cos(theta)),
				(float) (radius * Math.sin(phi) * Math.sin(theta)), (float) (radius * Math.cos(phi)));
	}

	public static Vector3f randomCirclePoint(float radius, Random rand) {
		double u = rand.nextDouble();
		double theta = TWO_PI * u;

		return new Vector3f((float) ((radius * Math.sin(theta))), 0, (float) ((radius * Math.cos(theta))));
	}

	public static Quaternion vec4FToQuaternion(Vector4f vector) {
		return new Quaternion(vector.x(), vector.y(), vector.z(), vector.w());
	}

	// Based on
	// https://github.com/LWJGL/lwjgl/blob/master/src/java/org/lwjgl/util/vector/Matrix4f.java
	public static Vector4f transformMatrixWithVector(Matrix4f left, Vector4f right, @Nullable Vector4f destination) {
		if (destination == null) {
			destination = new Vector4f();
		}

		destination.setX(left.m00 * right.x() + left.m10 * right.y() + left.m20 * right.z() + left.m30 * right.w());
		destination.setY(left.m01 * right.x() + left.m11 * right.y() + left.m21 * right.z() + left.m31 * right.w());
		destination.setZ(left.m02 * right.x() + left.m12 * right.y() + left.m22 * right.z() + left.m32 * right.w());
		destination.setW(left.m03 * right.x() + left.m13 * right.y() + left.m23 * right.z() + left.m33 * right.w());

		return destination;
	}

	// From
	// https://github.com/LWJGL/lwjgl/blob/master/src/java/org/lwjgl/util/vector/Matrix4f.java
	public static Matrix4f rotateMatrixAlongAxis(float radians, Vector3f axis, Matrix4f source,
			@Nullable Matrix4f dest) {
		return rotateMatrixAlongAxis((float) Math.cos(radians), (float) Math.sin(radians), axis, source, dest);
	}

	public static Matrix4f rotateMatrixAlongAxis(float cosine, float sine, Vector3f axis, Matrix4f source,
			@Nullable Matrix4f dest) {
		if (dest == null)
			dest = new Matrix4f();
		float oneminusc = 1.0f - cosine;
		float xy = axis.x() * axis.y();
		float yz = axis.y() * axis.z();
		float xz = axis.x() * axis.z();
		float xs = axis.x() * sine;
		float ys = axis.y() * sine;
		float zs = axis.z() * sine;

		float f00 = axis.x() * axis.x() * oneminusc + cosine;
		float f01 = xy * oneminusc + zs;
		float f02 = xz * oneminusc - ys;
		// n[3] not used
		float f10 = xy * oneminusc - zs;
		float f11 = axis.y() * axis.y() * oneminusc + cosine;
		float f12 = yz * oneminusc + xs;
		// n[7] not used
		float f20 = xz * oneminusc + ys;
		float f21 = yz * oneminusc - xs;
		float f22 = axis.z() * axis.z() * oneminusc + cosine;

		float t00 = source.m00 * f00 + source.m10 * f01 + source.m20 * f02;
		float t01 = source.m01 * f00 + source.m11 * f01 + source.m21 * f02;
		float t02 = source.m02 * f00 + source.m12 * f01 + source.m22 * f02;
		float t03 = source.m03 * f00 + source.m13 * f01 + source.m23 * f02;
		float t10 = source.m00 * f10 + source.m10 * f11 + source.m20 * f12;
		float t11 = source.m01 * f10 + source.m11 * f11 + source.m21 * f12;
		float t12 = source.m02 * f10 + source.m12 * f11 + source.m22 * f12;
		float t13 = source.m03 * f10 + source.m13 * f11 + source.m23 * f12;
		dest.m20 = source.m00 * f20 + source.m10 * f21 + source.m20 * f22;
		dest.m21 = source.m01 * f20 + source.m11 * f21 + source.m21 * f22;
		dest.m22 = source.m02 * f20 + source.m12 * f21 + source.m22 * f22;
		dest.m23 = source.m03 * f20 + source.m13 * f21 + source.m23 * f22;
		dest.m00 = t00;
		dest.m01 = t01;
		dest.m02 = t02;
		dest.m03 = t03;
		dest.m10 = t10;
		dest.m11 = t11;
		dest.m12 = t12;
		dest.m13 = t13;

		return dest;
	}

	public static Vector3f blockPosToVector(BlockPos pos) {
		return new Vector3f(pos.getX(), pos.getY(), pos.getZ());
	}

	public static Vector3f moveToEdgeOfFaceAndCenter(Direction dir, BlockPos blockPos) {

		Vector3f pos = UtilsMath.blockPosToVector(blockPos);
		switch (dir) {
		case UP:
			pos.add(0.5F, 1.0F, 0.5F);
			return pos;
		case DOWN:
			pos.add(0.5F, 0.0F, 0.5F);
			return pos;
		case NORTH:
			pos.add(0.5F, 0.5F, 0.0F);
			return pos;
		case SOUTH:
			pos.add(0.5F, 0.5F, 1.0F);
			return pos;
		case EAST:
			pos.add(1.0F, 0.5F, 0.5F);
			return pos;
		case WEST:
			pos.add(0.0F, 0.5F, 0.5F);
			return pos;
		}

		return pos;
	}

	// JAVA REFERENCE IMPLEMENTATION OF IMPROVED NOISE - COPYRIGHT 2002 KEN PERLIN.
	static public double noise(double x, double y, double z) {
		int X = (int) Math.floor(x) & 255, // FIND UNIT CUBE THAT
				Y = (int) Math.floor(y) & 255, // CONTAINS POINT.
				Z = (int) Math.floor(z) & 255;
		x -= Math.floor(x); // FIND RELATIVE X,Y,Z
		y -= Math.floor(y); // OF POINT IN CUBE.
		z -= Math.floor(z);
		double u = fadeInternal(x), // COMPUTE FADE CURVES
				v = fadeInternal(y), // FOR EACH OF X,Y,Z.
				w = fadeInternal(z);
		int A = p[X] + Y, AA = p[A] + Z, AB = p[A + 1] + Z, // HASH INATES OF
				B = p[X + 1] + Y, BA = p[B] + Z, BB = p[B + 1] + Z; // THE 8 CUBE CORNERS,

		return lerpInternal(w, lerpInternal(v, lerpInternal(u, gradInternal(p[AA], x, y, z), // AND ADD
				gradInternal(p[BA], x - 1, y, z)), // BLENDED
				lerpInternal(u, gradInternal(p[AB], x, y - 1, z), // RESULTS
						gradInternal(p[BB], x - 1, y - 1, z))), // FROM 8
				lerpInternal(v, lerpInternal(u, gradInternal(p[AA + 1], x, y, z - 1), // CORNERS
						gradInternal(p[BA + 1], x - 1, y, z - 1)), // OF CUBE
						lerpInternal(u, gradInternal(p[AB + 1], x, y - 1, z - 1),
								gradInternal(p[BB + 1], x - 1, y - 1, z - 1))));
	}

	private static double fadeInternal(double total) {
		return total * total * total * (total * (total * 6 - 15) + 10);
	}

	private static double lerpInternal(double total, double from, double to) {
		return from + total * (to - from);
	}

	private static double gradInternal(int hash, double x, double y, double z) {
		int h = hash & 15; // CONVERT LO 4 BITS OF HASH CODE
		double u = h < 8 ? x : y, // INTO 12 GRADIENT DIRECTIONS.
				v = h < 4 ? y : h == 12 || h == 14 ? x : z;
		return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
	}

	public static double lerpD(double form, double to, double time) {
		double newTime = Mth.clamp(time, 0, 1);
		return (1 - newTime) * form + newTime * to;
	}

	public static float lerpF(float form, float to, float time) {
		float newTime = Mth.clamp(time, 0, 1);
		return (1 - newTime) * form + newTime * to;
	}

}
