package com.walterjwhite.queue.impl.worker;

import com.walterjwhite.queue.api.annotation.Job;
import com.walterjwhite.queue.api.annotation.JobExecutionConfiguration;
import com.walterjwhite.queue.api.annotation.scheduling.FixedDelay;
import com.walterjwhite.queue.api.enumeration.ExecutionState;
import com.walterjwhite.queue.api.job.AbstractRunnable;
import com.walterjwhite.queue.api.model.AbstractQueued;
import com.walterjwhite.queue.api.service.JobWorkerService;
import com.walterjwhite.queue.api.service.QueueService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// TODO: 1. configure how frequently we poll the backend for jobs
@Getter
@Job(
    jobExecutionConfiguration = @JobExecutionConfiguration(system = true),
    fixedDelay = @FixedDelay(fixedDelay = 60))
@RequiredArgsConstructor
public class WorkerSchedulerRunnable extends AbstractRunnable {
  protected final QueueService queueService;
  protected final JobWorkerService jobWorkerService;

  protected void doCall() {
    markJobExecutionAsAborted();
    rescheduleJobs();
  }

  /** For any job that appears to have died, mark it as aborted. */
  protected void markJobExecutionAsAborted() {
    queueService
        .findAbortedJobExecutions()
        .forEach(
            queuedJob -> {
              markJobExecutionAsAborted(queuedJob);
            });
  }

  protected void markJobExecutionAsAborted(AbstractQueued queued) {
    queued
        .getJobExecutions()
        .get(queued.getJobExecutions().size() - 1)
        .setExecutionState(ExecutionState.Aborted);

    queueService.update(queued);
  }

  /** For any jobs we may pick up, attempt to assign them out to our local worker. */
  protected void rescheduleJobs() {
    queueService.findAssignable().forEach(queuedJob -> jobWorkerService.queue(queuedJob));
    queueService.findRecurringAssignable().forEach(queuedJob -> jobWorkerService.queue(queuedJob));
  }
}
