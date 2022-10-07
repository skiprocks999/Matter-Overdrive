package matteroverdrive.core.tile.utils;

import net.minecraft.world.level.Level;

public interface ITickableTile {

	long getTicks();

	void incrementTicks();

	/**
	 * Do not override this unless you know exactly what you're doing
	 * @param world The world the ticker is in
	 */
	@Deprecated
	default void tick(Level world) {
		tickCommon();
		incrementTicks();
		if (world.isClientSide) {
			tickClient();
		} else {
			tickServer();
		}
	}

	default void tickServer() {

	}

	default void tickCommon() {

	}

	default void tickClient() {

	}
	
	boolean canTick();
	
	boolean updateTickable(boolean tickable);

}
