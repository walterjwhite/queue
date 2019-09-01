package com.walterjwhite.queue.jpa.guice;

import com.google.inject.AbstractModule;
import com.walterjwhite.queue.api.service.JobWorkerService;
import com.walterjwhite.queue.api.service.QueueService;
import com.walterjwhite.queue.event.service.QueueEventBuilder;
import com.walterjwhite.queue.impl.worker.DefaultJobWorkerService;
import com.walterjwhite.queue.jpa.DatastoreEventQueuer;
import com.walterjwhite.queue.jpa.DatastoreQueueEventBuilder;
import com.walterjwhite.queue.jpa.DatastoreQueueService;

public class QueueModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(JobWorkerService.class).to(DefaultJobWorkerService.class);

    bind(DatastoreEventQueuer.class);
    bind(QueueEventBuilder.class).to(DatastoreQueueEventBuilder.class);
    bind(QueueService.class).to(DatastoreQueueService.class);
    //    bind(Repository.class).annotatedWith(Secondary.class).to(SecondaryJpaRepository.class);
    // bind(EntityManager.class).annotatedWith(Secondary.class).to(SecondaryJpaRepository.class);
  }
}
