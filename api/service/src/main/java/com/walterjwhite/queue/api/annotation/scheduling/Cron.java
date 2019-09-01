package com.walterjwhite.queue.api.annotation.scheduling;

import java.lang.annotation.*;
import java.time.temporal.ChronoUnit;

/** Class to register this event listener to. */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cron {
  int hourOfDay() default -1;

  int minuteOfHour() default -1;

  int dayOfMonth() default -1;

  int monthOfYear() default -1;

  ChronoUnit recurrence() default ChronoUnit.SECONDS;

  long recurrenceAmount() default 1;

  boolean runNowIfMissedFirstExecution() default true;
}
