package matteroverdrive.core.capability.types.matter;

public class CapabilityCreativeMatterStorage extends CapabilityMatterStorage {

	public CapabilityCreativeMatterStorage(double maxStorage, boolean hasInput, boolean hasOutput) {
		super(maxStorage, hasInput, hasOutput);
	}

	@Override
	public double receiveMatter(double maxReceive, boolean simulate) {
		return 0;
	}

	@Override
	public double extractMatter(double maxExtract, boolean simulate) {
		if (canExtract()) {
			return getMatterStored() <= maxExtract ? getMatterStored() : maxExtract;
		}
		return 0;
	}

	@Override
	public double getMatterStored() {
		return getMaxMatterStored();
	}

}
