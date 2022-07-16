package matteroverdrive.common.block.cable.dualside;

import java.util.HashSet;

import matteroverdrive.common.block.type.TypeMatterNetworkCable;
import matteroverdrive.common.tile.cable.AbstractCableTile;
import matteroverdrive.common.tile.matter_network.TileMatterNetworkCable;
import matteroverdrive.core.network.utils.IMatterNetworkMember;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class BlockMatterNetworkCable extends AbstractDualSideCableBlock {
	
	public BlockMatterNetworkCable(TypeMatterNetworkCable type) {
		super(Properties.of(Material.METAL).sound(SoundType.METAL).strength(0.15f).dynamicShape(), type);
	}
	
	@Override
	protected void sortDirections(HashSet<Direction> usedDirs, HashSet<Direction> inventory, HashSet<Direction> cable,
			LevelAccessor world, BlockPos pos) {
		
		BlockEntity entity;
		for(Direction dir : Direction.values()) {
			entity = world.getBlockEntity(pos.relative(dir));
			if (entity instanceof TileMatterNetworkCable) {
				usedDirs.add(dir);
				cable.add(dir);
			} else if (entity instanceof IMatterNetworkMember member && member.canConnectToFace(dir.getOpposite())) {
				usedDirs.add(dir);
				inventory.add(dir);
			} 
		}
		
	}
	
	@Override
	public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		super.onPlace(state, worldIn, pos, oldState, isMoving);
		BlockEntity tile = worldIn.getBlockEntity(pos);
		if (checkCableClass(tile)) {
			((AbstractCableTile<?>)tile).refreshNetwork();
		}
	}

	@Override
	public void onNeighborChange(BlockState state, LevelReader world, BlockPos pos, BlockPos neighbor) {
		super.onNeighborChange(state, world, pos, neighbor);
		BlockEntity tile = world.getBlockEntity(pos);
		if (checkCableClass(tile)) {
			((AbstractCableTile<?>)tile).refreshNetwork();
		}
		
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileMatterNetworkCable(pos, state);
	}

	@Override
	public boolean checkCableClass(BlockEntity entity) {
		return entity instanceof TileMatterNetworkCable;
	}

	@Override
	public boolean isValidConnection(BlockEntity facingTile, Direction facing) {
		return facingTile instanceof IMatterNetworkMember member && member.canConnectToFace(facing);
	}
	
}
