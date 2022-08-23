package matteroverdrive.core.capability.types.android;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AndroidEnergyProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {

  private final AndroidEnergy energyCapability = new AndroidEnergy(AndroidEnergy.DEFAULT_ENERGY);

  private final LazyOptional<IEnergyStorage> optional = LazyOptional.of(() -> energyCapability);

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
    return cap == CapabilityEnergy.ENERGY ? optional.cast() : LazyOptional.empty();
  }

  @Override
  public CompoundTag serializeNBT() {
    CompoundTag CompoundTag = new CompoundTag();
    CompoundTag.putInt("Energy", energyCapability.getEnergyStored());
    return CompoundTag;
  }

  @Override
  public void deserializeNBT(CompoundTag nbt) {
    energyCapability.setEnergy(nbt.getInt("Energy"));
  }
}
