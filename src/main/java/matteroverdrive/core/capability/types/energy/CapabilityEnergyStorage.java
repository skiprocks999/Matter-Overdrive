package matteroverdrive.core.capability.types.energy;

import matteroverdrive.core.capability.IOverdriveCapability;
import matteroverdrive.core.capability.types.CapabilityType;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

public class CapabilityEnergyStorage implements IEnergyStorage, IOverdriveCapability {

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompoundTag serializeNBT() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> boolean matchesCapability(Capability<T> capability) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> LazyOptional<T> castHolder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getEnergyStored() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxEnergyStored() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean canExtract() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canReceive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLoad(BlockEntity tile) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CapabilityType getCapabilityType() {
		return CapabilityType.Energy;
	}

}
