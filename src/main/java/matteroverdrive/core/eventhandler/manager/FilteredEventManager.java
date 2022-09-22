package matteroverdrive.core.eventhandler.manager;


import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class FilteredEventManager<T extends Event> implements ISubscribe {
  private Predicate<T> filter;
  private Consumer<T> process;
  private Class<T> event;
  private boolean cancel;
  private Bus bus;
  private EventPriority priority;

  public FilteredEventManager(Class<T> clazz, Bus bus, EventPriority priority) {
    this.event = clazz;
    this.filter = t -> true;
    this.process = t -> {
    };
    this.bus = bus;
    this.priority = priority;
  }

  public void subscribe() {
    bus.bus().addListener(priority, false, event, event -> {
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

  public FilteredEventManager<T> filter(Predicate<T> predicateFilter) {
    this.filter = this.filter.and(predicateFilter);
    return this;
  }

  public FilteredEventManager<T> process(Consumer<T> process) {
    this.process = this.process.andThen(process);
    return this;
  }

  public FilteredEventManager<T> cancel() {
    this.cancel = true;
    return this;
  }
}
