package com.walterjwhite.queue.api.annotation;

import com.walterjwhite.queue.api.annotation.scheduling.AtDateAndTime;
import com.walterjwhite.queue.api.annotation.scheduling.Cron;
import com.walterjwhite.queue.api.annotation.scheduling.FixedDelay;
import java.lang.annotation.*;

/** Class to register this event listener to. */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Job {
  JobExecutionConfiguration jobExecutionConfiguration();

  // schedule (at date time, cron, fixed delay, now, queue name, atDateAndTime)
  FixedDelay[] fixedDelay() default {};

  AtDateAndTime[] atDateAndTime() default {};

  Cron[] cron() default {};

  // source for producing input / source data / contextual data?
}
