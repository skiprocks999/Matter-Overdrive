package matteroverdrive.core.tile.types;

import matteroverdrive.core.property.Property;
import matteroverdrive.core.property.PropertyTypes;
import matteroverdrive.core.tile.utils.IUpgradableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class GenericUpgradableTile extends GenericRedstoneTile implements IUpgradableTile {


	
	public double clientSAMultipler = 1;

	// Defaults
	public double defaultSpeed = 0;
	public double defaultMatterUsage = 0;
	public double defaultMatterStorage = 0;
	public double defaultPowerStorage = 0;
	public double defaultPowerUsage = 0;
	public double defaultRange = 0;
	public float defaultFailureChance = 0;

	// Currents
	public double currentSpeed = 0;
	public double currentMatterUsage = 0;
	public double currentMatterStorage = 0;
	public double currentPowerStorage = 0;
	public double currentPowerUsage = 0;
	public double currentRange = 0;
	public float currentFailureChance = 0;

	public double saMultiplier = 1; //we don't save this to NBT to make out lives easier

	// MISC
	public boolean isMuffled = false;

	// Properties
	public final Property<Double> currSpeedProp;
	public final Property<Double> currMatterUsage;
	public final Property<Double> currMatterStorage;
	public final Property<Double> currPowerUsage;
	public final Property<Double> currPowerStorage;
	public final Property<Double> currRangeProp;
	public final Property<Boolean> currIsMuffled;
	public final Property<Double> currSAMultiplier;
	
	public GenericUpgradableTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.currSpeedProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.DOUBLE.create(this::getCurrentSpeed, this::setSpeed));
		this.currMatterUsage = this.getPropertyManager().addTrackedProperty(PropertyTypes.DOUBLE.create(this::getCurrentMatterUsage, this::setMatterUsage));
		this.currMatterStorage = this.getPropertyManager().addTrackedProperty(PropertyTypes.DOUBLE.create(this::getCurrentMatterStorage, this::setMatterStorage));
		this.currPowerUsage = this.getPropertyManager().addTrackedProperty(PropertyTypes.DOUBLE.create(this::getCurrentPowerUsage, this::setPowerUsage));
		this.currPowerStorage = this.getPropertyManager().addTrackedProperty(PropertyTypes.DOUBLE.create(this::getCurrentPowerStorage, this::setPowerStorage));
		this.currRangeProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.DOUBLE.create(this::getCurrentRange, this::setRange));
		this.currIsMuffled = this.getPropertyManager().addTrackedProperty(PropertyTypes.BOOLEAN.create(this::isMuffled, this::setMuffled));
		this.currSAMultiplier = this.getPropertyManager().addTrackedProperty(PropertyTypes.DOUBLE.create(this::getSaMultiplier, this::setSaMultiplier));
	}

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

	@Override
	public boolean isMuffled() {
		return this.isMuffled;
	}

	@Override
	public double getCurrentSpeed() {
		return this.currentSpeed;
	}

	@Override
	public double getCurrentMatterUsage() {
		return this.currentMatterUsage;
	}

	@Override
	public float getCurrentFailure() {
		return this.currentFailureChance;
	}

	@Override
	public double getCurrentMatterStorage() {
		return this.currentMatterStorage;
	}

	@Override
	public double getCurrentPowerStorage() {
		return this.currentPowerStorage;
	}

	@Override
	public double getCurrentPowerUsage() {
		return this.currentPowerUsage;
	}

	@Override
	public double getCurrentRange() {
		return this.currentRange;
	}

	public double getSaMultiplier() {
		return saMultiplier;
	}

	@Override
	public void setAcceleratorMultiplier(double multiplier) {
		this.saMultiplier = multiplier;
	}

	@Override
	public void setSpeed(double speed) {
		this.currentSpeed = speed;
	}

	@Override
	public void setMatterUsage(double matter) {
		this.currentMatterUsage = matter;
	}

	@Override
	public void setFailure(float failure) {
		this.currentFailureChance = failure;
	}

	@Override
	public void setMatterStorage(double storage) {
		this.currentMatterStorage = storage;
	}

	@Override
	public void setPowerStorage(double storage) {
		this.currentPowerStorage = storage;
	}

	@Override
	public void setPowerUsage(double usage) {
		this.currentPowerUsage = usage;
	}

	@Override
	public void setRange(double range) {
		this.currentRange = range;
	}

	@Override
	public void setMuffled(boolean muffled) {
		this.isMuffled = muffled;
	}

	public void setSaMultiplier(double saMultiplier) {
		this.saMultiplier = saMultiplier;
	}
}
