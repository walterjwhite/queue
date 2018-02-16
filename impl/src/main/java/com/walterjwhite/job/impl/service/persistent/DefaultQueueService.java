package com.walterjwhite.job.impl.service.persistent;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.inject.persist.Transactional;
import com.walterjwhite.datastore.criteria.Repository;
import com.walterjwhite.google.guice.executor.annotation.ExecutorServiceOnly;
import com.walterjwhite.google.guice.property.enumeration.NoOperation;
import com.walterjwhite.google.guice.property.property.Property;
import com.walterjwhite.job.api.enumeration.JobExecutionState;
import com.walterjwhite.job.api.model.*;
import com.walterjwhite.job.api.model.scheduling.AbstractSchedule;
import com.walterjwhite.job.api.model.scheduling.AtDateTimeSchedule;
import com.walterjwhite.job.impl.ScheduleType;
import com.walterjwhite.job.impl.annotation.MonitorJob;
import com.walterjwhite.job.impl.helper.JobExecutionEnvironmentManager;
import com.walterjwhite.job.impl.helper.JobExecutionInstance;
import com.walterjwhite.job.impl.repository.ScheduleRepository;
import com.walterjwhite.queue.api.service.QueueService;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.NoResultException;

/**
 * This implementation is responsible for queueing jobs (persisting them to the database). The job
 * execution service is responsible for retrieving queued jobs and executing them.
 */
public class DefaultQueueService extends AbstractIdleService implements QueueService {
  protected final ListeningScheduledExecutorService listeningScheduledExecutorService;

  protected final Provider<ScheduleRepository> scheduleRepositoryProvider;
  protected final JobExecutionEnvironmentManager jobExecutionEnvironmentManager;
  protected final Provider<Repository> repositoryProvider;
  protected final Map<String, ListenableFuture> futures = new HashMap<>();
  protected final boolean noOperation;

  @Inject
  public DefaultQueueService(
      @ExecutorServiceOnly ListeningScheduledExecutorService listeningScheduledExecutorService,
      Provider<ScheduleRepository> scheduleRepositoryProvider,
      JobExecutionEnvironmentManager jobExecutionEnvironmentManager,
      @Property(NoOperation.class) boolean noOperation,
      Provider<Repository> repositoryProvider) {
    super();
    this.listeningScheduledExecutorService = listeningScheduledExecutorService;
    this.scheduleRepositoryProvider = scheduleRepositoryProvider;

    this.jobExecutionEnvironmentManager = jobExecutionEnvironmentManager;
    this.repositoryProvider = repositoryProvider;
    this.noOperation = noOperation;
  }

  public Job queue(Job job) {
    if (isAcceptingNewJobs()) doSchedule(job);

    return job;
  }

  protected boolean isAcceptingNewJobs() {
    return (!noOperation);
  }

  protected void doSchedule(Job job) {
    final Set<JobExecutionInstance> jobExecutionInstances = new HashSet<>();
    try {
      for (AbstractSchedule schedule : job.getSchedules()) {
        JobExecutionInstance jobExecutionInstance = schedule(schedule, false);
        if (jobExecutionInstance != null) {
          jobExecutionInstances.add(jobExecutionInstance);
        }
      }
    } catch (Exception e) {
      throw (new RuntimeException(e));
    }

    waitForJobsToComplete(job, jobExecutionInstances);
  }

  protected void waitForJobsToComplete(
      Job job, final Set<JobExecutionInstance> jobExecutionInstances) {
    if (job.isSynchronous()) {
      while (jobExecutionInstances.size() > 0) {
        final Set<JobExecutionInstance> instancesToRemove = new HashSet<>();

        for (JobExecutionInstance jobExecutionInstance : jobExecutionInstances) {
          if (jobExecutionInstance.getFuture().isDone()
              || jobExecutionInstance.getFuture().isCancelled()) {
            instancesToRemove.add(jobExecutionInstance);
          }
        }

        jobExecutionInstances.removeAll(instancesToRemove);
        if (jobExecutionInstances.size() > 0) {
          // TODO: make this delay configurable
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
          }
        }
      }
    }
  }

  @MonitorJob
  protected JobExecutionInstance schedule(AbstractSchedule schedule, final boolean isFuture) {
    JobExecution jobExecution = queue(schedule);

    if (jobExecution == null) return null;

    ListenableFuture future =
        ScheduleType.get(schedule.getClass())
            .schedule(
                listeningScheduledExecutorService,
                jobExecutionEnvironmentManager,
                jobExecution,
                schedule,
                isFuture);
    return new JobExecutionInstance(jobExecution, future);
  }

  public JobExecution queue(AbstractSchedule schedule) {
    if (!isAcceptingNewJobs()) return null;

    if (hasOtherScheduledExecution(schedule)) return null;

    return createNewJobExecution(schedule);
  }

  protected boolean hasOtherScheduledExecution(AbstractSchedule schedule) {
    return scheduleRepositoryProvider.get().hasExistingPendingOrScheduledJobExecution(schedule);
  }

  @Transactional
  protected JobExecution createNewJobExecution(AbstractSchedule schedule) {
    final Repository repository = repositoryProvider.get();

    repository.refresh(schedule);

    JobExecution jobExecution = new JobExecution(schedule);
    schedule.getJobExecutions().add(jobExecution);

    return (JobExecution) repository.persist(jobExecution);
  }

  public JobExecution scheduleNextRun(AbstractSchedule schedule) {
    if (ScheduleType.get(schedule.getClass()).isRecurring(schedule)) {
      JobExecutionInstance jobExecutionInstance = schedule(schedule, true);
      if (jobExecutionInstance != null) return jobExecutionInstance.getJobExecution();
    }

    // either not recurring or another instance is outstanding
    return null;
  }

  // TODO: implement, should we have a delay? if yes, that means we would change the schedule, that
  // is okay, when creating the new schedule, subtract 1 from the retries remaining and set this one
  // to 0
  public JobExecution rescheduleFailedExecution(AbstractSchedule schedule) {
    //    schedule.setRemainingRetries(schedule.getRemainingRetries() - 1);
    final int remainingRetries = schedule.getRemainingRetries() - 1;
    schedule.setRemainingRetries(0);

    // TODO: perhaps make this a bit more dynamic
    AtDateTimeSchedule newSchedule = new AtDateTimeSchedule(LocalDateTime.now().plusMinutes(1));
    newSchedule.setRemainingRetries(remainingRetries);

    schedule.getJob().getSchedules().add(newSchedule);

    return null;
  }

  @Override
  public void shutdownNow() {
    listeningScheduledExecutorService.shutdownNow();
  }

  @Override
  public void shutdown() {
    listeningScheduledExecutorService.shutdown();
  }

  @Transactional(ignore = {NoResultException.class})
  @Override
  public Job cancel(Job job) {
    for (AbstractSchedule schedule : job.getSchedules()) {
      for (JobExecution jobExecution : schedule.getJobExecutions()) {
        cancel(jobExecution);
      }
    }

    return job;
  }

  @Transactional
  public JobExecution cancel(JobExecution jobExecution) {
    if (JobExecutionState.Scheduled.equals(jobExecution.getJobExecutionState())
        || JobExecutionState.Pending.equals(jobExecution.getJobExecutionState())) {
      jobExecution.setJobExecutionState(JobExecutionState.Cancelled);
      futures.get(jobExecution.getId()).cancel(true);

      repositoryProvider.get().merge(jobExecution);
    }

    return jobExecution;
  }

  /**
   * Reschedule jobs that were pending / scheduled at the time of shutdown.
   *
   * @throws Exception
   */
  @Override
  protected void startUp() throws Exception {
    for (final AbstractSchedule schedule : scheduleRepositoryProvider.get().findPendingExecutions())
      queue(schedule);
  }

  @Override
  protected void shutDown() throws Exception {
    shutdownNow();
  }
}
