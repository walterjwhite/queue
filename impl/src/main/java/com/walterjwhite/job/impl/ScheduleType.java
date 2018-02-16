package com.walterjwhite.job.impl;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.walterjwhite.job.api.enumeration.Recurrence;
import com.walterjwhite.job.api.model.JobExecution;
import com.walterjwhite.job.api.model.scheduling.*;
import com.walterjwhite.job.impl.helper.JobExecutionEnvironmentManager;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public enum ScheduleType {
  AtDateTime(AtDateTimeSchedule.class) {
    @Override
    protected ScheduleInstance getDelay(AbstractSchedule schedule, final boolean isFuture) {
      if (isFuture)
        throw new UnsupportedOperationException("AtDateTime does not support a future execution.");

      AtDateTimeSchedule atDateTimeSchedule = (AtDateTimeSchedule) schedule;

      return new ScheduleInstance(
          LocalDateTime.now().until(atDateTimeSchedule.getOnDateTime(), ChronoUnit.MILLIS),
          -1,
          TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isRecurring(AbstractSchedule schedule) {
      return false;
    }
  },
  Cron(CronSchedule.class) {
    @Override
    protected ScheduleInstance getDelay(AbstractSchedule schedule, final boolean isFuture) {
      throw (new UnsupportedOperationException("Not implemented."));
    }

    @Override
    public boolean isRecurring(AbstractSchedule schedule) {
      throw (new UnsupportedOperationException("Not implemented."));
    }
  },
  FixedDelay(FixedDelaySchedule.class) {
    @Override
    protected ScheduleInstance getDelay(AbstractSchedule schedule, final boolean isFuture) {
      if (isFuture)
        throw new UnsupportedOperationException("FixedDelay does not support a future execution.");

      return new ScheduleInstance(
          ((FixedDelaySchedule) schedule).getInitialDelay(),
          ((FixedDelaySchedule) schedule).getFixedDelay(),
          ((FixedDelaySchedule) schedule).getTimeUnit());
    }

    @Override
    public boolean isRecurring(AbstractSchedule schedule) {
      return true;
    }
  },
  Simple(SimpleSchedule.class) {
    @Override
    protected ScheduleInstance getDelay(AbstractSchedule schedule, final boolean isFuture) {
      SimpleSchedule simpleSchedule = (SimpleSchedule) schedule;
      LocalDateTime scheduledRun = LocalDateTime.now();
      if (simpleSchedule.getHourOfDay() >= 0) scheduledRun.withHour(simpleSchedule.getHourOfDay());
      if (simpleSchedule.getMinuteOfHour() >= 0)
        scheduledRun.withMinute(simpleSchedule.getMinuteOfHour());
      if (simpleSchedule.getDayOfMonth() > 0)
        scheduledRun.withDayOfMonth(simpleSchedule.getDayOfMonth());
      if (simpleSchedule.getMonthOfYear() > 0)
        scheduledRun.withMonth(simpleSchedule.getMonthOfYear());

      if (isFuture) {
        // compute time to future run
        if (Recurrence.Yearly.equals(simpleSchedule.getRecurrence())) {
          scheduledRun = scheduledRun.plusYears(simpleSchedule.getRecurrenceAmount());
        } else if (Recurrence.Monthly.equals(simpleSchedule.getRecurrence())) {
          scheduledRun = scheduledRun.plusMonths(simpleSchedule.getRecurrenceAmount());
        } else if (Recurrence.Weekly.equals(simpleSchedule.getRecurrence())) {
          scheduledRun = scheduledRun.plusWeeks(simpleSchedule.getRecurrenceAmount());
        } else if (Recurrence.Daily.equals(simpleSchedule.getRecurrence())) {
          scheduledRun = scheduledRun.plusDays(simpleSchedule.getRecurrenceAmount());
        } else if (Recurrence.Hourly.equals(simpleSchedule.getRecurrence())) {
          scheduledRun = scheduledRun.plusHours(simpleSchedule.getRecurrenceAmount());
        } else if (Recurrence.Minutely.equals(simpleSchedule.getRecurrence())) {
          scheduledRun = scheduledRun.plusMinutes(simpleSchedule.getRecurrenceAmount());
        } else if (Recurrence.Secondly.equals(simpleSchedule.getRecurrence())) {
          scheduledRun = scheduledRun.plusSeconds(simpleSchedule.getRecurrenceAmount());
        } else {
          throw (new UnsupportedOperationException(
              "Not implemented:" + simpleSchedule.getRecurrence()));
        }

        return new ScheduleInstance(
            LocalDateTime.now().until(scheduledRun, ChronoUnit.MILLIS), -1, TimeUnit.MILLISECONDS);
      }

      final long timeUntilRun = LocalDateTime.now().until(scheduledRun, ChronoUnit.MILLIS);
      if (timeUntilRun < 0) {
        if (Recurrence.NONE.equals(simpleSchedule.getRecurrence())
            || simpleSchedule.isRunNowIfMissedFirstExecution()) {
          return new ScheduleInstance(0, -1, TimeUnit.MILLISECONDS);
        }
      }

      return new ScheduleInstance(timeUntilRun, -1, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isRecurring(AbstractSchedule schedule) {
      return !Recurrence.NONE.equals(((SimpleSchedule) schedule).getRecurrence());
    }
  };

  private final Class<? extends AbstractSchedule> scheduleClass;

  ScheduleType(Class<? extends AbstractSchedule> scheduleClass) {
    this.scheduleClass = scheduleClass;
  }

  public ListenableFuture schedule(
      ListeningScheduledExecutorService listeningScheduledExecutorService,
      JobExecutionEnvironmentManager jobExecutionEnvironmentManager,
      JobExecution jobExecution,
      AbstractSchedule schedule,
      final boolean isFuture) {
    final ScheduleInstance scheduleInstance = getDelay(schedule, isFuture);
    if (scheduleInstance.fixedDelay > 0)
      return listeningScheduledExecutorService.scheduleWithFixedDelay(
          jobExecutionEnvironmentManager.getRunnable(jobExecution.getId()),
          scheduleInstance.getInitialDelay(),
          scheduleInstance.getFixedDelay(),
          scheduleInstance.getUnits());

    return listeningScheduledExecutorService.schedule(
        jobExecutionEnvironmentManager.getRunnable(jobExecution.getId()),
        scheduleInstance.getInitialDelay(),
        scheduleInstance.getUnits());
  }

  protected abstract ScheduleInstance getDelay(AbstractSchedule schedule, final boolean isFuture);

  public abstract boolean isRecurring(AbstractSchedule schedule);

  public static ScheduleType get(final Class<? extends AbstractSchedule> scheduleClass) {
    for (final ScheduleType scheduleType : values()) {
      if (scheduleClass.equals(scheduleType.scheduleClass)) return scheduleType;
    }

    throw (new IllegalStateException(scheduleClass + " is not currently supported."));
  }
}
