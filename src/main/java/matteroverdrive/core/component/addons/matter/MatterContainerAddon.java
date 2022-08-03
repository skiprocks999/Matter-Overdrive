package matteroverdrive.core.component.addons.matter;

import com.hrznstudio.titanium.block.tile.BasicTile;
import com.hrznstudio.titanium.component.IComponentHarness;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.matter.ICapabilityMatterStorage;
import matteroverdrive.core.component.container.PropertyHolderAddon;
import matteroverdrive.core.component.util.property.Property;
import matteroverdrive.core.component.util.property.PropertyTypes;

public class MatterContainerAddon extends PropertyHolderAddon {

  private final Property<Double> matter;

  public MatterContainerAddon(IComponentHarness harness) {
    this.matter = addProperty(PropertyTypes.DOUBLE, () -> this.getStoredMatter(harness), (amount) -> this.setStoredMatter(harness, amount));
  }

  public Property<Double> getMatter() {
    return matter;
  }

  private double getStoredMatter(IComponentHarness harness) {
    if (harness instanceof BasicTile<?> tile && tile.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE).isPresent()) {
      this.matter.set(tile.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE).map(ICapabilityMatterStorage::getMatterStored).orElse(0D));
      return this.matter.get();
    }
    return 0;
  }

  private void setStoredMatter(IComponentHarness harness, double amount) {
    if (harness instanceof BasicTile<?> tile && tile.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE).isPresent()) {
      this.matter.set(amount);
      tile.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE).ifPresent(storage -> storage.setMatterStored(amount));
    }
  }

}
