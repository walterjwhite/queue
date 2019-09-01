package com.walterjwhite.queue.impl.worker.scheduler;

import com.walterjwhite.queue.api.job.AbstractRunnable;
import com.walterjwhite.queue.api.model.QueuedJob;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

public class QueuedJobScheduler implements Scheduler<QueuedJob> {
  @Override
  public Future schedule(
      ScheduledExecutorService scheduledExecutorService,
      AbstractRunnable runnable,
      QueuedJob queuedJob) {

    // return queuedJob.getScheduleInstance().getScheduleType().queue();
    return null;
  }
}
