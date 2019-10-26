package com.walterjwhite.queue.impl.worker.enumeration;

import com.walterjwhite.queue.api.annotation.scheduling.AtDateAndTime;
import com.walterjwhite.queue.api.annotation.scheduling.Cron;
import com.walterjwhite.queue.api.annotation.scheduling.FixedDelay;
import com.walterjwhite.queue.api.job.AbstractRunnable;
import com.walterjwhite.queue.api.model.QueuedJob;
import com.walterjwhite.queue.impl.worker.builder.AtDateAndTimeScheduleInstanceBuilder;
import com.walterjwhite.queue.impl.worker.builder.CronScheduleInstanceBuilder;
import com.walterjwhite.queue.impl.worker.builder.FixedDelayScheduleInstanceBuilder;
import com.walterjwhite.queue.impl.worker.builder.ScheduleInstanceBuilder;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import lombok.Getter;

@Getter
public enum ScheduleType {
  Now(null, null) {
    public void queue(
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor,
        QueuedJob queuedJob,
        AbstractRunnable runnable) {
      scheduledThreadPoolExecutor.submit(runnable);
    }
  },
  AtDateTimeSchedule(AtDateAndTime.class, new AtDateAndTimeScheduleInstanceBuilder()) {
    public void queue(
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor,
        QueuedJob queuedJob,
        AbstractRunnable runnable) {
      scheduledThreadPoolExecutor.schedule(
          runnable,
          queuedJob.getScheduleInstance().getInitialDelay(),
          queuedJob.getScheduleInstance().getUnits());
    }
  },
  CronSchedule(Cron.class, new CronScheduleInstanceBuilder()) {
    public void queue(
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor,
        QueuedJob queuedJob,
        AbstractRunnable runnable) {
      throw new UnsupportedOperationException("Not implemented");
    }
  },
  FixedDelaySchedule(FixedDelay.class, new FixedDelayScheduleInstanceBuilder()) {
    public void queue(
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor,
        QueuedJob queuedJob,
        AbstractRunnable runnable) {
      scheduledThreadPoolExecutor.scheduleWithFixedDelay(
          runnable,
          queuedJob.getScheduleInstance().getInitialDelay(),
          queuedJob.getScheduleInstance().getFixedDelay(),
          queuedJob.getScheduleInstance().getUnits());
    }
  };

  private final Class annotationClass;
  private final ScheduleInstanceBuilder scheduleInstanceBuilder;

  ScheduleType(Class annotationClass, ScheduleInstanceBuilder scheduleInstanceBuilder) {
    this.annotationClass = annotationClass;
    this.scheduleInstanceBuilder = scheduleInstanceBuilder;
  }

  public static ScheduleType get(final Class annotationClass) {
    for (final ScheduleType scheduleType : values()) {
      if (scheduleType.annotationClass.equals(annotationClass)) return scheduleType;
    }

    throw new IllegalStateException(annotationClass + " is not currently supported.");
  }

  public abstract void queue(
      ScheduledThreadPoolExecutor scheduledThreadPoolExecutor,
      QueuedJob queuedJob,
      AbstractRunnable runnable);
}
