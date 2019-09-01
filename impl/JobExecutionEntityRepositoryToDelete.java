package com.walterjwhite.job.impl.repository;

import com.google.inject.persist.Transactional;
import com.walterjwhite.datastore.api.repository.GenericEntityRepository;
import com.walterjwhite.job.api.enumeration.JobExecutionState;
import com.walterjwhite.job.api.model.JobExecution;
import java.time.LocalDateTime;
import javax.inject.Inject;

public class JobExecutionEntityRepositoryToDelete extends GenericEntityRepository<JobExecution> {
  @Inject
  public JobExecutionEntityRepositoryToDelete(
      EntityManager entityManager, CriteriaBuilder criteriaBuilder) {
    super(entityManager, criteriaBuilder, JobExecution.class);
  }

  //  @Transactional
  //  public JobExecution fetchJobExecutionInstance(final Integer jobExecutionId) {
  //    JobExecution jobExecution = entityManager.find(JobExecution.class, jobExecutionId);
  //
  //    if (JobExecutionState.Pending.equals(jobExecution.getExecutionState())) {
  //      jobExecution.setJobExecutionState(JobExecutionState.Scheduled);
  //      entityManager.merge(jobExecution);
  //    }
  //
  //    return jobExecution;
  //  }

  // TODO: the queuedJob monitor interceptor should do this work and merely call update ...
  @Transactional
  public JobExecution updateJobExecution(JobExecution jobExecution, Throwable throwable) {
    jobExecution = entityManager.find(JobExecution.class, jobExecution.getId());
    entityManager.refresh(jobExecution);

    jobExecution.setEndDateTime(LocalDateTime.now());
    if (throwable != null) {
      jobExecution.setJobExecutionState(JobExecutionState.Exception);
      jobExecution.setThrowable(throwable);

      //
      // jobExecution.getSchedule().setRemainingRetries(jobExecution.getSchedule().getRemainingRetries() - 1);
    } else {
      jobExecution.setJobExecutionState(JobExecutionState.Completed);
    }

    return merge(jobExecution);
  }
}
