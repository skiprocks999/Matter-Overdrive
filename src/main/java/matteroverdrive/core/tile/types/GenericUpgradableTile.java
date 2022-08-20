package matteroverdrive.core.tile.types;

import matteroverdrive.core.property.Property;
import matteroverdrive.core.property.PropertyTypes;
import matteroverdrive.core.tile.utils.IUpgradableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class GenericUpgradableTile extends GenericRedstoneTile implements IUpgradableTile {

	// Defaults
	public double defaultSpeed = 0;
	public double defaultMatterUsage = 0;
	public double defaultMatterStorage = 0;
	public double defaultPowerStorage = 0;
	public double defaultPowerUsage = 0;
	public double defaultRange = 0;
	public float defaultFailureChance = 0;
	public double defaultProcessingTime = 0;

	// Currents
	private double currentSpeed = 0;
	private double currentMatterUsage = 0;
	private double currentPowerUsage = 0;
	private double currentRange = 0;
	private float currentFailureChance = 0;

	private double saMultiplier = 1; //we don't save this to NBT to make out lives easier

	// MISC
	private boolean isMuffled = false;

	// Properties
	private final Property<Double> currSpeedProp;
	private final Property<Double> currMatterUsage;
	private final Property<Double> currPowerUsage;
	private final Property<Double> currRangeProp;
	private final Property<Boolean> currIsMuffled;
	private final Property<Double> currSAMultiplier;
	private final Property<Float> currFailureChance;
	
	public GenericUpgradableTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.currSpeedProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.DOUBLE.create(this::currentSpeedGetter, this::speedSetter));
		this.currMatterUsage = this.getPropertyManager().addTrackedProperty(PropertyTypes.DOUBLE.create(this::currentMatterUsageGetter, this::matterUsageSetter));
		this.currPowerUsage = this.getPropertyManager().addTrackedProperty(PropertyTypes.DOUBLE.create(this::currentPowerUsageGetter, this::powerUsageSetter));
		this.currRangeProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.DOUBLE.create(this::currentRangeGetter, this::rangeSetter));
		this.currIsMuffled = this.getPropertyManager().addTrackedProperty(PropertyTypes.BOOLEAN.create(this::muffledGetter, this::muffledSetter));
		this.currSAMultiplier = this.getPropertyManager().addTrackedProperty(PropertyTypes.DOUBLE.create(this::acceleratorMultiplierGetter, this::acceleratorMultiplierSetter));
		this.currFailureChance = this.getPropertyManager().addTrackedProperty(PropertyTypes.FLOAT.create(this::currentFailureGetter, this::failureSetter));
	}
	
	//INTERFACE HANDLING

	@Override
	public double getDefaultSpeed() {
		return this.defaultSpeed;
	}

	@Override
	public double getDefaultMatterUsage() {
		return this.defaultMatterUsage;
	}

	@Override
	public float getDefaultFailure() {
		return this.defaultFailureChance;
	}

	@Override
	public double getDefaultMatterStorage() {
		return this.defaultMatterStorage;
	}

	@Override
	public double getDefaultPowerStorage() {
		return this.defaultPowerStorage;
	}

	@Override
	public double getDefaultPowerUsage() {
		return this.defaultPowerUsage;
	}

	@Override
	public double getDefaultRange() {
		return this.defaultRange;
	}
	
	//getters

	@Override
	public boolean isMuffled() {
		return this.currIsMuffled.get();
	}

	@Override
	public double getCurrentSpeed() {
		return this.currSpeedProp.get();
	}

	@Override
	public double getCurrentMatterUsage() {
		return this.currMatterUsage.get();
	}

	@Override
	public float getCurrentFailure() {
		return this.currFailureChance.get();
	}

	@Override
	public double getCurrentPowerUsage() {
		return this.currPowerUsage.get();
	}

	@Override
	public double getCurrentRange() {
		return this.currRangeProp.get();
	}

	@Override
	public double getAcceleratorMultiplier() {
		return this.currSAMultiplier.get();
	}
	
	//setters

	@Override
	public void setAcceleratorMultiplier(double multiplier) {
		this.currSAMultiplier.set(multiplier);
	}

	@Override
	public void setSpeed(double speed) {
		this.currSpeedProp.set(speed);
	}

	@Override
	public void setMatterUsage(double matter) {
		this.currMatterUsage.set(matter);
	}

	@Override
	public void setFailure(float failure) {
		this.currFailureChance.set(failure);
	}

	@Override
	public void setPowerUsage(double usage) {
		this.currPowerUsage.set(usage);
	}

	@Override
	public void setRange(double range) {
		this.currRangeProp.set(range);
	}

	@Override
	public void setMuffled(boolean muffled) {
		this.currIsMuffled.set(muffled);
	}
	
	@Override
	public double getProcessingTime() {
		return defaultProcessingTime;
	}
	
	//GETTERS
	
	private boolean muffledGetter() {
		return this.isMuffled;
	}

	private double currentSpeedGetter() {
		return this.currentSpeed;
	}

	private double currentMatterUsageGetter() {
		return this.currentMatterUsage;
	}

	private float currentFailureGetter() {
		return this.currentFailureChance;
	}

	private double currentPowerUsageGetter() {
		return this.currentPowerUsage;
	}

	private double currentRangeGetter() {
		return this.currentRange;
	}

	private double acceleratorMultiplierGetter() {
		return saMultiplier;
	}
	
	//SETTERS
	
	private void acceleratorMultiplierSetter(double multiplier) {
		this.saMultiplier = multiplier;
	}

	private void speedSetter(double speed) {
		this.currentSpeed = speed;
	}

	private void matterUsageSetter(double matter) {
		this.currentMatterUsage = matter;
	}

	private void failureSetter(float failure) {
		this.currentFailureChance = failure;
	}

	private void powerUsageSetter(double usage) {
		this.currentPowerUsage = usage;
	}

	private void rangeSetter(double range) {
		this.currentRange = range;
	}

	private void muffledSetter(boolean muffled) {
		this.isMuffled = muffled;
	}

}
