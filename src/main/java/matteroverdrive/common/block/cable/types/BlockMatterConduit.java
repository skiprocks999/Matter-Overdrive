package matteroverdrive.common.block.cable.types;

import java.util.HashSet;

import matteroverdrive.common.block.cable.AbstractCableBlock;
import matteroverdrive.common.block.type.TypeMatterConduit;
import matteroverdrive.common.tile.TileMatterConduit;
import matteroverdrive.core.block.OverdriveBlockProperties;
import matteroverdrive.core.utils.UtilsMatter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockMatterConduit extends AbstractCableBlock {

	public static final HashSet<Block> PIPESET = new HashSet<>();

	public BlockMatterConduit(TypeMatterConduit type) {
		super(OverdriveBlockProperties.from(DEFAULT_CABLE_PROPERTIES).setCanBeWaterlogged(), type);
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

}
