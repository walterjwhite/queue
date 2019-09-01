package com.walterjwhite.queue.event.annotation;

import com.walterjwhite.queue.api.annotation.JobExecutionConfiguration;
import com.walterjwhite.queue.event.enumeration.EventActionType;
import java.lang.annotation.*;

/** Class to register this event listener to. */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SubscribeTo {
  Class eventClass();

  EventActionType[] eventAction() default {};

  JobExecutionConfiguration jobExecutionConfiguration() default @JobExecutionConfiguration;
}
