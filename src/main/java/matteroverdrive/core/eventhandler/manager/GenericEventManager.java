package matteroverdrive.core.eventhandler.manager;


import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.GenericEvent;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class GenericEventManager<T extends GenericEvent<? extends F>, F> implements ISubscribe {
  private Predicate<T> filter;
  private Consumer<T> process;
  private Class<T> event;
  private boolean cancel;
  private Bus bus;
  private EventPriority priority;
  private Class<F> generic;

  public GenericEventManager(Class<T> clazz, Bus bus, EventPriority priority, Class<F> generic) {
    this.event = clazz;
    this.filter = t -> true;
    this.process = t -> {
    };
    this.bus = bus;
    this.priority = priority;
    this.generic = generic;
  }

  public void subscribe() {
    bus.bus().addGenericListener(this.generic, priority, false, this.event, event -> {
      if (event.getClass().isAssignableFrom(this.event)) {
        if (this.filter.test(event)) {
          if (this.cancel) {
            if (event.isCancelable()) {
              event.setCanceled(true);
            }
          }
          this.process.accept(event);
        }
      }
    });
  }

  public GenericEventManager<T, F> filter(Predicate<T> predicateFilter) {
    this.filter = this.filter.and(predicateFilter);
    return this;
  }

  public GenericEventManager<T, F> process(Consumer<T> process) {
    this.process = this.process.andThen(process);
    return this;
  }

  public GenericEventManager<T, F> cancel() {
    this.cancel = true;
    return this;
  }
}
