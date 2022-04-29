package matteroverdrive.core.capability.types.energy;

public class CapabilityCreativeEnergyStorage extends CapabilityEnergyStorage {

	public CapabilityCreativeEnergyStorage(int maxStorage, boolean hasInput, boolean hasOutput) {
		super(maxStorage, hasInput, hasOutput);
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return 0;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		if (canExtract()) {
			return getEnergyStored() <= maxExtract ? getEnergyStored() : maxExtract;
		}
		return 0;
	}

	@Override
	public int getEnergyStored() {
		return getMaxEnergyStored();
	}

}
