package com.walterjwhite.queue.api.annotation.scheduling;

import java.lang.annotation.*;
import java.time.temporal.ChronoUnit;

/** Class to register this event listener to. */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AtDateAndTime {
  int hour() default -1;

  int minutes() default -1;

  int dayOfMonth() default -1;

  int dayOfWeek() default -1;

  int year() default -1;

  int month() default -1;

  ChronoUnit recurrenceUnits() default ChronoUnit.SECONDS;

  long recurrenceValue() default -1;

  boolean runNowIfMissedFirstExecution() default true;
}
