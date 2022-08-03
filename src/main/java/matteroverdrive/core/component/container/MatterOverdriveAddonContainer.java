package matteroverdrive.core.component.container;

import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.container.BasicAddonContainer;
import com.hrznstudio.titanium.container.addon.IContainerAddonProvider;
import com.hrznstudio.titanium.network.locator.LocatorInstance;
import matteroverdrive.core.component.util.property.IPropertyManaged;
import matteroverdrive.core.component.util.property.PropertyManager;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.ObjectHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class MatterOverdriveAddonContainer extends BasicAddonContainer implements IPropertyManaged {
  @ObjectHolder(registryName = "minecraft:menu", value = "matteroverdrive:addon_container")
  public static MenuType<MatterOverdriveAddonContainer> TYPE;

  private final PropertyManager propertyManager;

  public MatterOverdriveAddonContainer(Object provider, LocatorInstance locatorInstance, ContainerLevelAccess worldPosCallable, Inventory playerInventory, int containerId) {
    super(provider, locatorInstance, TYPE, worldPosCallable, playerInventory, containerId);
    this.propertyManager = new PropertyManager((short) containerId);
    if (this.getProvider() instanceof IContainerAddonProvider addonProvider) {
      addonProvider.getContainerAddons()
              .stream()
              .map(IFactory::create)
              .forEach(addon -> {
                if (addon instanceof PropertyHolderAddon propHolder) {
                  propHolder.getProperties().forEach(propertyManager::addTrackedProperty);
                }
              });
    }
  }

  @Override
  public void addSlotListener(@NotNull ContainerListener listener) {
    super.addSlotListener(listener);
    this.propertyManager.sendChanges(Collections.singletonList(listener), true);
  }

  @Override
  public void broadcastChanges() {
    super.broadcastChanges();
    this.getPropertyManager().sendChanges(this.containerListeners, false);
  }

  @Override
  public PropertyManager getPropertyManager() {
    return this.propertyManager;
  }

}
