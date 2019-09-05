package com.walterjwhite.queue.api.service;

import com.walterjwhite.queue.api.job.RunningFuture;
import com.walterjwhite.queue.api.model.AbstractQueued;
import com.walterjwhite.queue.api.model.JobExecution;

public interface JobWorkerService {
  /**
   * To be called by the job scheduler service in the worker JVM
   *
   * @param queued the job to be executed
   * @return the job queued in the scheduler (same instance with updated references)
   */
  AbstractQueued queue(AbstractQueued queued);

  /**
   * To be called by the job scheduler service in the worker JVM
   *
   * @param queued the job to cancel
   */
  void cancel(AbstractQueued queued);

  /**
   * Cancel a specific execution.
   *
   * @param jobExecution the specific execution to cancel
   */
  void cancel(JobExecution jobExecution);

  void remove(RunningFuture runningFuture);
}
