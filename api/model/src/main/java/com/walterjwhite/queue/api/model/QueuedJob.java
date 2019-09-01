package com.walterjwhite.queue.api.model;

import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * A QueuedJob represents an instance of a scheduled queuedJob execution 1. what executor should be
 * used 2. what parameter (entityReference) should be used: TODO: support multiple resources 3. when
 * to run (immediately, @ date/time, or cron expression)
 */
@NoArgsConstructor
@Data
@ToString(doNotUseGetters = true)
// @PersistenceCapable
@Entity
public class QueuedJob extends AbstractQueued {
  @Embedded protected ScheduleInstance scheduleInstance;

  /** Used for jobs that run once "NOW" */
  public QueuedJob(Class jobExecutorClass) {
    this(jobExecutorClass, null);
  }

  public QueuedJob(Class jobExecutorClass, ScheduleInstance scheduleInstance) {
    this(jobExecutorClass, scheduleInstance, false);
  }

  public QueuedJob(
      Class jobExecutorClass, ScheduleInstance scheduleInstance, final boolean system) {
    this();
    this.jobExecutorClass = jobExecutorClass;
    this.scheduleInstance = scheduleInstance;
    this.queueDateTime = LocalDateTime.now();
    this.system = system;
  }

  //  public ExecutionState getJobState() {
  //    final ExecutionState jobState = super.getJobState();
  //
  //    if (scheduleInstance.isRecurring()) return RecurringJobStateMapping.translate(jobState);
  //
  //    return jobState;
  //  }
}
