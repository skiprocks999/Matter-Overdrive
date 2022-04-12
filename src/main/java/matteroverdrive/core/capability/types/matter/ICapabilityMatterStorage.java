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

	int receiveMatter(int maxReceive, boolean simulate);
	
	int extractMatter(int maxExtract, boolean simulate);

    int getMatterStored();

    int getMaxMatterStored();

    boolean canExtract();

    boolean canReceive();
}
