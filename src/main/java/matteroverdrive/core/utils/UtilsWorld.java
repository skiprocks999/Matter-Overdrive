package matteroverdrive.core.utils;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import matteroverdrive.core.tile.utils.IUpgradableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;

public class UtilsWorld {

	public static double distanceBetweenPositions(BlockPos a, BlockPos b) {
		return Mth.sqrt((float) (Mth.square(a.getX() - b.getX()) + Mth.square(a.getY() - b.getY())
				+ Mth.square(a.getZ() - b.getZ())));
	}

	public static List<BlockEntity> getSurroundingBlockEntities(Level world, AABB aabb) {
		List<BlockEntity> entities = new ArrayList<>();
		BlockPos.betweenClosedStream(aabb).forEach(pos -> {
			BlockEntity tile = world.getBlockEntity(pos);
			if (tile != null) {
				entities.add(tile);
			}
		});
		return entities;
	}

	public static List<IUpgradableTile> getSurroundingUpgradableTiles(Level world, AABB aabb) {
		List<IUpgradableTile> entities = new ArrayList<>();
		BlockPos.betweenClosedStream(aabb).forEach(pos -> {
			BlockEntity tile = world.getBlockEntity(pos);
			if (tile != null && tile instanceof IUpgradableTile upgrade) {
				entities.add(upgrade);
			}
		});
		return entities;
	}

	@Nullable
	public static BlockPos getPosFromTraceNoFluid(Player player) {
		Level world = player.level;
		BlockHitResult trace = Item.getPlayerPOVHitResult(world, player,
				net.minecraft.world.level.ClipContext.Fluid.ANY);
		if (trace.getType() != Type.MISS && trace.getType() != Type.ENTITY) {
			return trace.getBlockPos();
		}
		return null;
	}

	public static boolean isNotFluid(BlockState state) {
		return state.getFluidState().getType().isSame(Fluids.EMPTY);
	}

}
