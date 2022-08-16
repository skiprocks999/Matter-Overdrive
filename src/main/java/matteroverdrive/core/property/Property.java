package matteroverdrive.core.property;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This is our Property object class.
 * It holds a reference to the {@link PropertyType} itself.
 * Then optionally a {@link Supplier<T>} and {@link Consumer<T>} for getting and setting the object value.
 *
 * @param <T> The T type value object for the property.
 */
public final class Property<T> {

  /**
   * The Type of the {@link Property}
   */
  private final PropertyType<T> propertyType;

  /**
   * The optional getter for the {@link Property}.
   */
  private final Supplier<T> getter;

  /**
   * The optional setter for the {@link Property}.
   */
  private final Consumer<T> setter;

  /**
   * The currently known value state of the {@link Property}.
   */
  private T currentValue;

  /**
   * The previously known value state of the {@link Property}.
   */
  private T lastKnownValue;

  /**
   * Default Empty Constructor
   * This constructor is used currently for Client-Side Construction of Container Properties.
   *
   * @param propertyType The T {@link PropertyType} of the {@link Property}.
   */
  public Property(PropertyType<T> propertyType) {
    this.propertyType = propertyType;
    this.getter = () -> currentValue;
    this.setter = value -> currentValue = value;
  }

  /**
   * Default Filled Constructor
   * This constructor is used for common-sided construction of properties as well as
   *
   * @param propertyType The T {@link PropertyType} of the {@link Property}.
   * @param getter The {@link Supplier<T>} getter of the {@link Property}.
   * @param setter The {@link Consumer<T>} setter of the {@link Property}
   */
  public Property(PropertyType<T> propertyType, Supplier<T> getter, Consumer<T> setter) {
    this.propertyType = propertyType;
    this.getter = getter;
    this.setter = setter;
  }

  /**
   * Checks if the {@link Property} object is dirty and in need of update.
   * @return Returns if the {@link Property} is Dirty (changed) and needs updating.
   */
  public boolean isDirty() {
    T value = this.getter.get();
    boolean dirty = lastKnownValue == null || !propertyType.getEquals().test(value, lastKnownValue);
    this.lastKnownValue = value;
    return dirty;
  }

  /**
   * Gets the {@link Property} T value.
   *
   * @return Returns the value of T.
   */
  @Nullable
  public T get() {
    return getter.get();
  }

  /**
   * Gets the {@link Property} T value or an replacement.
   *
   * @param other Replacement value if T is null.
   * @return Returns either T or Replacement.
   */
  @Nonnull
  public T getOrElse(T other) {
    T gotten = getter.get();
    if (gotten != null) {
      return gotten;
    } else {
      return other;
    }
  }

  /**
   * Sets the {@link Property} T value.
   *
   * @param value The T value to set the {@link Property} T value to.
   */
  public void set(T value) {
    this.setter.accept(value);
  }

  /**
   * Gets the T-valued {@link PropertyType} of the {@link Property}.
   *
   * @return Returns the T-valued {@link PropertyType} of the {@link Property}.
   */
  public PropertyType<T> getPropertyType() {
    return this.propertyType;
  }
}
