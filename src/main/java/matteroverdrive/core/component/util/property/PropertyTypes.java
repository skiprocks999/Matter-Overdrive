package matteroverdrive.core.component.util.property;

import com.google.common.collect.Lists;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class PropertyTypes {
  private static final List<PropertyType<?>> types = Lists.newArrayList();

  public static PropertyType<FluidStack> FLUID_STACK = addType("fluid_stack", FluidStack.class,
          FriendlyByteBuf::readFluidStack, FriendlyByteBuf::writeFluidStack, FluidStack::isFluidEqual);
  public static PropertyType<Boolean> BOOLEAN = addType("boolean", Boolean.class, FriendlyByteBuf::readBoolean,
          FriendlyByteBuf::writeBoolean);
  public static PropertyType<Integer> INTEGER = addType("integer", Integer.class, FriendlyByteBuf::readInt,
          FriendlyByteBuf::writeInt);
  public static PropertyType<Double> DOUBLE = addType("double", Double.class, FriendlyByteBuf::readDouble,
          FriendlyByteBuf::writeDouble);

  public static <T> PropertyType<T> addType(String name, Class<T> tClass, Function<FriendlyByteBuf, T> reader,
                                            BiConsumer<FriendlyByteBuf, T> writer) {
    return addType(new PropertyType<>(name, tClass, reader, writer));
  }

  public static <T> PropertyType<T> addType(String name, Class<T> tClass, Function<FriendlyByteBuf, T> reader,
                                            BiConsumer<FriendlyByteBuf, T> writer, BiPredicate<T, T> equals) {
    return addType(new PropertyType<>(name, tClass, reader, writer, equals));
  }

  public static <T> PropertyType<T> addType(PropertyType<T> type) {
    types.add(type);
    types.sort(PropertyType::compareTo);
    return type;
  }

  public static short getIndex(PropertyType<?> propertyType) {
    return (short) types.indexOf(propertyType);
  }

  public static PropertyType<?> getByIndex(short index) {
    return types.get(index);
  }
}
