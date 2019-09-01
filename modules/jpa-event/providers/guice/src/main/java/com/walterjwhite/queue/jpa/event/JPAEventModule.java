package com.walterjwhite.queue.jpa.event;

import com.google.inject.AbstractModule;

public class JPAEventModule extends AbstractModule {

  @Override
  protected void configure() {
    // install(new Module)
    // bind(JPAEventQueueService.class).to(DefaultEventService.class);
  }
}
