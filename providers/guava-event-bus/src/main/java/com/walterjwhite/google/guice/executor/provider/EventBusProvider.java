package com.walterjwhite.google.guice.executor.provider;

import com.google.common.eventbus.EventBus;
import com.walterjwhite.google.guice.annotation.EventBusOnly;
import com.walterjwhite.job.impl.property.property.EventBusName;
import com.walterjwhite.property.impl.annotation.Property;
import java.util.concurrent.ExecutorService;
import javax.inject.Inject;
import javax.inject.Provider;

public class EventBusProvider implements Provider<EventBus> {
  protected final EventBus eventBus;
  // exception handler

  @Inject
  public EventBusProvider(
      @EventBusOnly ExecutorService executorService,
      @Property(EventBusName.class) String eventBusName) {
    super();
    eventBus = new EventBus(eventBusName /*, executorService*/);
  }

  @Override
  public EventBus get() {
    return eventBus;
  }
}
