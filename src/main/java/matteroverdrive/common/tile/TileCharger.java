package matteroverdrive.common.tile;

import java.util.HashSet;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.block.MultiBlockMachine;
import matteroverdrive.core.block.multiblock.IMultiblockTileNode;
import matteroverdrive.core.block.multiblock.Subnode;
import matteroverdrive.core.tile.GenericTile;
import matteroverdrive.core.tile.IRedstoneModeTile;
import matteroverdrive.core.tile.IUpgradableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class TileCharger extends GenericTile implements IRedstoneModeTile, IUpgradableTile, IMultiblockTileNode {

	public TileCharger(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_CHARGER.get(), pos, state);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
	}
	
	@Override
	public AABB getRenderBoundingBox() {
		return super.getRenderBoundingBox().inflate(2);
	}

	@Override
	public HashSet<Subnode> getSubNodes() {
		return MultiBlockMachine.CHARGER_NODES;
	}

	@Override
	public void setMode(int mode) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getCurrMod() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxMode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean canRun() {
		// TODO Auto-generated method stub
		return false;
	}

}
