package matteroverdrive.core.tile.types;

import matteroverdrive.common.item.ItemUpgrade;
import matteroverdrive.common.item.ItemUpgrade.UpgradeType;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.property.Property;
import matteroverdrive.core.property.PropertyTypes;
import matteroverdrive.core.tile.utils.IUpgradableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
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

	// Currents
	private double currentSpeed = 0;
	private double currentMatterUsage = 0;
	private double currentPowerUsage = 0;
	private double currentRange = 0;
	private float currentFailureChance = 0;
	
	private double currentProcessingTime = 0;

	private double saMultiplier = 1; // we don't save this to NBT to make out lives easier

	// MISC
	private boolean isMuffled = false;

	// Properties
	private final Property<Double> currSpeedProp;
	private final Property<Double> currMatterUsageProp;
	private final Property<Double> currPowerUsageProp;
	private final Property<Double> currRangeProp;
	private final Property<Boolean> currIsMuffledProp;
	private final Property<Double> currSAMultiplierProp;
	private final Property<Float> currFailureChanceProp;
	private final Property<Double> currProcessingTimeProp;

	public GenericUpgradableTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.currSpeedProp = this.getPropertyManager()
				.addTrackedProperty(PropertyTypes.DOUBLE.create(() -> currentSpeed, speed -> currentSpeed = speed));
		this.currMatterUsageProp = this.getPropertyManager().addTrackedProperty(
				PropertyTypes.DOUBLE.create(() -> currentMatterUsage, usage -> currentMatterUsage = usage));
		this.currPowerUsageProp = this.getPropertyManager().addTrackedProperty(
				PropertyTypes.DOUBLE.create(() -> currentPowerUsage, usage -> currentPowerUsage = usage));
		this.currRangeProp = this.getPropertyManager()
				.addTrackedProperty(PropertyTypes.DOUBLE.create(() -> currentRange, range -> currentRange = range));
		this.currIsMuffledProp = this.getPropertyManager()
				.addTrackedProperty(PropertyTypes.BOOLEAN.create(() -> isMuffled, muff -> isMuffled = muff));
		this.currSAMultiplierProp = this.getPropertyManager()
				.addTrackedProperty(PropertyTypes.DOUBLE.create(() -> saMultiplier, mult -> saMultiplier = mult));
		this.currFailureChanceProp = this.getPropertyManager().addTrackedProperty(
				PropertyTypes.FLOAT.create(() -> currentFailureChance, fail -> currentFailureChance = fail));
		this.currProcessingTimeProp = this.getPropertyManager()
				.addTrackedProperty(PropertyTypes.DOUBLE.create(() -> currentProcessingTime, time -> currentProcessingTime = time));
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
		return this.currIsMuffledProp.get();
	}

	@Override
	public double getCurrentSpeed() {
		return this.currSpeedProp.get();
	}

	@Override
	public double getCurrentMatterUsage() {
		return this.currMatterUsageProp.get();
	}

	@Override
	public float getCurrentFailure() {
		return this.currFailureChanceProp.get();
	}

	@Override
	public double getCurrentPowerUsage() {
		return this.currPowerUsageProp.get();
	}

	@Override
	public double getCurrentRange() {
		return this.currRangeProp.get();
	}

	@Override
	public double getAcceleratorMultiplier() {
		return this.currSAMultiplierProp.get();
	}

	// setters

	@Override
	public boolean setAcceleratorMultiplier(double multiplier) {
		this.currSAMultiplierProp.set(multiplier);
		return currSAMultiplierProp.isDirtyNoUpdate();
	}

	@Override
	public boolean setSpeed(double speed) {
		this.currSpeedProp.set(speed);
		return currSpeedProp.isDirtyNoUpdate();
	}

	@Override
	public boolean setMatterUsage(double matter) {
		this.currMatterUsageProp.set(matter);
		return currMatterUsageProp.isDirtyNoUpdate();
	}

	@Override
	public boolean setFailure(float failure) {
		this.currFailureChanceProp.set(failure);
		return currFailureChanceProp.isDirtyNoUpdate();
	}

	@Override
	public boolean setPowerUsage(double usage) {
		this.currPowerUsageProp.set(usage);
		return currPowerUsageProp.isDirtyNoUpdate();
	}

	@Override
	public boolean setRange(double range) {
		this.currRangeProp.set(range);
		return currRangeProp.isDirtyNoUpdate();
	}

	@Override
	public boolean setMuffled(boolean muffled) {
		this.currIsMuffledProp.set(muffled);
		return currIsMuffledProp.isDirtyNoUpdate();
	}
	
	@Override
	public boolean setProcessingTime(double time) {
		this.currProcessingTimeProp.set(time);
		return currProcessingTimeProp.isDirtyNoUpdate();
	}

	@Override
	public double getProcessingTime() {
		return currentProcessingTime;
	}
	
	@Override
	public void onInventoryChange(int slot, CapabilityInventory inv) {
		super.onInventoryChange(slot, inv);
		if (slot >= inv.upgradeIndex() && inv.upgrades() > 0) {
			
			double speed = getDefaultSpeed();
			double matterUsage = getDefaultMatterUsage();
			double matterStorage = getDefaultMatterStorage();
			float failure = getDefaultFailure();
			double powerStorage = getDefaultPowerStorage();
			double powerUsage = getDefaultPowerUsage();
			double range = getDefaultRange();
			boolean isMuffled = isMuffled();
			
			double[] prevValues = {speed, matterUsage, matterStorage, failure, powerStorage, powerUsage, range, isMuffled ? 1.0D : 0.0D};
			
			for (ItemStack stack : inv.getUpgrades()) {
				if (!stack.isEmpty()) {
					UpgradeType upgrade = ((ItemUpgrade) stack.getItem()).type;
					speed *= upgrade.speedBonus;
					matterUsage *= upgrade.matterUsageBonus;
					matterStorage *= upgrade.matterStorageBonus;
					failure *= upgrade.failureChanceBonus;
					powerStorage *= upgrade.powerStorageBonus;
					powerUsage *= upgrade.powerUsageBonus;
					range *= upgrade.rangeBonus;
					if (upgrade == UpgradeType.MUFFLER)
						isMuffled = true;
				}
			}
			setSpeed(speed);
			setMatterUsage(matterUsage);
			setMatterStorage(matterStorage);
			setFailure(failure);
			setPowerStorage((int) powerStorage);
			setPowerUsage((int) powerUsage);
			setRange((int) range);
			setMuffled(isMuffled);
			
			double[] newValues = {speed, matterUsage, matterStorage, failure, powerStorage, powerUsage, range, isMuffled ? 1.0D : 0.0D};
			
			onUpgradesChange(prevValues, newValues);
			
		}
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		
		CompoundTag additional = new CompoundTag();
		additional.putDouble("currSpeed", currSpeedProp.get());
		additional.putDouble("currMatterUsage", currMatterUsageProp.get());
		additional.putDouble("currPowerUsage", currPowerUsageProp.get());
		additional.putDouble("currRange", currRangeProp.get());
		additional.putBoolean("currIsMuffled", currIsMuffledProp.get());
		additional.putFloat("currFailure", currFailureChanceProp.get());
		additional.putDouble("procTime", currentProcessingTime);
		
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
		setProcessingTime(additional.getDouble("procTime"));
	}

}
