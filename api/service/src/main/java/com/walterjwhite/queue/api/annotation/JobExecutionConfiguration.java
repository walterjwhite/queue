package com.walterjwhite.queue.api.annotation;

import com.walterjwhite.property.api.property.ConfigurableProperty;
import com.walterjwhite.queue.api.enumeration.Durability;
import com.walterjwhite.queue.api.property.JobRetryAttempts;
import com.walterjwhite.queue.api.property.JobTimeoutUnits;
import com.walterjwhite.queue.api.property.JobTimeoutValue;
import java.lang.annotation.*;

/** Class to register this event listener to. */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JobExecutionConfiguration {
  /** @return The class to use for property injection. */
  @Deprecated
  Class<? extends ConfigurableProperty> timeoutUnits() default JobTimeoutUnits.class;

  /** @return The class to use for property injection. */
  @Deprecated
  Class<? extends ConfigurableProperty> timeoutValue() default JobTimeoutValue.class;

  /**
   * Whether or not this queuedJob should be durable.
   *
   * @return the configured queuedJob durability, none by default.
   */
  Durability durability() default Durability.None;

  /** @return The class to use for property injection. */
  Class<? extends JobRetryAttempts> retryAttempts() default JobRetryAttempts.class;

  /**
   * Exception classes to retry execution for.
   *
   * @return array of classes for which execution should be reattempted (assuming there are
   *     remaining attempts).
   */
  Class<? extends Throwable>[] retryFor() default RuntimeException.class;

  /**
   * Is this a system job (should not be recorded)
   *
   * <p>This would be useful when we need to have a periodic task such as polling a database for
   * activity ...
   *
   * @return
   */
  boolean system() default false;
}
