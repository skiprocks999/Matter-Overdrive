package matteroverdrive.common.tile;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.inventory.InventoryComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import matteroverdrive.DeferredRegisters;
import matteroverdrive.core.tile.GenericRedstoneSoundTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

public class TileCharger extends GenericRedstoneSoundTile<TileCharger> {

  public static final int CHARGE_RATE = 512;
  public static final int ENERGY_STORAGE = 512000;
  public static final int DEFAULT_RADIUS = 8;

  @Save
  private int usage = CHARGE_RATE;
  @Save
  private int radius = DEFAULT_RADIUS;
  @Save
  private boolean running = false;

  @Save
  private EnergyStorageComponent<TileCharger> energyStorageComponent;
  @Save
  private SidedInventoryComponent<TileCharger> input;
  @Save
  private SidedInventoryComponent<TileCharger> output;

  public TileCharger(BlockPos pos, BlockState state) {
    super(DeferredRegisters.BLOCK_CHARGER.get(), DeferredRegisters.TILE_CHARGER.get(), pos, state);
    this.energyStorageComponent = new EnergyStorageComponent<>(ENERGY_STORAGE, 4, 10);
    this.addInventory(input = (SidedInventoryComponent<TileCharger>) new SidedInventoryComponent<TileCharger>("input", 10, 20, 1, 0)
            .setInputFilter((stack, slot) -> {
              if (stack.getCapability(CapabilityEnergy.ENERGY).isPresent()) {
                LazyOptional<IEnergyStorage> lazyStorage = stack.getCapability(CapabilityEnergy.ENERGY).cast();
                if (lazyStorage.isPresent() && lazyStorage.resolve().isPresent()) {
                  IEnergyStorage storage = lazyStorage.resolve().get();
                  return storage.getEnergyStored() < storage.getMaxEnergyStored();
                }
              }
              return false;
            })
            .setOutputFilter((stack, slot) -> false)
            .setComponentHarness(this)
    );
    this.addInventory(output = (SidedInventoryComponent<TileCharger>) new SidedInventoryComponent<TileCharger>("output", 30, 20, 1, 0)
            .setInputFilter((stack, slot) -> false)
            .setOutputFilter((stack, slot) -> false)
            .setComponentHarness(this)
    );
    this.input.setColor(DyeColor.CYAN);
    this.output.setColor(DyeColor.RED);
  }

  @Override
  public void serverTick(Level level, BlockPos pos, BlockState state, TileCharger blockEntity) {
    super.serverTick(level, pos, state, blockEntity);
    ItemStack stack = input.getStackInSlot(0);
    if (stack.getCapability(CapabilityEnergy.ENERGY).isPresent()) {
      LazyOptional<IEnergyStorage> lazyStorage = stack.getCapability(CapabilityEnergy.ENERGY).cast();
      if (lazyStorage.isPresent() && lazyStorage.resolve().isPresent()) {
        IEnergyStorage energyStorage = lazyStorage.resolve().get();
        boolean canReceive = energyStorage.canReceive() &&
                energyStorageComponent.canExtract() &&
                energyStorage.receiveEnergy(energyStorageComponent.extractEnergy(usage, true), true) > 0;
        if (canReceive) {
          energyStorage.receiveEnergy(energyStorageComponent.extractEnergy(usage, false), false);
          this.markComponentForUpdate(true);
        }
      }
    }
  }

  @NotNull
  @Override
  public TileCharger getSelf() {
    return this;
  }

  @Override
  public Component getName() {
    return Component.literal("Charger"); // TODO: Temporary, Fix this
  }

  @NotNull
  @Override
  protected EnergyStorageComponent<TileCharger> createEnergyStorage() {
    return new EnergyStorageComponent<>(ENERGY_STORAGE, 20, 20);
  }
}
