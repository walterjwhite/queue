package com.walterjwhite.job.impl;

import com.walterjwhite.queue.api.annotation.SubscribeTo;
import com.walterjwhite.queue.api.job.AbstractRunnable;
import com.walterjwhite.timeout.annotation.TimeConstrained;

@SubscribeTo(eventClass = Message.class)
public class LogJobRunnableExecutor extends AbstractRunnable {

  @TimeConstrained
  // (timeoutUnits = LogTimeoutUnits.class, timeoutValue = LogTimeoutValue.class)
  @Override
  protected void doCall() throws Exception {
    // LOGGER.info("message:" + entity.getMessage());

  }

  @Override
  public boolean isRetryable(Throwable throwable) {
    return false;
  }
}
