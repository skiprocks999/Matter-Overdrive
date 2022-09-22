package matteroverdrive.core.eventhandler.manager;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.GenericEvent;


public class EventManager {

  public static <T extends Event> FilteredEventManager<T> forge(Class<T> clazz) {
    return create(clazz, EventPriority.NORMAL, Bus.FORGE);
  }

  public static <T extends Event> FilteredEventManager<T> mod(Class<T> clazz) {
    return create(clazz, EventPriority.NORMAL, Bus.MOD);
  }

  public static <T extends Event> FilteredEventManager<T> mod(Class<T> clazz, EventPriority priority) {
    return create(clazz, priority, Bus.MOD);
  }

  public static <T extends Event> FilteredEventManager<T> forge(Class<T> clazz, EventPriority priority) {
    return create(clazz, priority, Bus.FORGE);
  }

  public static <T extends Event> FilteredEventManager<T> create(Class<T> clazz, Bus bus) {
    return create(clazz, EventPriority.NORMAL, bus);
  }

  public static <T extends Event> FilteredEventManager<T> create(Class<T> clazz, EventPriority priority, Bus bus) {
    return new FilteredEventManager<>(clazz, bus, priority);
  }

  public static <T extends GenericEvent<? extends F>, F> GenericEventManager<T, F> forgeGeneric(Class<T> clazz, Class<F> generic) {
    return createGeneric(clazz, EventPriority.NORMAL, Bus.FORGE, generic);
  }

  public static <T extends GenericEvent<? extends F>, F> GenericEventManager<T, F> modGeneric(Class<T> clazz, Class<F> generic) {
    return createGeneric(clazz, EventPriority.NORMAL, Bus.MOD, generic);
  }

  public static <T extends GenericEvent<? extends F>, F> GenericEventManager<T, F> modGeneric(Class<T> clazz, EventPriority priority, Class<F> generic) {
    return createGeneric(clazz, priority, Bus.MOD, generic);
  }

  public static <T extends GenericEvent<? extends F>, F> GenericEventManager<T, F> forgeGeneric(Class<T> clazz, EventPriority priority, Class<F> generic) {
    return createGeneric(clazz, priority, Bus.FORGE, generic);
  }

  public static <T extends GenericEvent<? extends F>, F> GenericEventManager<T, F> createGeneric(Class<T> clazz, Bus bus, Class<F> generic) {
    return createGeneric(clazz, EventPriority.NORMAL, bus, generic);
  }

  public static <T extends GenericEvent<? extends F>, F> GenericEventManager<T, F> createGeneric(Class<T> clazz, EventPriority priority, Bus bus, Class<F> generic) {
    return new GenericEventManager<>(clazz, bus, priority, generic);
  }

}