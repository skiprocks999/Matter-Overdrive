package matteroverdrive.core.utils;

import java.util.ArrayList;
import java.util.List;

import matteroverdrive.core.tile.utils.IUpgradableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

public class UtilsWorld {

	public static double distanceBetweenPositions(BlockPos a, BlockPos b) {
		return Mth.sqrt((float) (Mth.square(a.getX() - b.getX()) + Mth.square(a.getY() - b.getY())
				+ Mth.square(a.getZ() - b.getZ())));
	}
	
	public static List<BlockEntity> getSurroundingBlockEntities(Level world, AABB aabb){
		List<BlockEntity> entities = new ArrayList<>();
		BlockPos.betweenClosedStream(aabb).forEach(pos -> {
			BlockEntity tile = world.getBlockEntity(pos);
			if(tile != null) {
				entities.add(tile);
			}
		});
		return entities;
	}
	
	public static List<IUpgradableTile> getSurroundingUpgradableTiles(Level world, AABB aabb){
		List<IUpgradableTile> entities = new ArrayList<>();
		BlockPos.betweenClosedStream(aabb).forEach(pos -> {
			BlockEntity tile = world.getBlockEntity(pos);
			if(tile != null && tile instanceof IUpgradableTile upgrade) {
				entities.add(upgrade);
			}
		});
		return entities;
	}

}
