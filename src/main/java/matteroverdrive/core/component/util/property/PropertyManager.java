package matteroverdrive.core.component.util.property;

import com.google.common.collect.Lists;
import matteroverdrive.core.component.util.property.message.UpdateClientContainerPropertiesMessage;
import matteroverdrive.core.component.util.property.message.UpdateServerContainerPropertyMessage;
import matteroverdrive.core.packet.NetworkHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerListener;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collection;
import java.util.List;

public class PropertyManager {
  private final List<Property<?>> properties;
  private final short windowId;

  public PropertyManager(short windowId) {
    this.windowId = windowId;
    this.properties = Lists.newArrayList();
  }

  public <T> Property<T> addTrackedProperty(Property<T> property) {
    this.properties.add(property);
    return property;
  }

  public <T> void updateServer(Property<T> property, T value) {
    short propertyId = -1;
    for (short i = 0; i < properties.size(); i++) {
      if (properties.get(i) == property) {
        propertyId = i;
      }
    }
    property.set(value);
    NetworkHandler.sendUpdateServerContainerProperties(
            new UpdateServerContainerPropertyMessage(windowId, property.getPropertyType(), propertyId, value));
  }

  public void sendChanges(Collection<ContainerListener> containerListeners, boolean firstTime) {
    List<ServerPlayer> playerListeners = Lists.newArrayList();
    for (ContainerListener listener : containerListeners) {
      if (listener instanceof ServerPlayer player) {
        playerListeners.add(player);
      }
    }

    if (!playerListeners.isEmpty()) {
      List<Triple<PropertyType<?>, Short, Object>> dirtyProperties = Lists.newArrayList();
      for (short i = 0; i < properties.size(); i++) {
        Property<?> property = properties.get(i);
        if (property.isDirty() || firstTime) {
          dirtyProperties.add(Triple.of(property.getPropertyType(), i, property.get()));
        }
      }

      if (!dirtyProperties.isEmpty()) {
        for (ServerPlayer playerEntity : playerListeners) {
          NetworkHandler.sendUpdateClientContainerProperties(playerEntity,
                  new UpdateClientContainerPropertiesMessage(windowId, dirtyProperties));
        }
      }
    }
  }

  public void update(PropertyType<?> propertyType, short propertyId, Object value) {
    if (propertyId < properties.size()) {
      Property<?> property = properties.get(propertyId);
      if (property != null && property.getPropertyType() == propertyType) {
        propertyType.attemptSet(value, property);
      }
    }
  }
}
