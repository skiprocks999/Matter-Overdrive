package matteroverdrive.core.property;

import com.google.common.collect.Lists;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class PropertyTypes {

  /**
   * Internally stored list of {@link PropertyType}'s.
   */
  private static final List<PropertyType<?>> types = Lists.newArrayList();

  /**
   * Default {@link PropertyType} implementation for {@link FluidStack} values.
   */
  public static PropertyType<FluidStack> FLUID_STACK = addType("fluid_stack", FluidStack.class,
          FriendlyByteBuf::readFluidStack, FriendlyByteBuf::writeFluidStack, FluidStack::isFluidEqual);

  /**
   * Default {@link PropertyType} implementation for {@link Boolean} values.
   */
  public static PropertyType<Boolean> BOOLEAN = addType("boolean", Boolean.class, FriendlyByteBuf::readBoolean,
          FriendlyByteBuf::writeBoolean);

  /**
   * Default {@link PropertyType} implementation for {@link Integer} values.
   */
  public static PropertyType<Integer> INTEGER = addType("integer", Integer.class, FriendlyByteBuf::readInt,
          FriendlyByteBuf::writeInt);

  /**
   * Default {@link PropertyType} implementation for {@link Double} values.
   */
  public static PropertyType<Double> DOUBLE = addType("double", Double.class, FriendlyByteBuf::readDouble,
          FriendlyByteBuf::writeDouble);
  
  /**
   * Default {@link PropertyType} implementation for {@link Double} values.
   */
  public static PropertyType<Float> FLOAT = addType("float", Float.class, FriendlyByteBuf::readFloat,
          FriendlyByteBuf::writeFloat);
  
  public static PropertyType<CompoundTag> NBT = addType("nbt", CompoundTag.class, FriendlyByteBuf::readNbt,
          FriendlyByteBuf::writeNbt);

  /**
   * Add type method.
   *
   * @param name The internal name for the {@link PropertyType}.
   * @param tClass The internal T class reference for the {@link PropertyType}.
   * @param reader The internal {@link Function} for reading the value from a {@link FriendlyByteBuf}.
   * @param writer The internal {@link BiConsumer} for writing the value to a {@link FriendlyByteBuf}.
   * @param <T> T value type.
   * @return Returns a new {@link PropertyType} reference.
   */
  public static <T> PropertyType<T> addType(String name, Class<T> tClass, Function<FriendlyByteBuf, T> reader,
                                            BiConsumer<FriendlyByteBuf, T> writer) {
    return addType(new PropertyType<>(name, tClass, reader, writer));
  }

  /**
   * Add type method.
   *
   * @param name The internal name for the {@link PropertyType}.
   * @param tClass The internal T class reference for the {@link PropertyType}.
   * @param reader The internal {@link Function} for reading the value from a {@link FriendlyByteBuf}.
   * @param writer The internal {@link BiConsumer} for writing the value to a {@link FriendlyByteBuf}.
   * @param equals The internal {@link BiPredicate} comparison object.
   * @param <T> T value type.
   * @return Returns a new {@link PropertyType} reference.
   */
  public static <T> PropertyType<T> addType(String name, Class<T> tClass, Function<FriendlyByteBuf, T> reader,
                                            BiConsumer<FriendlyByteBuf, T> writer, BiPredicate<T, T> equals) {
    return addType(new PropertyType<>(name, tClass, reader, writer, equals));
  }

  /**
   * Default add type method.
   *
   * @param type The {@link PropertyType} to add.
   * @param <T> T value type.
   * @return Returns a new {@link PropertyType} reference.
   */
  public static <T> PropertyType<T> addType(PropertyType<T> type) {
    types.add(type);
    types.sort(PropertyType::compareTo);
    return type;
  }

  /**
   * Gets the index of the {@link PropertyType} in the internal list.
   *
   * @param propertyType The {@link PropertyType} you want the index of.
   * @return Returns the index of the corresponding to the provided {@link PropertyType}.
   */
  public static short getIndex(PropertyType<?> propertyType) {
    return (short) types.indexOf(propertyType);
  }

  /**
   * Gets the {@link PropertyType} from the internal list using an index.
   *
   * @param index The index of the {@link PropertyType} you want to get.
   * @return Returns the {@link PropertyType} corresponding to the index.
   */
  public static PropertyType<?> getByIndex(short index) {
    return types.get(index);
  }
}
