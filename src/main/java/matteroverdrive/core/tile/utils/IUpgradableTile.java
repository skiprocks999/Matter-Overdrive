package matteroverdrive.core.tile.utils;

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

	default boolean isMuffled() {
		return false;
	}

	// MODIFIED VALUES

	default double getCurrentSpeed() {
		return 0;
	}

	default double getCurrentMatterUsage() {
		return 0;
	}

	default float getCurrentFailure() {
		return 0;
	}

	default double getCurrentMatterStorage() {
		return 0;
	}

	default double getCurrentPowerStorage() {
		return 0;
	}

	default double getCurrentPowerUsage() {
		return 0;
	}

	default double getCurrentRange() {
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

	default void setPowerStorage(double storage) {
	}

	default void setPowerUsage(double usage) {
	}

	default void setRange(double range) {
	}

	default void setMuffled(boolean muffled) {
	}

	// MISC

	default void setAcceleratorMultiplier(double multiplier) {
	}

	default double getProcessingTime() {
		return 0;
	}

}
