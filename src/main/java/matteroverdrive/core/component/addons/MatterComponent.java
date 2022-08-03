package matteroverdrive.core.component.addons;

import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.api.client.IScreenAddonProvider;
import com.hrznstudio.titanium.component.IComponentHarness;
import com.hrznstudio.titanium.container.addon.IContainerAddon;
import com.hrznstudio.titanium.container.addon.IContainerAddonProvider;
import com.hrznstudio.titanium.container.addon.IntReferenceHolderAddon;
import com.hrznstudio.titanium.container.referenceholder.FunctionReferenceHolder;
import matteroverdrive.core.capability.types.matter.CapabilityMatterStorage;
import matteroverdrive.core.capability.types.matter.ICapabilityMatterStorage;
import matteroverdrive.core.component.addons.matter.MatterContainerAddon;
import matteroverdrive.core.component.addons.matter.MatterScreenAddon;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MatterComponent<T extends IComponentHarness>
        extends CapabilityMatterStorage
        implements IScreenAddonProvider, IContainerAddonProvider, INBTSerializable<CompoundTag> {

  private final int posX;
  private final int posY;
  private String name;
  private T componentHarness;
  private Action action;

  public MatterComponent(double maxStorage, int posX, int posY, String name, Action defaultAction) {
    super(maxStorage, defaultAction.canFill(), defaultAction.canDrain());
    this.posX = posX;
    this.posY = posY;
    this.name = name;
    this.action = defaultAction;
  }

  public MatterComponent<T> setComponentHarness(T componentHarness) {
    this.componentHarness = componentHarness;
    return this;
  }

  public T getComponentHarness() {
    return componentHarness;
  }

  @Override
  public double receiveMatter(double maxReceive, boolean simulate) {
    return super.receiveMatter(maxReceive, simulate);
  }

  @Override
  public double extractMatter(double maxExtract, boolean simulate) {
    return super.extractMatter(maxExtract, simulate);
  }

  @Override
  public boolean canExtract() {
    return this.action.canDrain();
  }

  @Override
  public boolean canReceive() {
    return this.action.canFill();
  }

  @NotNull
  @Override
  public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
    List<IFactory<? extends IScreenAddon>> addons = new ArrayList<>();
    addons.add(() -> new MatterScreenAddon(posX, posY, this));
    return addons;
  }

  @NotNull
  @Override
  public List<IFactory<? extends IContainerAddon>> getContainerAddons() {
    List<IFactory<? extends IContainerAddon>> addons = new ArrayList<>();
    addons.add(() -> new MatterContainerAddon(getComponentHarness()));
    return addons;
  }

  public String getName() {
    return name;
  }

  public enum Action {
    FILL(true, false),
    DRAIN(false, true),
    BOTH(true, true),
    NONE(false, false);

    private final boolean fill;
    private final boolean drain;

    Action(boolean fill, boolean drain) {
      this.fill = fill;
      this.drain = drain;
    }

    public boolean canFill() {
      return this.fill;
    }

    public boolean canDrain() {
      return this.drain;
    }
  }

}
