package com.walterjwhite.queue.impl.worker.builder;

import com.walterjwhite.infrastructure.inject.core.helper.ApplicationHelper;
import com.walterjwhite.property.impl.annotation.Property;
import com.walterjwhite.queue.api.job.AbstractRunnable;
import com.walterjwhite.queue.api.model.AbstractQueued;
import com.walterjwhite.queue.impl.worker.property.JobExecutionHeartbeatTimeoutUnits;
import com.walterjwhite.queue.impl.worker.property.JobExecutionHeartbeatTimeoutValue;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import javax.inject.Inject;

public class JobBuilder {
  protected final long interruptGracePeriodValue;
  protected final ChronoUnit interruptGracePeriodUnits;
  protected final long heartbeatIntervalValue;
  protected final ChronoUnit heartbeatIntervalUnits;

  @Inject
  public JobBuilder(
      /*@Property(InterruptGracePeriodValue.class)*/ long
          interruptGracePeriodValue, /*@Property(InterruptGracePeriodUnits.class)*/
      ChronoUnit interruptGracePeriodUnits,
      @Property(JobExecutionHeartbeatTimeoutValue.class) long heartbeatIntervalValue,
      @Property(JobExecutionHeartbeatTimeoutUnits.class) ChronoUnit heartbeatIntervalUnits) {
    this.interruptGracePeriodValue = interruptGracePeriodValue;
    this.interruptGracePeriodUnits = interruptGracePeriodUnits;
    this.heartbeatIntervalValue = heartbeatIntervalValue;
    this.heartbeatIntervalUnits = heartbeatIntervalUnits;
  }

  /** This gets an instance of the SelfReschedulingJobCallable */
  public AbstractRunnable prepareCallableJob(AbstractQueued queued) {
    AbstractRunnable runnable = getRunnable(queued);
    setup(runnable, queued);

    return runnable;
  }

  protected AbstractRunnable getRunnable(AbstractQueued queued) {
    return (AbstractRunnable)
        ApplicationHelper.getApplicationInstance()
            .getInjector()
            .getInstance(queued.getJobExecutorClass());
  }

  protected void setup(AbstractRunnable runnable, AbstractQueued queued) {
    runnable.setQueued(queued);
    runnable.setHeartbeatInterval(Duration.of(heartbeatIntervalValue, heartbeatIntervalUnits));
    runnable.setInterruptGracePeriodTimeout(
        Duration.of(heartbeatIntervalValue, heartbeatIntervalUnits));
  }
}
