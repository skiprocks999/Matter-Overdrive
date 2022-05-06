package matteroverdrive.common.block.type;

import java.util.Arrays;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public enum TypeMachine {

	solar_panel(Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), true), matter_decomposer(true), matter_recycler(true),
	charger(new VoxelShape[] { Shapes.block(), Shapes.block(), 
			Shapes.box(0.09375D, 0.0D, 0.0D, 0.90625D, 1.0D, 1.0D),
			Shapes.box(0.09375D, 0.0D, 0.0D, 0.90625D, 1.0D, 1.0D),
			Shapes.box(0.0D, 0.0D, 0.09375D, 1.0D, 1.0D, 0.90625D),
			Shapes.box(0.0D, 0.0D, 0.09375D, 1.0D, 1.0D, 0.90625D) }, true),
	microwave(new VoxelShape[] { Shapes.block(), Shapes.block(), 
			Shapes.box(0.0625D, 0.0D, 0.125D, 0.9375D, 0.625D, 0.875D),
			Shapes.box(0.0625D, 0.0D, 0.125D, 0.9375D, 0.625D, 0.875D),
			Shapes.box(0.125D, 0.0D, 0.0625D, 0.875D, 0.625D, 0.9375D),
			Shapes.box(0.125D, 0.0D, 0.0625D, 0.875D, 0.625D, 0.9375D) }, true);

	// DUNSEW
	public VoxelShape[] shapes = new VoxelShape[6];
	public final boolean hasCustomAABB;

	public final boolean isRedstoneConnected;

	private TypeMachine(boolean isRedstoneConnected) {
		hasCustomAABB = false;
		this.isRedstoneConnected = isRedstoneConnected;
	}

	private TypeMachine(VoxelShape allDirs, boolean isRedstoneConnected) {
		hasCustomAABB = true;
		Arrays.fill(shapes, allDirs);
		this.isRedstoneConnected = isRedstoneConnected;
	}

	private TypeMachine(VoxelShape[] allDirs, boolean isRedstoneConnected) {
		hasCustomAABB = true;
		shapes = allDirs;
		this.isRedstoneConnected = isRedstoneConnected;
	}

	public VoxelShape getShape(Direction dir) {
		return shapes[dir.ordinal()];
	}

}
