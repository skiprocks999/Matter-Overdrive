package matteroverdrive.core.property.manager;

import matteroverdrive.core.packet.NetworkHandler;
import matteroverdrive.core.packet.type.clientbound.property.PacketUpdateClientEntityProperty;
import matteroverdrive.core.packet.type.serverbound.property.PacketUpdateServerEntityProperty;
import matteroverdrive.core.property.Property;
import matteroverdrive.core.property.PropertyManager;
import matteroverdrive.core.property.PropertyType;
import net.minecraft.world.entity.Entity;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

public class EntityPropertyManager extends PropertyManager {

	/**
	 * The integer entityId of the entity.
	 */
	private final int entityId;

	/**
	 * Entity PropertyManager Constructor
	 *
	 * @param entityId The integer id of the entity.
	 */
	public EntityPropertyManager(int entityId) {
		super(Lists.newArrayList());
		this.entityId = entityId;
	}

	/**
	 * Client -> Server update method.
	 *
	 * @param property Property to update.
	 * @param value    The value to update the property with.
	 * @param <T>      The T value type.
	 */
	public <T> void updateServerEntity(Property<T> property, T value) {
		short propertyId = -1;
		for (short i = 0; i < properties.size(); i++) {
			if (properties.get(i) == property) {
				propertyId = i;
			}
		}
		property.set(value);
		NetworkHandler.sendUpdateServerEntityProperties(
				new PacketUpdateServerEntityProperty(entityId, property.getPropertyType(), propertyId, value));
	}

	/**
	 * Server -> Client update method.
	 *
	 * @param entity The integer id of the {@link Entity}.
	 */
	public void sendEntityChanges(Entity entity) {
		List<Triple<PropertyType<?>, Short, Object>> dirtyProperties = Lists.newArrayList();
		for (short i = 0; i < properties.size(); i++) {
			Property<?> property = properties.get(i);
			if (property.isDirty()) {
				dirtyProperties.add(Triple.of(property.getPropertyType(), i, property.get()));
			}
		}

		if (!dirtyProperties.isEmpty()) {
			NetworkHandler.sendUpdateClientEntityProperties(entity,
					new PacketUpdateClientEntityProperty(entityId, dirtyProperties));
		}
	}
}
