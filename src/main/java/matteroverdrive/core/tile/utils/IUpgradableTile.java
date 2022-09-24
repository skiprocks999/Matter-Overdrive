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

	default double getAcceleratorMultiplier() {
		return 0;
	}

	// MUTATOR METHODS
	
	//boolean indicates change

	default boolean setSpeed(double speed) {
		return false;
	}

	default boolean setMatterUsage(double matter) {
		return false;
	}

	default boolean setFailure(float failure) {
		return false;
	}

	default boolean setMatterStorage(double storage) {
		return false;
	}

	default boolean setPowerStorage(double storage) {
		return false;
	}

	default boolean setPowerUsage(double usage) {
		return false;
	}

	default boolean setRange(double range) {
		return false;
	}

	default boolean setMuffled(boolean muffled) {
		return false;
	}
	
	default boolean setProcessingTime(double time) {
		return false;
	}

	// MISC

	default boolean setAcceleratorMultiplier(double multiplier) {
		return false;
	}

	default double getProcessingTime() {
		return 0;
	}
	
	default void onUpgradesChange(double[] prevValues, double[] newValues) {
		
	}

}
