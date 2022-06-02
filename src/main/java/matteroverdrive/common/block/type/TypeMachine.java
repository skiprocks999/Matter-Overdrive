package matteroverdrive.common.block.type;

import java.util.Arrays;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public enum TypeMachine {

	SOLAR_PANEL(Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), true), MATTER_DECOMPOSER(true), MATTER_RECYCLER(true),
	CHARGER(new VoxelShape[] { Shapes.block(), Shapes.block(), Shapes.box(0.09375D, 0.0D, 0.0D, 0.90625D, 1.0D, 1.0D),
			Shapes.box(0.09375D, 0.0D, 0.0D, 0.90625D, 1.0D, 1.0D),
			Shapes.box(0.0D, 0.0D, 0.09375D, 1.0D, 1.0D, 0.90625D),
			Shapes.box(0.0D, 0.0D, 0.09375D, 1.0D, 1.0D, 0.90625D) }, true),
	MICROWAVE(new VoxelShape[] { Shapes.block(), Shapes.block(),
			Shapes.box(0.0625D, 0.0D, 0.125D, 0.9375D, 0.625D, 0.875D),
			Shapes.box(0.0625D, 0.0D, 0.125D, 0.9375D, 0.625D, 0.875D),
			Shapes.box(0.125D, 0.0D, 0.0625D, 0.875D, 0.625D, 0.9375D),
			Shapes.box(0.125D, 0.0D, 0.0625D, 0.875D, 0.625D, 0.9375D) }, true),
	INSCRIBER(Shapes.box(0.015625D, 0.0D, 0.015625D, 0.984375D, 0.96875D, 0.984375D), true), TRANSPORTER(true),
	SPACETIME_ACCELERATOR(Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D), true),
	NETWORK_POWER_SUPPLY(true);

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

	public String id() {
		return this.toString().toLowerCase();
	}

}
