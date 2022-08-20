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
		this.currSpeedProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.DOUBLE.create(() -> currentSpeed, speed -> currentSpeed = speed));
		this.currMatterUsage = this.getPropertyManager().addTrackedProperty(PropertyTypes.DOUBLE.create(() -> currentMatterUsage, usage -> currentMatterUsage = usage));
		this.currPowerUsage = this.getPropertyManager().addTrackedProperty(PropertyTypes.DOUBLE.create(() -> currentPowerUsage, usage -> currentPowerUsage = usage));
		this.currRangeProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.DOUBLE.create(() -> currentRange, range -> currentRange = range));
		this.currIsMuffled = this.getPropertyManager().addTrackedProperty(PropertyTypes.BOOLEAN.create(() -> isMuffled, muff -> isMuffled = muff));
		this.currSAMultiplier = this.getPropertyManager().addTrackedProperty(PropertyTypes.DOUBLE.create(() -> saMultiplier, mult -> saMultiplier = mult));
		this.currFailureChance = this.getPropertyManager().addTrackedProperty(PropertyTypes.FLOAT.create(() -> currentFailureChance, fail -> currentFailureChance = fail));
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

}
