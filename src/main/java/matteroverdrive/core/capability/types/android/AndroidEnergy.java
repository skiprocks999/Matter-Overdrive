package matteroverdrive.core.capability.types.android;

import matteroverdrive.core.packet.NetworkHandler;
import matteroverdrive.core.packet.type.clientbound.android.PacketAndroidEnergySync;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class AndroidEnergy implements IEnergyStorage, ICapabilityProvider {

  private final LazyOptional<IEnergyStorage> holder = LazyOptional.of(() -> this);

  public static int DEFAULT_ENERGY = 100_000;
  protected int energy;
  protected int capacity;
  protected int maxReceive;
  protected int maxExtract;

  public AndroidEnergy(int capacity) {
    this(capacity, capacity, capacity, 0);
  }

  public AndroidEnergy(int capacity, int maxTransfer) {
    this(capacity, maxTransfer, maxTransfer, 0);
  }

  public AndroidEnergy(int capacity, int maxReceive, int maxExtract) {
    this(capacity, maxReceive, maxExtract, 0);
  }

  public AndroidEnergy(int capacity, int maxReceive, int maxExtract, int energy) {
    this.capacity = capacity;
    this.maxReceive = maxReceive;
    this.maxExtract = maxExtract;
    this.energy = Math.max(0, Math.min(capacity, energy));
  }

  public static void syncEnergy(ServerPlayer entity) {
    entity.getCapability(CapabilityEnergy.ENERGY).ifPresent(energyStorage -> {
      NetworkHandler.sendToClientPlayer(entity, new PacketAndroidEnergySync(energyStorage.getEnergyStored(), energyStorage.getMaxEnergyStored()));
    });
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    if (!canReceive())
      return 0;

    int energyReceived = Math.min(getMaxEnergyStored() - energy, Math.min(this.maxReceive, maxReceive));
    if (!simulate)
      energy += energyReceived;
    return energyReceived;
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    if (!canExtract())
      return 0;

    int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
    if (!simulate)
      energy -= energyExtracted;
    return energyExtracted;
  }

  @Override
  public int getEnergyStored() {
    return energy;
  }

  @Override
  public int getMaxEnergyStored() {
    return capacity;
  }

  @Override
  public boolean canExtract() {
    return this.maxExtract > 0;
  }

  @Override
  public boolean canReceive() {
    return this.maxReceive > 0;
  }

  public void setEnergy(int energy) {
    this.energy = energy;
  }

  @Override
  public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
    return CapabilityEnergy.ENERGY.orEmpty(cap, holder);
  }
}
