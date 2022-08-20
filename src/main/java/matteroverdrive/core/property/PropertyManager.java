package matteroverdrive.core.property;

import java.util.List;

public abstract class PropertyManager {

	/**
	 * List of internally stored {@link Property} values.
	 */
	protected final List<Property<?>> properties;

	/**
	 * Default Constructor. This in most cases should be defaulted and passed an
	 * empty list.
	 *
	 * @param properties A storage list that holds {@link Property} values.
	 */
	public PropertyManager(List<Property<?>> properties) {
		this.properties = properties;
	}

	/**
	 * Adds a tracked {@link Property} value to the internally stored list.
	 *
	 * @param property The {@link Property} to be added.
	 * @param <T>      The T type of the property.
	 * @return Returns the {@link Property} itself for field storage.
	 */
	public <T> Property<T> addTrackedProperty(Property<T> property) {
		this.properties.add(property);
		return property;
	}

	/**
	 * Updates the stored {@link Property} value.
	 *
	 * @param propertyType The {@link PropertyType} of the {@link Property}.
	 * @param propertyId   The short-numeric id of the stored {@link Property}.
	 * @param value        The T object value of the {@link Property}.
	 */
	public void update(PropertyType<?> propertyType, short propertyId, Object value) {
		if (propertyId < properties.size()) {
			Property<?> property = properties.get(propertyId);
			if (property != null && property.getPropertyType() == propertyType) {
				propertyType.attemptSet(value, property);
			}
		}
	}
}
