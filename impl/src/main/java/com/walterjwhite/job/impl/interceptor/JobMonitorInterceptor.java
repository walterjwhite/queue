package com.walterjwhite.job.impl.interceptor;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.walterjwhite.google.guice.executor.annotation.ExecutorManagementServiceOnly;
import com.walterjwhite.job.api.model.Job;
import com.walterjwhite.job.api.model.JobExecution;
import com.walterjwhite.job.impl.helper.JobExecutionInstance;
import com.walterjwhite.job.impl.repository.JobExecutionRepository;
import com.walterjwhite.queue.api.service.QueueService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import javax.inject.Inject;
import javax.inject.Provider;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class JobMonitorInterceptor implements MethodInterceptor {
  @ExecutorManagementServiceOnly @Inject
  protected ListeningScheduledExecutorService managementExecutorService;

  protected final Provider<QueueService> queueServiceProvider;
  protected final Provider<JobExecutionRepository> jobExecutionRepositoryProvider;

  protected final Set<Future> futures = new HashSet<>();
  protected final Map<Future, Future> managementFutures = new HashMap<>();

  public JobMonitorInterceptor(
      Provider<QueueService> queueServiceProvider,
      Provider<JobExecutionRepository> jobExecutionRepositoryProvider) {
    this.queueServiceProvider = queueServiceProvider;
    this.jobExecutionRepositoryProvider = jobExecutionRepositoryProvider;
  }

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    final JobExecutionInstance jobExecutionInstance = (JobExecutionInstance) invocation.proceed();
    trackJob(jobExecutionInstance);

    return jobExecutionInstance;
  }

  protected void trackJob(JobExecutionInstance jobExecutionInstance) {
    if (jobExecutionInstance == null) return;

    if (jobExecutionInstance.getFuture() != null) {
      futures.add(jobExecutionInstance.getFuture());
      scheduleTimeout(
          jobExecutionInstance.getJobExecution().getSchedule().getJob(),
          jobExecutionInstance.getFuture());
      registerListener(jobExecutionInstance.getJobExecution(), jobExecutionInstance.getFuture());
    }
  }

  protected void scheduleTimeout(Job job, ListenableFuture future) {
    // if this job has an execution time limit, set a task to kill it
    if (job.getExecutionTimeLimit() > 0) {
      managementFutures.put(
          future,
          Futures.withTimeout(
              future,
              job.getExecutionTimeLimit(),
              job.getExecutionTimeLimitUnits(),
              managementExecutorService));
    }
  }

  protected void registerListener(JobExecution jobExecution, ListenableFuture future) {
    Futures.addCallback(
        future,
        new FutureCallback<Void>() {
          public void onSuccess(Void explosion) {
            onCompletion(future, jobExecution, null);
          }

          public void onFailure(Throwable throwable) {
            onCompletion(future, jobExecution, throwable);
          }
        },
        managementExecutorService);
  }

  protected void onCompletion(
      ListenableFuture future, JobExecution jobExecution, Throwable throwable) {
    clearJobExecution(future);
    clearTimeout(future);

    jobExecution = jobExecutionRepositoryProvider.get().updateJobExecution(jobExecution, throwable);

    scheduleNextRun(jobExecution, throwable == null);
  }

  protected void clearJobExecution(Future future) {
    futures.remove(future);
  }

  protected void clearTimeout(Future future) {
    final Future managementFuture = managementFutures.get(future);
    if (managementFuture != null) {
      managementFuture.cancel(true);
      managementFutures.remove(future);
    }
  }

  protected void scheduleNextRun(JobExecution jobExecution, final boolean isCompletedSuccessfully) {
    if (isCompletedSuccessfully)
      queueServiceProvider.get().scheduleNextRun(jobExecution.getSchedule());
    else if (jobExecution.getSchedule().getRemainingRetries() > 0 && jobExecution.isRetryable())
      queueServiceProvider.get().rescheduleFailedExecution(jobExecution.getSchedule());
  }
}
