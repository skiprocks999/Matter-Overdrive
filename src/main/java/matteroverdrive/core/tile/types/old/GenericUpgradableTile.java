package matteroverdrive.core.tile.types.old;

import matteroverdrive.core.tile.utils.IUpgradableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class GenericUpgradableTile extends GenericRedstoneTile implements IUpgradableTile {

	protected double saMultiplier = 1;//we don't save this to NBT to make out lives easier
	
	public double clientSAMultipler = 1;
	
	public GenericUpgradableTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
	
	@Override
	public void setAcceleratorMultiplier(double multiplier) {
		saMultiplier = multiplier;
	}

}
