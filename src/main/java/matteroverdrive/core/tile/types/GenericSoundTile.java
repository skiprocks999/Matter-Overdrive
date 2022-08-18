package matteroverdrive.core.tile.types;

import matteroverdrive.core.property.Property;
import matteroverdrive.core.property.PropertyTypes;
import matteroverdrive.core.sound.tile.ITickingSoundTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class GenericSoundTile extends GenericUpgradableTile implements ITickingSoundTile {

	public boolean isRunning = false;
	//Client-side only value
	protected boolean clientSoundPlaying = false; 
	
	public final Property<Boolean> currIsRunning;
	
	protected GenericSoundTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		currIsRunning = this.getPropertyManager().addTrackedProperty(PropertyTypes.BOOLEAN.create(this::isRunning));
	}

	@Override
	public void setNotPlaying() {
		clientSoundPlaying = false;
	}
	
	@Override
	public boolean shouldPlaySound() {
		return isRunning && !isMuffled;
	}
	
	public boolean isRunning() {
		return isRunning;
	}

}
