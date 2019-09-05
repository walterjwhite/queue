package com.walterjwhite.queue.impl.worker;

import com.walterjwhite.interruptable.Interruptable;
import com.walterjwhite.property.api.enumeration.NoOperation;
import com.walterjwhite.property.impl.annotation.Property;
import com.walterjwhite.queue.api.job.AbstractRunnable;
import com.walterjwhite.queue.api.job.RunningFuture;
import com.walterjwhite.queue.api.model.AbstractQueued;
import com.walterjwhite.queue.api.model.JobExecution;
import com.walterjwhite.queue.api.model.Worker;
import com.walterjwhite.queue.api.service.JobWorkerService;
import com.walterjwhite.queue.impl.worker.builder.JobBuilder;
import com.walterjwhite.queue.impl.worker.property.ExecutorServiceShutdownTimeoutUnits;
import com.walterjwhite.queue.impl.worker.property.ExecutorServiceShutdownTimeoutValue;
import com.walterjwhite.queue.impl.worker.scheduler.Scheduler;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;
import javax.inject.Inject;
import lombok.Getter;

@Getter
// @RequiredArgsConstructor
public class DefaultJobWorkerService implements JobWorkerService, Interruptable {
  protected final boolean noOperation;

  protected final long shutdownTimeout;
  protected final TimeUnit shutdownTimeoutUnits;
  protected final JobBuilder jobBuilder;
  protected final Worker worker;
  protected final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor =
      new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2);
  protected boolean shutdown;

  protected final Set<RunningFuture> runningFutures = new ConcurrentSkipListSet<>();

  @Inject
  public DefaultJobWorkerService(
      @Property(NoOperation.class) boolean noOperation,
      @Property(ExecutorServiceShutdownTimeoutValue.class) long shutdownTimeout,
      @Property(ExecutorServiceShutdownTimeoutUnits.class) TimeUnit shutdownTimeoutUnits,
      JobBuilder jobBuilder,
      Worker worker) {
    this.noOperation = noOperation;
    this.shutdownTimeout = shutdownTimeout;
    this.shutdownTimeoutUnits = shutdownTimeoutUnits;
    this.jobBuilder = jobBuilder;
    this.worker = worker;
  }

  public void interrupt() {
    scheduledThreadPoolExecutor.shutdown();

    try {
      if (!scheduledThreadPoolExecutor.awaitTermination(shutdownTimeout, shutdownTimeoutUnits)) {
        updateCancelledJobs();
      }
    } catch (InterruptedException e) {
      throw new RuntimeException("Error during shutdown", e);
    }
  }

  protected void updateCancelledJobs() {
    scheduledThreadPoolExecutor.shutdownNow().stream()
        .forEach(runnable -> handleRemovalOfRunnable(runnable));
  }

  protected void handleRemovalOfRunnable(Runnable runnable) {
    // TODO: check if the running job would know it was interrupted and let the job mark it as such
    // there
    // in order for this to work, we would need to store a link between a future/runnable and an
    // AbstractQueued
    // maybe that is as simple as storing the hashCode of the runnable and linking that with the id
    // of the AbstractQueued
    // AND, adding, removing that map as jobs are scheduled or completed or cancelled
    // alternatively, the job would appear as aborted since it wasn't updated, so we may not need to
    // do anything
  }

  protected void checkIfShutdown() {
    if (isShutdown()) throw new IllegalStateException("Executor service is shutting down");
  }

  @Override
  // @Transactional
  public AbstractQueued queue(AbstractQueued queued) {
    checkIfShutdown();

    if (isNoOperation()) return queued;

    // updateJobStatus(queued, ExecutionState.Assigned);

    doQueue(queued);
    // updateJobStatus(queued, ExecutionState.Scheduled);

    return queued;
  }

  protected void doQueue(AbstractQueued queued) {
    final AbstractRunnable runnable = jobBuilder.prepareCallableJob(queued);

    // get scheduler based on the type ...
    Scheduler scheduler = null;
    final Future f = scheduler.schedule(scheduledThreadPoolExecutor, runnable, queued);

    final RunningFuture runningFuture =
        new RunningFuture(
            f, queued.getId(), queued.getCurrentJobExecution().getId(), runnable.hashCode());
    runningFutures.add(runningFuture);
    runnable.setRunningFuture(runningFuture);
  }

  /**
   * To be invoked by the queue service directly for cancelling jobs. The queue service handles the
   * persistence operations.
   *
   * @param queued the job to cancel
   */
  @Override
  public void cancel(AbstractQueued queued) {
    final Optional<RunningFuture> runningFutureOptional =
        runningFutures.stream()
            .filter(runningFuture -> runningFuture.getQueuedId() == queued.getId())
            .findFirst();
    if (!runningFutureOptional.isPresent())
      throw new IllegalStateException(
          "Requested queued is not present and must have already completed or failed:" + queued);

    runningFutureOptional.get().getFuture().cancel(true);
    remove(runningFutureOptional.get());
  }

  /**
   * To be invoked by the queue service directly for cancelling a specific job execution. The queue
   * service handles the persistence operations.
   *
   * @param jobExecution the job execution to cancel
   */
  @Override
  public void cancel(JobExecution jobExecution) {
    final Optional<RunningFuture> runningFutureOptional =
        runningFutures.stream()
            .filter(runningFuture -> runningFuture.getExecutionId() == jobExecution.getId())
            .findFirst();
    if (!runningFutureOptional.isPresent())
      throw new IllegalStateException(
          "Requested job execution is not present and must have already completed or failed:"
              + jobExecution);

    runningFutureOptional.get().getFuture().cancel(true);
    remove(runningFutureOptional.get());
  }

  /**
   * To be invoked by the AbstractRunnable upon completion or failure.
   *
   * @param runningFuture the completed job to remove from the list of running jobs
   */
  public void remove(RunningFuture runningFuture) {
    runningFutures.remove(runningFuture);
  }

  // commented this out because it doesn't seem to provide tremendous value.
  /**
   * Cannot inject the queue service at construction time since that would result in circular
   * dependencies. Pull these persistence operations out?
   *
   * @param queued
   * @param jobState
   */
  //  protected void updateJobStatus(AbstractQueued queued, ExecutionState jobState) {
  //    queued.getCurrentJobExecution().setExecutionState(jobState);
  //    queued.getCurrentJobExecution().setWorker(worker);
  //    ApplicationHelper.getApplicationInstance()
  //        .getInjector()
  //        .getInstance(QueueService.class)
  //        .update(queued);
  //  }
}
