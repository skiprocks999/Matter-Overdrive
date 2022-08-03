package matteroverdrive.core.component.container;

import com.hrznstudio.titanium.component.IComponentHarness;
import com.hrznstudio.titanium.container.addon.IContainerAddon;
import matteroverdrive.core.component.util.property.Property;
import matteroverdrive.core.component.util.property.PropertyType;
import matteroverdrive.core.component.util.property.PropertyTypes;

public abstract class PropertyAddon<T extends PropertyType> implements IContainerAddon {

  private final Property<T> property;

  public PropertyAddon(T IComponentHarness harness) {
    this.property = T


}
