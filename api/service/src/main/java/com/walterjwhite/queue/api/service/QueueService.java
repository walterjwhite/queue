package com.walterjwhite.queue.api.service;

import com.walterjwhite.queue.api.model.AbstractQueued;
import com.walterjwhite.queue.api.model.JobExecution;
import java.util.List;

/**
 * TODO: do NOT queue the same queuedJob configuration if another one is still pending. Also,
 * support scheduling multiple instances of the same queuedJob?
 */
public interface QueueService {
  /**
   * Cancel the queuedJob and all executions.
   *
   * @param queued the queuedJob to cancel
   * @return the cancelled queuedJob with all executions cancelled.
   */
  <QueuedType extends AbstractQueued> QueuedType cancel(QueuedType queued);

  /**
   * Cancel the specific job execution, but if the job has future executions, let those continue.
   * NOTE: this is an interactive call, why would this be automatically canceled?
   *
   * @param jobExecution
   * @return
   */
  JobExecution cancel(JobExecution jobExecution);

  /**
   * Queue the specified job with the specified schedule(s) TODO: do we need to specify the queue?
   * 1. YES, to isolate jobs 2. NO, if we need to isolate jobs / work, shouldn't the work be in
   * separate databases?
   *
   * @param queued
   * @return
   */
  <QueuedType extends AbstractQueued> QueuedType queue(QueuedType queued);

  /**
   * Used by the runnable to check if it was cancelled while it was running.
   *
   * @param queued
   * @return
   */
  boolean wasCancelled(AbstractQueued queued);

  /**
   * Find any queued jobs that can be assigned out to a worker.
   *
   * @return
   */
  <QueuedType extends AbstractQueued> List<QueuedType> findAssignable();

  /**
   * Find any queued jobs that are recurring and can be assigned out to a worker.
   *
   * @param <QueuedType>
   * @return
   */
  <QueuedType extends AbstractQueued> List<QueuedType> findRecurringAssignable();

  /**
   * Find any queued jobs that appear to still be running but the heartbeat last received is greater
   * than the heartbeat interval. Additionally, look at jobs that ended in a failure status and are
   * retryable
   *
   * @return
   */
  <QueuedType extends AbstractQueued> List<QueuedType> findAbortedJobExecutions();

  // TODO: could this be internal?  I believe this was exposed to allow the underlying
  // implementation to determine how to update the job so that a worker could update the job without
  // needing to know the implementation details
  <QueuedType extends AbstractQueued> QueuedType update(QueuedType queued);
}
