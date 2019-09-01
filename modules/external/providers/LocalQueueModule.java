package com.walterjwhite.queue.providers.local.service;

import com.google.inject.AbstractModule;

public class LocalQueueModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(LocalQueueService.class);
  }
}
