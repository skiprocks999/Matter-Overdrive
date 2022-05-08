// Credit to AurilisDev https://github.com/aurilisdev/Electrodynamics
package matteroverdrive.core.block.multiblock;

import java.util.function.Function;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Subnode {
	public BlockPos pos;
	public Function<Direction, VoxelShape> shape;

	public Subnode(BlockPos pos, VoxelShape shape) {
		this(pos, direction -> shape);
	}

	public Subnode(BlockPos pos, Function<Direction, VoxelShape> shape) {
		this.pos = pos;
		this.shape = shape;
	}
}
