package matteroverdrive.core.capability.types.matter;

/**
 * 
 * Capability interface to interact with matter machines and items
 * 
 * Same concept as IEnergyStroage
 * 
 * @author skip999
 *
 */
public interface ICapabilityMatterStorage {

	double receiveMatter(double maxReceive, boolean simulate);

	double extractMatter(double maxExtract, boolean simulate);

	double getMatterStored();

	double getMaxMatterStored();

	boolean canExtract();

	boolean canReceive();
}
