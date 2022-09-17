package matteroverdrive.core.tile.types;

import matteroverdrive.core.property.Property;
import matteroverdrive.core.property.PropertyTypes;
import matteroverdrive.core.tile.utils.IUpgradableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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

	private double saMultiplier = 1; // we don't save this to NBT to make out lives easier

	// MISC
	private boolean isMuffled = false;

	// Properties
	public final Property<Double> currSpeedProp;
	public final Property<Double> currMatterUsage;
	public final Property<Double> currPowerUsage;
	public final Property<Double> currRangeProp;
	public final Property<Boolean> currIsMuffled;
	public final Property<Double> currSAMultiplier;
	public final Property<Float> currFailureChance;

	public GenericUpgradableTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.currSpeedProp = this.getPropertyManager()
				.addTrackedProperty(PropertyTypes.DOUBLE.create(() -> currentSpeed, speed -> currentSpeed = speed));
		this.currMatterUsage = this.getPropertyManager().addTrackedProperty(
				PropertyTypes.DOUBLE.create(() -> currentMatterUsage, usage -> currentMatterUsage = usage));
		this.currPowerUsage = this.getPropertyManager().addTrackedProperty(
				PropertyTypes.DOUBLE.create(() -> currentPowerUsage, usage -> currentPowerUsage = usage));
		this.currRangeProp = this.getPropertyManager()
				.addTrackedProperty(PropertyTypes.DOUBLE.create(() -> currentRange, range -> currentRange = range));
		this.currIsMuffled = this.getPropertyManager()
				.addTrackedProperty(PropertyTypes.BOOLEAN.create(() -> isMuffled, muff -> isMuffled = muff));
		this.currSAMultiplier = this.getPropertyManager()
				.addTrackedProperty(PropertyTypes.DOUBLE.create(() -> saMultiplier, mult -> saMultiplier = mult));
		this.currFailureChance = this.getPropertyManager().addTrackedProperty(
				PropertyTypes.FLOAT.create(() -> currentFailureChance, fail -> currentFailureChance = fail));
	}

	// INTERFACE HANDLING

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

	// getters

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

	// setters

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
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		
		CompoundTag additional = new CompoundTag();
		additional.putDouble("currSpeed", currSpeedProp.get());
		additional.putDouble("currMatterUsage", currMatterUsage.get());
		additional.putDouble("currPowerUsage", currPowerUsage.get());
		additional.putDouble("currRange", currRangeProp.get());
		additional.putBoolean("currIsMuffled", currIsMuffled.get());
		additional.putFloat("currFailure", currFailureChance.get());
		
		tag.put("upgradeinfo", additional);
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		
		CompoundTag additional = tag.getCompound("upgradeinfo");
		
		setSpeed(additional.getDouble("currSpeed"));
		setMatterUsage(additional.getDouble("currMatterUsage"));
		setPowerUsage(additional.getDouble("currPowerUsage"));
		setRange(additional.getDouble("currRange"));
		setFailure(additional.getFloat("currFailure"));
		setMuffled(additional.getBoolean("currIsMuffled"));
	}
	
	@Override
	public void getFirstContactData(CompoundTag tag) {
		saveAdditional(tag);
	}

}
