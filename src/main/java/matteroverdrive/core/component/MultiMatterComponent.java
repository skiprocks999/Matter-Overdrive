package matteroverdrive.core.component;

import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.api.client.IScreenAddonProvider;
import com.hrznstudio.titanium.component.IComponentHandler;
import com.hrznstudio.titanium.component.IComponentHarness;
import com.hrznstudio.titanium.component.sideness.ICapabilityHolder;
import com.hrznstudio.titanium.component.sideness.IFacingComponent;
import com.hrznstudio.titanium.container.addon.IContainerAddon;
import com.hrznstudio.titanium.container.addon.IContainerAddonProvider;
import com.hrznstudio.titanium.util.FacingUtil;
import matteroverdrive.core.capability.types.matter.ICapabilityMatterStorage;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MultiMatterComponent<T extends IComponentHarness> implements IScreenAddonProvider, IContainerAddonProvider,
        ICapabilityHolder<MultiMatterComponent.MultiMatterCapabilityHandler<T>>, IComponentHandler {

  private final LinkedHashSet<?> matterStorages;
  private final HashMap<FacingUtil.Sideness, LazyOptional<MultiMatterCapabilityHandler<T>>> lazyOptionals;

  public MultiMatterComponent() {
    matterStorages = new LinkedHashSet<>();
    this.lazyOptionals = new HashMap<>();
    lazyOptionals.put(null, LazyOptional.empty());
    for (FacingUtil.Sideness sideness : FacingUtil.Sideness.values()) {
      lazyOptionals.put(sideness, LazyOptional.empty());
    }
  }

  @Override
  public void add(Object... component) {
    Arrays.stream(component).filter(this::accepts).forEach(storage -> {
      this.matterStorages.add((FluidTankComponent<T>) storage);
      rebuildCapability(new FacingUtil.Sideness[]{null});
      rebuildCapability(FacingUtil.Sideness.values());
    });
  }

  private boolean accepts(Object component) {
    return component instanceof FluidTankComponent<T>;
  }

  private void rebuildCapability(FacingUtil.Sideness[] sides) {
    for (FacingUtil.Sideness side : sides) {
      lazyOptionals.get(side).invalidate();
      lazyOptionals.put(side, LazyOptional.of(() -> new MultiMatterCapabilityHandler<>(getHandlersForSide(side))));
    }
  }

  private List<FluidTankComponent<T>> getHandlersForSide(FacingUtil.Sideness sideness) {
    if (sideness == null) {
      return new ArrayList<>(tanks);
    }
    List<FluidTankComponent<T>> handlers = new ArrayList<>();
    for (FluidTankComponent<T> tankHandler : tanks) {
      if (tankHandler instanceof IFacingComponent) {
        if (((IFacingComponent) tankHandler).getFacingModes().containsKey(sideness) &&
                ((IFacingComponent) tankHandler).getFacingModes().get(sideness).allowsConnection()) {
          handlers.add(tankHandler);
        }
      } else {
        handlers.add(tankHandler);
      }
    }
    return handlers;
  }


  @NotNull
  @Override
  public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
    return null;
  }



  @NotNull
  @Override
  public LazyOptional<MultiMatterCapabilityHandler<T>> getCapabilityForSide(@Nullable FacingUtil.Sideness sideness) {
    return null;
  }

  @Override
  public boolean handleFacingChange(String handlerName, FacingUtil.Sideness facing, int mode) {
    return false;
  }

  @Override
  public Collection<LazyOptional<MultiMatterCapabilityHandler<T>>> getLazyOptionals() {
    return null;
  }

  @NotNull
  @Override
  public List<IFactory<? extends IContainerAddon>> getContainerAddons() {
    return null;
  }

  public static class MultiMatterCapabilityHandler<T extends IComponentHarness> implements ICapabilityMatterStorage {

    private final List<?> matterStorages;

    public MultiMatterCapabilityHandler(List<?> matterStorages) {
      this.matterStorages = matterStorages;
    }

    @Override
    public double receiveMatter(double maxReceive, boolean simulate) {
      return 0;
    }

    @Override
    public double extractMatter(double maxExtract, boolean simulate) {
      return 0;
    }

    @Override
    public double getMatterStored() {
      return 0;
    }

    @Override
    public double getMaxMatterStored() {
      return 0;
    }

    @Override
    public boolean canExtract() {
      return false;
    }

    @Override
    public boolean canReceive() {
      return false;
    }
  }
}
