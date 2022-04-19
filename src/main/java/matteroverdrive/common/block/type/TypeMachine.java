package matteroverdrive.common.block.type;

import java.util.Arrays;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public enum TypeMachine {

	solar_panel(Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D)),
	matter_decomposer;

	// DUNSEW
	public final VoxelShape[] shapes = new VoxelShape[6];
	public final boolean hasCustomAABB;

	private TypeMachine() {
		hasCustomAABB = false;
	}

	private TypeMachine(VoxelShape allDirs) {
		hasCustomAABB = true;
		Arrays.fill(shapes, allDirs);
	}

	public VoxelShape getShape(Direction dir) {
		return shapes[dir.ordinal()];
	}

}
