package com.walterjwhite.queue.api.service;

import com.walterjwhite.job.api.model.Job;
import com.walterjwhite.job.api.model.JobExecution;
import com.walterjwhite.job.api.model.scheduling.AbstractSchedule;

/**
 * TODO: do NOT queue the same job configuration if another one is still pending. Also, support
 * scheduling multiple instances of the same job?
 */
public interface QueueService {
  /**
   * Queue the job and all schedules.
   *
   * @param job the job to queue.
   * @return the queued job.
   */
  Job queue(Job job);

  /**
   * Used for rescheduling jobs, used primarily on JVM restart.
   *
   * @param schedule the schedule to queue
   * @return the persistent job execution
   */
  JobExecution queue(AbstractSchedule schedule);

  /**
   * Cancel the job and all executions.
   *
   * @param job the job to cancel
   * @return the cancelled job with all executions cancelled.
   */
  Job cancel(Job job);

  /**
   * Cancel just the job execution.
   *
   * @param jobExecution the cancelled job execution.
   * @return
   */
  JobExecution cancel(JobExecution jobExecution);

  JobExecution scheduleNextRun(AbstractSchedule schedule);

  JobExecution rescheduleFailedExecution(AbstractSchedule schedule);

  /** Delegates to ExecutorService.shutdownNow() */
  void shutdownNow();

  /** Delegates to ExecutorService.shutdown() */
  void shutdown();
}
