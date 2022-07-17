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

import matteroverdrive.MatterOverdrive;
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
	
	public static Vector3f randomCirclePoint(float radius) {
		double u = MatterOverdrive.RANDOM.nextDouble();
		double theta = 2 * Math.PI * u;
		double x = (radius * Math.sin(theta));
		double z = (radius * Math.cos(theta));
		return new Vector3f((float) x, 0, (float) z);
	}
	
	public static Quaternion vec4FToQuaternion(Vector4f vector) {
		return new Quaternion(vector.x(), vector.y(), vector.z(), vector.w());
	}
	
	//Based on https://github.com/LWJGL/lwjgl/blob/master/src/java/org/lwjgl/util/vector/Matrix4f.java
	public static Vector4f transformMatrixWithVector(Matrix4f left, Vector4f right, @Nullable Vector4f destination) {
		if(destination == null) {
			destination = new Vector4f();
		}
		
		destination.setX(left.m00 * right.x() + left.m10 * right.y() + left.m20 * right.z() + left.m30 * right.w());
		destination.setY(left.m01 * right.x() + left.m11 * right.y() + left.m21 * right.z() + left.m31 * right.w());
		destination.setZ(left.m02 * right.x() + left.m12 * right.y() + left.m22 * right.z() + left.m32 * right.w());
		destination.setW(left.m03 * right.x() + left.m13 * right.y() + left.m23 * right.z() + left.m33 * right.w());
		
		return destination;
	}
	
	//From https://github.com/LWJGL/lwjgl/blob/master/src/java/org/lwjgl/util/vector/Matrix4f.java
	public static Matrix4f rotateMatrixAlongAxis(float radians, Vector3f axis, Matrix4f source, @Nullable Matrix4f dest) {
		return rotateMatrixAlongAxis((float) Math.cos(radians), (float) Math.sin(radians), axis, source, dest);
	}
	
	public static Matrix4f rotateMatrixAlongAxis(float cosine, float sine, Vector3f axis, Matrix4f source, @Nullable Matrix4f dest) {
		if (dest == null)
			dest = new Matrix4f();
		float oneminusc = 1.0f - cosine;
		float xy = axis.x()*axis.y();
		float yz = axis.y()*axis.z();
		float xz = axis.x()*axis.z();
		float xs = axis.x()*sine;
		float ys = axis.y()*sine;
		float zs = axis.z()*sine;

		float f00 = axis.x()*axis.x()*oneminusc+cosine;
		float f01 = xy*oneminusc+zs;
		float f02 = xz*oneminusc-ys;
		// n[3] not used
		float f10 = xy*oneminusc-zs;
		float f11 = axis.y()*axis.y()*oneminusc+cosine;
		float f12 = yz*oneminusc+xs;
		// n[7] not used
		float f20 = xz*oneminusc+ys;
		float f21 = yz*oneminusc-xs;
		float f22 = axis.z()*axis.z()*oneminusc+cosine;

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

}
