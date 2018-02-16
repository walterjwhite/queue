package com.walterjwhite.job.impl;

import com.walterjwhite.queue.api.job.AbstractCallableJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogJobExecutor extends AbstractCallableJob<Message, Void> {
  private static final Logger LOGGER = LoggerFactory.getLogger(LogJobExecutor.class);

  @Override
  public Void call() throws Exception {
    LOGGER.info("message:" + entity.getMessage());
    return null;
  }

  @Override
  protected boolean isRetryable(Throwable thrown) {
    return false;
  }
}
