package com.walterjwhite.job.impl;

import com.google.inject.AbstractModule;

public class QueueTestModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(LogJobExecutor.class);
  }
}
