package com.walterjwhite.queue.event.service;

import com.walterjwhite.queue.api.job.AbstractRunnable;
import com.walterjwhite.queue.event.model.QueuedEvent;
import com.walterjwhite.queue.impl.worker.scheduler.Scheduler;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

public class QueuedEventScheduler implements Scheduler<QueuedEvent> {
  @Override
  public Future schedule(
      ScheduledExecutorService scheduledExecutorService,
      AbstractRunnable runnable,
      QueuedEvent queuedJob) {
    return scheduledExecutorService.submit(runnable);
  }
}
