package matteroverdrive.core.property;

import matteroverdrive.MatterOverdrive;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Objects;
import java.util.function.*;

public class PropertyType<T> implements Comparable<PropertyType<?>> {

  /**
   * The internal name of the {@link PropertyType}.
   */
  private final String name;

  /**
   * The internal T value class of the {@link PropertyType}.
   */
  private final Class<T> tClass;

  /**
   * The internal {@link Function} for reading the value from a {@link FriendlyByteBuf}.
   */
  private final Function<FriendlyByteBuf, T> reader;

  /**
   * The internal {@link BiConsumer} for writing the value to a {@link FriendlyByteBuf}.
   */
  private final BiConsumer<FriendlyByteBuf, T> writer;

  /**
   * The internal {@link BiPredicate} comparison object.
   */
  private final BiPredicate<T, T> equals;

  /**
   * Default Constructor with a straight Objects::equals comparison.
   *
   * @param name The internal name for the {@link PropertyType}.
   * @param tClass The internal T class reference for the {@link PropertyType}.
   * @param reader The internal {@link Function} for reading the value from a {@link FriendlyByteBuf}.
   * @param writer
   */
  public PropertyType(String name, Class<T> tClass, Function<FriendlyByteBuf, T> reader,
                      BiConsumer<FriendlyByteBuf, T> writer) {
    this(name, tClass, reader, writer, Objects::equals);
  }

  /**
   * Default Constructor with a custom comparison predicate.
   *
   * @param name The internal name for the {@link PropertyType}.
   * @param tClass The internal T class reference for the {@link PropertyType}.
   * @param reader The internal {@link Function} for reading the value from a {@link FriendlyByteBuf}.
   * @param writer The internal {@link BiConsumer} for writing the value to a {@link FriendlyByteBuf}.
   * @param equals The internal {@link BiPredicate} comparison object.
   */
  public PropertyType(String name, Class<T> tClass, Function<FriendlyByteBuf, T> reader,
                      BiConsumer<FriendlyByteBuf, T> writer, BiPredicate<T, T> equals) {
    this.name = name;
    this.tClass = tClass;
    this.reader = reader;
    this.writer = writer;
    this.equals = equals;
  }

  /**
   * Gets the reader {@link Function} object for the {@link PropertyType}.
   *
   * @return Returns the {@link Function} reader object.
   */
  public Function<FriendlyByteBuf, T> getReader() {
    return reader;
  }

  /**
   * Gets the writer {@link BiConsumer} object for the {@link PropertyType}.
   *
   * @return Returns the {@link BiConsumer} writer object.
   */
  public BiConsumer<FriendlyByteBuf, T> getWriter() {
    return writer;
  }

  /**
   * Gets the comparitor {@link BiPredicate} object for the {@link PropertyType}.
   *
   * @return Returns the {@link BiPredicate} comparitor object.
   */
  public BiPredicate<T, T> getEquals() {
    return equals;
  }

  /**
   * Gets the internal {@link PropertyType} name.
   *
   * @return Returns the internal reference name of the {@link PropertyType}.
   */
  public String getName() {
    return name;
  }

  /**
   * Create a new Property for this type.
   *
   * @return Returns a Property of type T matching this {@link PropertyType}'s T.
   */
  public Property<T> create() {
    return new Property<>(this);
  }

  /**
   * Create a new Property for this type with provided getter.
   *
   * @param getter Getter for the Property.
   * @return Returns a Property of type T matching this {@link PropertyType}'s T.
   */
  public Property<T> create(Supplier<T> getter) {
    return new Property<>(this, getter, value -> {
    });
  }

  /**
   * Create a new Property for this type with provided getter and setter.
   *
   * @param getter Getter for the Property.
   * @param setter Setter for the Property.
   * @return Returns a Property of type T matching this {@link PropertyType}'s T.
   */
  public Property<T> create(Supplier<T> getter, Consumer<T> setter) {
    return new Property<>(this, getter, setter);
  }

  /**
   * Checks if the value of the passed Object is valid towards the T storage values T class value.
   *
   * @param object The passed in T value object.
   * @return Returns whether the passed in object is an instance of the {@link PropertyType} T values class.
   */
  public boolean isValid(Object object) {
    return tClass.isInstance(object);
  }

  /**
   * Attempts to write the object value to the {@link FriendlyByteBuf} buffer.
   *
   * @param packetBuffer The {@link FriendlyByteBuf} buffer being passed in.
   * @param object The T value object.
   */
  public void attemptWrite(FriendlyByteBuf packetBuffer, Object object) {
    if (isValid(object)) {
      this.getWriter().accept(packetBuffer, tClass.cast(object));
    } else {
      MatterOverdrive.LOGGER.error("Attempted to Write with Invalid Object.");
    }
  }

  /**
   * Attempts to set the T object value to the provided {@link Property}.
   *
   * @param object The T object value to set to the property.
   * @param property The property to attempt to set the value of.
   */
  @SuppressWarnings("unchecked")
  public void attemptSet(Object object, Property<?> property) {
    if (property.getPropertyType() == this) {
      if (isValid(object)) {
        try {
          ((Property<T>) property).set(tClass.cast(object));
        } catch (ClassCastException classCastException) {
          MatterOverdrive.LOGGER.error("Failed to Set Container Property", classCastException);
        }
      }
    }
  }

  /**
   * Default compareTo implementation.
   * By default, compares the name of this object and the provided {@link PropertyType} object using case_insensitive matching.
   *
   * @param o The object to be compared.
   * @return Returns 0 if it doesn't match, and 1 if it matches.
   */
  @Override
  public int compareTo(PropertyType<?> o) {
    return String.CASE_INSENSITIVE_ORDER.compare(this.getName(), o.getName());
  }
}
