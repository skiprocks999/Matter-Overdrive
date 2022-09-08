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
	
	/**
	 * This should only be called when the BlockEntityTicker is created. It allows
	 * for a simple filter option for tiles that might be an instance of this 
	 * interface but are not intended to tick. Note, if the logic that affects this
	 * is modified from an internal tick method, then the block might cease to tick!
	 * @return
	 */
	@Deprecated
	boolean canTick();

}
