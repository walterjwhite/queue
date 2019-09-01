package com.walterjwhite.queue.api.service;

import com.walterjwhite.queue.api.job.RunningFuture;
import com.walterjwhite.queue.api.model.AbstractQueued;
import com.walterjwhite.queue.api.model.JobExecution;

public interface JobWorkerService {
  /**
   * To be called by the job scheduler service in the worker JVM
   *
   * @param queued
   * @return
   */
  AbstractQueued queue(AbstractQueued queued);

  /**
   * To be called by the job scheduler service in the worker JVM
   *
   * @param queued
   * @return
   */
  void cancel(AbstractQueued queued);

  /**
   * Cancel a specific execution.
   *
   * @param jobExecution
   * @return
   */
  void cancel(JobExecution jobExecution);

  void remove(RunningFuture runningFuture);
}
