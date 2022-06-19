package matteroverdrive.common.block.type;

import java.util.Arrays;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public enum TypeMachine {
	
	SOLAR_PANEL(Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), true), MATTER_DECOMPOSER(true), MATTER_RECYCLER(true),
	CHARGER(new VoxelShape[] { Shapes.block(), Shapes.block(), 
			VoxelShapes.CHARGER_N,
			VoxelShapes.CHARGER_S,
			VoxelShapes.CHARGER_E,
			VoxelShapes.CHARGER_W }, true),
	MICROWAVE(new VoxelShape[] { Shapes.block(), Shapes.block(),
			Shapes.box(0.0625D, 0.0D, 0.125D, 0.9375D, 0.625D, 0.875D),
			Shapes.box(0.0625D, 0.0D, 0.125D, 0.9375D, 0.625D, 0.875D),
			Shapes.box(0.125D, 0.0D, 0.0625D, 0.875D, 0.625D, 0.9375D),
			Shapes.box(0.125D, 0.0D, 0.0625D, 0.875D, 0.625D, 0.9375D) }, true),
	INSCRIBER(Shapes.box(0.015625D, 0.0D, 0.015625D, 0.984375D, 0.96875D, 0.984375D), true), TRANSPORTER(true),
	SPACETIME_ACCELERATOR(Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D), true),
	CHUNKLOADER(true), MATTER_ANALYZER(true), PATTERN_STORAGE(false), PATTERN_MONITOR(
			new VoxelShape[] { Shapes.box(0.0D, 0.6875D, 0.0D, 1.0D, 1.0D, 1.0D),
					Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 0.3125D, 1.0D),
					Shapes.box(0.0D, 0.0D, 0.6875D, 1.0D, 1.0D, 1.0D),
					Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.3125D),
					Shapes.box(0.6875D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D),
					Shapes.box(0.0D, 0.0D, 0.0D, 0.3125D, 1.0D, 1.0D)}, false),
	MATTER_REPLICATOR(true);

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
	
	public static class VoxelShapes {
		
		private static VoxelShape CHARGER_E;
		private static VoxelShape CHARGER_W;
		private static VoxelShape CHARGER_N;
		private static VoxelShape CHARGER_S;
		
		static {
			CHARGER_W = Shapes.box(0.0D, 0.0D, 0.09375D, 1.0D, 0.69375D, 0.90625D);
			CHARGER_W = Shapes.or(CHARGER_W, Shapes.box(0.09375D, 0.69375D, 0.29375D, 0.5D, 1.0D, 0.70625D));
			CHARGER_W = Shapes.or(CHARGER_W, Shapes.box(0.65625, 0.69375D, 0.40625D, 0.84375D, 1.0D, 0.59375D));
			
			CHARGER_E = Shapes.box(0.0D, 0.0D, 0.09375D, 1.0D, 0.69375D, 0.90625D);
			CHARGER_E = Shapes.or(CHARGER_E, Shapes.box(0.5D, 0.69375D, 0.29375D, 0.90625D, 1.0D, 0.70625D));
			CHARGER_E = Shapes.or(CHARGER_E, Shapes.box(0.15625D, 0.69375D, 0.40625D, 0.34375D, 1.0D, 0.59375D));
			
			CHARGER_S = Shapes.box(0.09375D, 0.0D, 0.0D, 0.90625D, 0.69375D, 1.0D);
			CHARGER_S = Shapes.or(CHARGER_S, Shapes.box(0.29375D, 0.69375D, 0.09375D, 0.70625D, 1.0D, 0.5D));
			CHARGER_S = Shapes.or(CHARGER_S, Shapes.box(0.40625D, 0.69375D, 0.65625, 0.59375D, 1.0D, 0.84375D));
			
			CHARGER_N = Shapes.box(0.09375D, 0.0D, 0.0D, 0.90625D, 0.69375D, 1.0D);
			CHARGER_N = Shapes.or(CHARGER_N, Shapes.box(0.29375D, 0.69375D, 0.5D, 0.70625D, 1.0D, 0.90625D));
			CHARGER_N = Shapes.or(CHARGER_N, Shapes.box(0.40625D, 0.69375D, 0.15625D, 0.59375, 1.0D, 0.34375D));
		}
		
	}

}
