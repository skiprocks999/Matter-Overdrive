package matteroverdrive.core.component.util.property;

import matteroverdrive.MatterOverdrive;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.*;

public class PropertyType<T> implements Comparable<PropertyType<?>> {
	private final String name;
	private final Class<T> tClass;
	private final Function<FriendlyByteBuf, T> reader;
	private final BiConsumer<FriendlyByteBuf, T> writer;
	private final BiPredicate<T, T> equals;

	public PropertyType(String name, Class<T> tClass, Function<FriendlyByteBuf, T> reader,
			BiConsumer<FriendlyByteBuf, T> writer) {
		this(name, tClass, reader, writer, Objects::equals);
	}

	public PropertyType(String name, Class<T> tClass, Function<FriendlyByteBuf, T> reader,
			BiConsumer<FriendlyByteBuf, T> writer, BiPredicate<T, T> equals) {
		this.name = name;
		this.tClass = tClass;
		this.reader = reader;
		this.writer = writer;
		this.equals = equals;
	}

	public Function<FriendlyByteBuf, T> getReader() {
		return reader;
	}

	public BiConsumer<FriendlyByteBuf, T> getWriter() {
		return writer;
	}

	public BiPredicate<T, T> getEquals() {
		return equals;
	}

	public String getName() {
		return name;
	}

	public Property<T> create() {
		return new Property<>(this);
	}

	public Property<T> create(Supplier<T> getter) {
		return new Property<>(this, getter, value -> {
		});
	}

	public Property<T> create(Supplier<T> getter, Consumer<T> setter) {
		return new Property<>(this, getter, setter);
	}

	public boolean isValid(Object object) {
		return tClass.isInstance(object);
	}

	public void attemptWrite(FriendlyByteBuf packetBuffer, Object object) {
		if (tClass.isInstance(object)) {
			this.getWriter().accept(packetBuffer, tClass.cast(object));
		} else {
			MatterOverdrive.LOGGER.error("Attempted to Write with Invalid Object.");
		}
	}

	@SuppressWarnings("unchecked")
	public void attemptSet(Object object, Property<?> property) {
		if (property.getPropertyType() == this) {
			if (tClass.isInstance(object)) {
				try {
					((Property<T>) property).set(tClass.cast(object));
				} catch (ClassCastException classCastException) {
					MatterOverdrive.LOGGER.error("Failed to Set Container Property", classCastException);
				}
			}
		}
	}

	@Override
	public int compareTo(PropertyType<?> o) {
		return String.CASE_INSENSITIVE_ORDER.compare(this.getName(), o.getName());
	}
}
