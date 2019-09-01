package com.walterjwhite.queue.api.job;

import com.walterjwhite.heartbeat.Heartbeatable;
import com.walterjwhite.heartbeat.annotation.Heartbeat;
import com.walterjwhite.infrastructure.inject.core.helper.ApplicationHelper;
import com.walterjwhite.interruptable.Interruptable;
import com.walterjwhite.interruptable.annotation.InterruptableTask;
import com.walterjwhite.logging.annotation.LogStackTrace;
import com.walterjwhite.queue.api.enumeration.ExecutionState;
import com.walterjwhite.queue.api.enumeration.QueueState;
import com.walterjwhite.queue.api.model.AbstractQueued;
import com.walterjwhite.queue.api.model.JobExecution;
import com.walterjwhite.queue.api.model.QueuedJob;
import com.walterjwhite.queue.api.service.JobWorkerService;
import com.walterjwhite.queue.api.service.QueueService;
import java.time.Duration;
import java.time.LocalDateTime;
import javax.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@ToString(doNotUseGetters = true)
@RequiredArgsConstructor
public abstract class AbstractRunnable<QueuedType extends AbstractQueued>
    implements Runnable, Heartbeatable, Interruptable {
  protected Duration heartbeatInterval;
  protected Duration interruptGracePeriodTimeout;

  protected transient Thread executingThread;

  protected QueuedType queued;
  protected JobExecution currentJobExecution;
  protected JobWorkerService jobWorkerService;
  protected RunningFuture runningFuture;

  public void run() {
    executingThread = Thread.currentThread();

    startCurrentJobExecution();
    updateJobExecutionStatus(ExecutionState.Running);

    try {
      doCall();
      onSuccess();
    } catch (Exception e) {
      onError(e);
    }
  }

  protected void startCurrentJobExecution() {
    currentJobExecution = new JobExecution(queued);
    queued.setCurrentJobExecution(currentJobExecution);
    queued.getJobExecutions().add(currentJobExecution);
  }

  @Transactional
  protected void updateJobExecutionStatus(ExecutionState jobState) {
    currentJobExecution.setExecutionState(jobState);
    currentJobExecution.setUpdateDateTime(LocalDateTime.now());

    // TODO: this needs to select the right instance (@Primary/@Secondary)
    ApplicationHelper.getApplicationInstance()
        .getInjector()
        .getInstance(QueueService.class)
        .update(queued);
  }

  /**
   * NOTE: for timeouts, use the @TimeConstrained annotation and override the properties for the
   * value/units. that will handle interrupting the method just as any other method can be
   * interrupted.
   *
   * @return
   * @throws Exception
   */
  @InterruptableTask
  @Heartbeat
  protected abstract void doCall() throws Exception;

  //  if (Thread.currentThread().isInterrupted()) {
  //    throw new RuntimeException("Interrupted")
  //  }

  protected void onSuccess() {
    jobWorkerService.remove(runningFuture);

    updateJobExecutionStatus(ExecutionState.Completed);
    updateJobStatus();

    doOnSuccess();
  }

  @Transactional
  protected void updateJobStatus() {
    // job is completed as it is not recurring
    if (((QueuedJob) queued).getScheduleInstance().isRecurring())
      queued.setQueueState(QueueState.Completed);
  }

  protected void doOnSuccess() {}

  @LogStackTrace
  protected void onError(Exception e) {
    jobWorkerService.remove(runningFuture);

    currentJobExecution.setThrowable(e);
    updateJobExecutionStatus(ExecutionState.Exception);

    retry(e);
  }

  /** Invoked periodically through aspectj plugin - annotation does *NOT* apply here */
  @Transactional
  public void onHeartbeat() {
    if (ApplicationHelper.getApplicationInstance()
        .getInjector()
        .getInstance(QueueService.class)
        .wasCancelled(queued)) {
      // InterruptableInvocationType.Task.getInterruptableRegistry().interrupt(this);
      interrupt();
      return;
    }

    // update job state
    updateJobExecutionStatus(currentJobExecution.getExecutionState());
  }

  protected void retry(Exception e) {
    if (isRetryable(e)) {
      // jobWorkerService.submit(callable);
      return;
    }
  }

  protected boolean isRetryable(Throwable t) {
    return RuntimeException.class.isInstance(t);
  }

  @LogStackTrace
  protected void onInterruptionError(Exception e) {}

  // implemented by subclass if there is anything that can be closed such as file handles
  @Override
  public void interrupt() {
    try {
      executingThread.interrupt();
    } catch (Exception e) {
      onInterruptionError(e);
    }
  }
}
