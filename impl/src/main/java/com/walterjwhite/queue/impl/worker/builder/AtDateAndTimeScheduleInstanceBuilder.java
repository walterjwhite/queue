package com.walterjwhite.queue.impl.worker.builder;

import com.walterjwhite.queue.api.annotation.Job;
import com.walterjwhite.queue.api.annotation.scheduling.AtDateAndTime;
import com.walterjwhite.queue.api.model.ScheduleInstance;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class AtDateAndTimeScheduleInstanceBuilder implements ScheduleInstanceBuilder {
  @Override
  public void getDelay(Set<ScheduleInstance> scheduleInstances, Job job) {
    Arrays.stream(job.atDateAndTime())
        .forEach(atDateAndTime -> scheduleInstances.add(build(atDateAndTime)));
  }

  protected ScheduleInstance build(AtDateAndTime atDateAndTime) {
    final LocalDateTime scheduledRun = LocalDateTime.now();

    configure(atDateAndTime, scheduledRun);

    long timeUntilRun = LocalDateTime.now().until(scheduledRun, ChronoUnit.SECONDS);
    if (timeUntilRun < 0) {
      timeUntilRun = 0;
    }

    long fixedDelay = -1;
    if (isRecurring(atDateAndTime)) {
      fixedDelay = getFixedDelay(atDateAndTime);
    }

    return new ScheduleInstance(timeUntilRun, fixedDelay, TimeUnit.SECONDS);
  }

  protected void configure(AtDateAndTime atDateAndTimeSchedule, final LocalDateTime scheduledRun) {
    if (atDateAndTimeSchedule.hour() >= 0) scheduledRun.withHour(atDateAndTimeSchedule.hour());
    if (atDateAndTimeSchedule.minutes() >= 0)
      scheduledRun.withMinute(atDateAndTimeSchedule.minutes());
    if (atDateAndTimeSchedule.dayOfMonth() > 0)
      scheduledRun.withDayOfMonth(atDateAndTimeSchedule.dayOfMonth());
    if (atDateAndTimeSchedule.month() > 0) scheduledRun.withMonth(atDateAndTimeSchedule.month());
  }

  protected boolean isRecurring(AtDateAndTime schedule) {
    return schedule.recurrenceValue() > 0;
  }

  protected long getFixedDelay(AtDateAndTime atDateAndTime) {
    return atDateAndTime.recurrenceValue()
        * (atDateAndTime.recurrenceUnits().getDuration().toMillis() / 1000);
  }
}
