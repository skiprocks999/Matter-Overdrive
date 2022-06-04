package matteroverdrive.common.block.cable;

import java.util.HashSet;

import matteroverdrive.common.block.type.TypeMatterConduit;
import matteroverdrive.common.tile.TileMatterConduit;
import matteroverdrive.core.cable.types.matter_pipe.IMatterConduit;
import matteroverdrive.core.utils.UtilsMatter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class BlockMatterConduit extends AbstractCableBlock {

	public static final HashSet<Block> PIPESET = new HashSet<>();
	
	public final TypeMatterConduit type;

	public BlockMatterConduit(TypeMatterConduit type) {
		super(Properties.of(Material.METAL).sound(SoundType.METAL).strength(0.15f).dynamicShape(), type.width);
		
		this.type = type;
		
		PIPESET.add(this);
	}

	@Override
	public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		super.onPlace(state, worldIn, pos, oldState, isMoving);
		if (!worldIn.isClientSide) {
			BlockEntity tile = worldIn.getBlockEntity(pos);
			if (tile instanceof IMatterConduit p) {
				p.refreshNetwork();
			}
		}
	}

	@Override
	public void onNeighborChange(BlockState state, LevelReader world, BlockPos pos, BlockPos neighbor) {
		super.onNeighborChange(state, world, pos, neighbor);
		if (!world.isClientSide()) {
			BlockEntity tile = world.getBlockEntity(pos);
			if (tile instanceof IMatterConduit p) {
				p.refreshNetworkIfChange();
			}
		}
	}
	
	@Override
	protected void sortDirections(HashSet<Direction> usedDirs, HashSet<Direction> inventory, HashSet<Direction> cable, 
			LevelAccessor world, BlockPos pos) {
		BlockEntity entity;
		for(Direction dir : Direction.values()) {
			entity = world.getBlockEntity(pos.relative(dir));
			if (entity instanceof IMatterConduit) {
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
