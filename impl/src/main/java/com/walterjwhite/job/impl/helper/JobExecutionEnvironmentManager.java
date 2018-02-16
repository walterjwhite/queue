package com.walterjwhite.job.impl.helper;

import com.walterjwhite.datastore.api.model.entity.AbstractEntity;
import com.walterjwhite.google.guice.GuiceHelper;
import com.walterjwhite.job.api.enumeration.JobExecutionState;
import com.walterjwhite.job.api.model.Job;
import com.walterjwhite.job.api.model.JobExecution;
import com.walterjwhite.job.impl.repository.JobExecutionRepository;
import com.walterjwhite.queue.api.job.CallableJob;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

@Singleton
public class JobExecutionEnvironmentManager {
  protected final Provider<JobExecutionRepository> jobExecutionRepositoryProvider;
  protected final Provider<EntityManager> entityManagerProvider;

  @Inject
  public JobExecutionEnvironmentManager(
      Provider<JobExecutionRepository> jobExecutionRepositoryProvider,
      Provider<EntityManager> entityManagerProvider) {
    super();
    this.jobExecutionRepositoryProvider = jobExecutionRepositoryProvider;
    this.entityManagerProvider = entityManagerProvider;
  }

  /**
   * This gets an instance of the CallableJob using Guice injection. TODO: automatically inject the
   * entityReference into the callable (constructor injection).
   */
  protected CallableJob prepareCallableJob(JobExecution jobExecution) {
    try {
      CallableJob callableJob =
          (CallableJob)
              GuiceHelper.getGuiceInjector()
                  .getInstance(
                      Class.forName(
                          jobExecution.getSchedule().getJob().getJobExecutor().getName()));

      callableJob.setJobExecution(jobExecution);
      callableJob.setEntity(get(jobExecution.getSchedule().getJob()));

      return (callableJob);
    } catch (ClassNotFoundException e) {
      throw (new RuntimeException(e));
    }
  }

  // TODO: use entity bridge service here
  // we could be using a CSV, ES, etc.
  protected AbstractEntity get(Job job) throws ClassNotFoundException {
    if (job.getEntityReference() == null) return (null);

    final Class<? extends AbstractEntity> entityClass =
        (Class<? extends AbstractEntity>)
            Class.forName(job.getEntityReference().getEntityType().getName());
    return (entityManagerProvider.get().find(entityClass, job.getEntityReference().getEntityId()));
  }

  public Runnable getRunnable(final String jobExecutionId) {
    return (() -> {
      JobExecution jobExecution =
          jobExecutionRepositoryProvider.get().fetchJobExecutionInstance(jobExecutionId);
      if (!JobExecutionState.Scheduled.equals(jobExecution.getJobExecutionState()))
        throw (new IllegalStateException(
            "Job is in an unexpected state, not scheduling:"
                + jobExecution.getJobExecutionState()));

      try {
        prepareCallableJob(jobExecution).call();
      } catch (Exception e) {
        throw (new RuntimeException(e));
      }
    });
  }
}
