package matteroverdrive.core.tile;

public interface IUpgradableTile {

	// DEFAULT VALUES

	default double getDefaultSpeed() {
		return 0;
	}

	default double getDefaultMatterUsage() {
		return 0;
	}

	default float getDefaultFailure() {
		return 0;
	}

	default double getDefaultMatterStorage() {
		return 0;
	}

	default double getDefaultPowerStorage() {
		return 0;
	}

	default double getDefaultPowerUsage() {
		return 0;
	}

	default double getDefaultRange() {
		return 0;
	}

	default boolean isMuffled(boolean clientSide) {
		return false;
	}

	// MODIFIED VALUES

	default double getCurrentSpeed(boolean clientSide) {
		return 0;
	}

	default double getCurrentMatterUsage(boolean clientSide) {
		return 0;
	}

	default float getCurrentFailure(boolean clientSide) {
		return 0;
	}

	default double getCurrentMatterStorage(boolean clientSide) {
		return 0;
	}

	default double getCurrentPowerStorage(boolean clientSide) {
		return 0;
	}

	default double getCurrentPowerUsage(boolean clientSide) {
		return 0;
	}

	default double getCurrentRange(boolean clientSide) {
		return 0;
	}

	// MUTATOR METHODS

	default void setSpeed(double speed) {
	}

	default void setMatterUsage(double matter) {
	}

	default void setFailure(float failure) {
	}

	default void setMatterStorage(double storage) {
	}

	default void setPowerStorage(int storage) {
	}

	default void setPowerUsage(int usage) {
	}

	default void setRange(int range) {
	}

	default void setMuffled(boolean muffled) {
	}

	// MISC

	default void setAcceleratorMultiplier(int multiplier) {
	}

	default int getProcessingTime() {
		return 0;
	}

}
