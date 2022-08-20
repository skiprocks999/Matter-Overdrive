package matteroverdrive.core.property.manager;

import matteroverdrive.core.packet.NetworkHandler;
import matteroverdrive.core.property.Property;
import matteroverdrive.core.property.PropertyManager;
import matteroverdrive.core.property.PropertyType;
import matteroverdrive.core.property.message.UpdateClientContainerPropertyMessage;
import matteroverdrive.core.property.message.UpdateServerContainerPropertyMessage;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerListener;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collection;
import java.util.List;

public class ContainerPropertyManager extends PropertyManager {

	/**
	 * The menuId of the Container/Menu.
	 */
	private final short menuId;

	/**
	 * Container PropertyManager Constructor
	 *
	 * @param menuId The id of the container.
	 */
	public ContainerPropertyManager(short menuId) {
		super(Lists.newArrayList());
		this.menuId = menuId;
	}

	/**
	 * Client -> Server update method.
	 *
	 * @param property Property to update.
	 * @param value    The value to update the property with.
	 * @param <T>      The T value type.
	 */
	public <T> void updateServerContainer(Property<T> property, T value) {
		short propertyId = -1;
		for (short i = 0; i < properties.size(); i++) {
			if (properties.get(i) == property) {
				propertyId = i;
			}
		}
		property.set(value);
		NetworkHandler.sendUpdateServerContainerProperties(
				new UpdateServerContainerPropertyMessage(menuId, property.getPropertyType(), propertyId, value));
	}

	/**
	 * Server -> Client update method.
	 *
	 * @param containerListeners The {@link ContainerListener}'s of the Container.
	 * @param firstTime          If this is the first sync for a new
	 *                           {@link ContainerListener} entering the
	 *                           container/menu.
	 */
	public void sendContainerChanges(Collection<ContainerListener> containerListeners, boolean firstTime) {
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
							new UpdateClientContainerPropertyMessage(menuId, dirtyProperties));
				}
			}
		}
	}

}
