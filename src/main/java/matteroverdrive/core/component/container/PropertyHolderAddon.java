package matteroverdrive.core.component.container;

import com.hrznstudio.titanium.container.addon.IContainerAddon;
import matteroverdrive.core.component.util.property.Property;
import matteroverdrive.core.component.util.property.PropertyType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PropertyHolderAddon implements IContainerAddon, IPropertyHolder {
  private final List<Property<?>> properties;

  public PropertyHolderAddon() {
    this.properties = new ArrayList<>();
  }

  @Override
  public List<Property<?>> getProperties() {
    return properties;
  }

  public <T> Property<T> addProperty(PropertyType<T> property, Supplier<T> getter, Consumer<T> setter) {
    Property<T> prop = property.create(getter, setter);
    this.properties.add(prop);
    return prop;
  }

}
