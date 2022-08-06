package matteroverdrive.common.block.cable.serverside;

import java.util.HashSet;

import matteroverdrive.common.block.cable.AbstractCableBlock;
import matteroverdrive.common.block.type.TypeMatterConduit;
import matteroverdrive.common.tile.TileMatterConduit;
import matteroverdrive.core.utils.UtilsMatter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class BlockMatterConduit extends AbstractCableBlock {

	public static final HashSet<Block> PIPESET = new HashSet<>();

	public BlockMatterConduit(TypeMatterConduit type) {
		super(Properties.of(Material.METAL).sound(SoundType.METAL).strength(0.15f).dynamicShape(), type);

		PIPESET.add(this);
	}

	@Override
	protected void sortDirections(HashSet<Direction> usedDirs, HashSet<Direction> inventory, HashSet<Direction> cable,
			LevelAccessor world, BlockPos pos) {
		BlockEntity entity;
		for (Direction dir : Direction.values()) {
			entity = world.getBlockEntity(pos.relative(dir));
			if (entity instanceof TileMatterConduit) {
				usedDirs.add(dir);
				cable.add(dir);
			} else if (UtilsMatter.isMatterReceiver(entity, dir.getOpposite())) {
				usedDirs.add(dir);
				inventory.add(dir);
			}
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileMatterConduit(pos, state);
	}

	@Override
	public boolean checkCableClass(BlockEntity entity) {
		return entity instanceof TileMatterConduit;
	}

	@Override
	public boolean isValidConnection(BlockEntity facingTile, Direction facing) {
		return UtilsMatter.isMatterReceiver(facingTile, facing);
	}

}
