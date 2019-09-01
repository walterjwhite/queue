package com.walterjwhite.queue.impl.worker.scheduler;

import com.walterjwhite.queue.api.job.AbstractRunnable;
import com.walterjwhite.queue.api.model.AbstractQueued;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

public interface Scheduler<QueuedType extends AbstractQueued> {
  Future schedule(
      ScheduledExecutorService scheduledExecutorService,
      AbstractRunnable runnable,
      QueuedType queuedType);
}
