package matteroverdrive.core.capability.types.fluid;

import matteroverdrive.core.capability.IOverdriveCapability;
import matteroverdrive.core.capability.types.CapabilityType;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class CapabilityFluidHandlerSimple implements IFluidHandler, IOverdriveCapability {

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
	public int getTanks() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTankCapacity(int tank) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onLoad(BlockEntity tile) {
		// TODO Auto-generated method stub

	}

	@Override
	public CapabilityType getCapabilityType() {
		return CapabilityType.Fluid;
	}

	@Override
	public void invalidateCapability() {
		// TODO Auto-generated method stub

	}

	@Override
	public void refreshCapability() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getSaveKey() {
		// TODO Auto-generated method stub
		return null;
	}

}
