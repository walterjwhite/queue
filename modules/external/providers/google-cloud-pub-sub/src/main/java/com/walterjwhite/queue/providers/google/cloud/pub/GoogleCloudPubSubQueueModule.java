package com.walterjwhite.queue.providers.google.cloud.pub;

import com.google.inject.AbstractModule;
import com.walterjwhite.google.GoogleModule;
import com.walterjwhite.job.external.queue.api.ExternalQueueService;

public class GoogleCloudPubSubQueueModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(ExternalQueueService.class).to(GoogleCloudPubSubMessageWriter.class);

    install(new GoogleModule());
  }
}
