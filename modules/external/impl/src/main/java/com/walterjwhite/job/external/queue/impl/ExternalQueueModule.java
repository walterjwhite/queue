package com.walterjwhite.job.external.queue.impl;

import com.google.inject.AbstractModule;
import com.walterjwhite.job.external.queue.api.ExternalQueueBridgeService;

public class ExternalQueueModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(ExternalQueueBridgeService.class).to(DefaultExternalQueueBridgeService.class);
  }
}
