package matteroverdrive.common.block.type;

import matteroverdrive.core.block.GenericMachineBlock;
import matteroverdrive.core.block.OverdriveBlockProperties;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public enum TypeMachine {

	SOLAR_PANEL(Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
			OverdriveBlockProperties.from(GenericMachineBlock.DEFAULT_MACHINE_PROPERTIES).redstoneConnectivity()
					.setCanBeWaterlogged().setHasFacing(false)),
	MATTER_DECOMPOSER(OverdriveBlockProperties.from(GenericMachineBlock.DEFAULT_MACHINE_PROPERTIES)
			.redstoneConnectivity().setCanBeLit(false).setHasFacing(false)),
	MATTER_RECYCLER(OverdriveBlockProperties.from(GenericMachineBlock.DEFAULT_MACHINE_PROPERTIES).redstoneConnectivity()
			.setCanBeLit(false).setHasFacing(false)),
	CHARGER(new VoxelShape[] { Shapes.block(), Shapes.block(), VoxelShapes.CHARGER_N, VoxelShapes.CHARGER_S,
			VoxelShapes.CHARGER_E, VoxelShapes.CHARGER_W },
			OverdriveBlockProperties.from(GenericMachineBlock.DEFAULT_MACHINE_PROPERTIES).redstoneConnectivity()
					.setHasFacing(false).setCanBeWaterlogged()),
	MICROWAVE(
			new VoxelShape[] { Shapes.block(), Shapes.block(),
					Shapes.box(0.0625D, 0.0D, 0.125D, 0.9375D, 0.625D, 0.875D),
					Shapes.box(0.0625D, 0.0D, 0.125D, 0.9375D, 0.625D, 0.875D),
					Shapes.box(0.125D, 0.0D, 0.0625D, 0.875D, 0.625D, 0.9375D),
					Shapes.box(0.125D, 0.0D, 0.0625D, 0.875D, 0.625D, 0.9375D) },
			OverdriveBlockProperties.from(GenericMachineBlock.DEFAULT_MACHINE_PROPERTIES).redstoneConnectivity()
					.setCanBeLit(false).setHasFacing(false).setCanBeWaterlogged()),
	INSCRIBER(Shapes.box(0.015625D, 0.0D, 0.015625D, 0.984375D, 0.96875D, 0.984375D),
			OverdriveBlockProperties.from(GenericMachineBlock.DEFAULT_MACHINE_PROPERTIES).redstoneConnectivity()
					.setHasFacing(false)),
	TRANSPORTER(OverdriveBlockProperties.from(GenericMachineBlock.DEFAULT_MACHINE_PROPERTIES).redstoneConnectivity()
			.setAlwaysLit().setHasFacing(false)),
	SPACETIME_ACCELERATOR(Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D),
			OverdriveBlockProperties.from(GenericMachineBlock.DEFAULT_MACHINE_PROPERTIES).redstoneConnectivity()
					.setHasFacing(false).setCanBeWaterlogged()),
	CHUNKLOADER(OverdriveBlockProperties.from(GenericMachineBlock.DEFAULT_MACHINE_PROPERTIES).redstoneConnectivity()),
	MATTER_ANALYZER(OverdriveBlockProperties.from(GenericMachineBlock.DEFAULT_MACHINE_PROPERTIES).redstoneConnectivity()
			.setCanBeLit(false).setCanBeWaterlogged().setHasFacing(false)),
	PATTERN_STORAGE(OverdriveBlockProperties.from(GenericMachineBlock.DEFAULT_MACHINE_PROPERTIES).redstoneConnectivity()
			.setCanBeWaterlogged().setHasFacing(false)),
	PATTERN_MONITOR(new VoxelShape[] { Shapes.box(0.0D, 0.6875D, 0.0D, 1.0D, 1.0D, 1.0D),
			Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 0.3125D, 1.0D), Shapes.box(0.0D, 0.0D, 0.6875D, 1.0D, 1.0D, 1.0D),
			Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.3125D), Shapes.box(0.6875D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D),
			Shapes.box(0.0D, 0.0D, 0.0D, 0.3125D, 1.0D, 1.0D) },
			OverdriveBlockProperties.from(GenericMachineBlock.DEFAULT_MACHINE_PROPERTIES).setCanBeLit(false)
					.setHasFacing(true).setCanBeWaterlogged()),
	MATTER_REPLICATOR(OverdriveBlockProperties.from(GenericMachineBlock.DEFAULT_MACHINE_PROPERTIES)
			.redstoneConnectivity().setCanBeLit(false).setCanBeWaterlogged().setHasFacing(false)),
	ANDROID_STATION(Shapes.box(0.0D,0.0D,0.0D, 1.0D, 0.5D, 1.0D),
					OverdriveBlockProperties.from(GenericMachineBlock.DEFAULT_MACHINE_PROPERTIES));

	// DUNSEW
	public final OverdriveBlockProperties properties;
	public VoxelShape[] shapes = new VoxelShape[6];
	public VoxelShape omniShape = Shapes.block();
	public final boolean hasCustomAABB;
	public final boolean singleShape;

	private TypeMachine(OverdriveBlockProperties properties) {
		hasCustomAABB = false;
		singleShape = false;
		this.properties = properties;
	}

	private TypeMachine(VoxelShape allDirs, OverdriveBlockProperties properties) {
		hasCustomAABB = true;
		omniShape = allDirs;
		singleShape = true;
		this.properties = properties;
	}

	private TypeMachine(VoxelShape[] allDirs, OverdriveBlockProperties properties) {
		hasCustomAABB = true;
		shapes = allDirs;
		singleShape = false;
		this.properties = properties;
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
