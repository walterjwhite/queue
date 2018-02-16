package com.walterjwhite.job.impl.repository;

import com.walterjwhite.datastore.api.model.entity.EntityReference;
import com.walterjwhite.datastore.criteria.AbstractRepository;
import com.walterjwhite.datastore.criteria.CriteriaQueryConfiguration;
import com.walterjwhite.job.api.model.*;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

public class JobRepository extends AbstractRepository<Job> {
  @Inject
  public JobRepository(EntityManager entityManager, CriteriaBuilder criteriaBuilder) {
    super(entityManager, criteriaBuilder, Job.class);
  }
  //
  //  public List<Job> findPendingJobs() {
  //    final CriteriaQueryConfiguration<Job> jobCriteriaQueryConfiguration = getCriteriaQuery();
  //    Predicate noJobExecutions =
  //
  // criteriaBuilder.isEmpty(jobCriteriaQueryConfiguration.getRoot().get(Job_.jobExecutions));
  //
  //    Join<Job, JobExecution> jobExecution =
  //        jobCriteriaQueryConfiguration.getRoot().join(Job_.jobExecutions);
  //    Predicate pendingJobExecutions =
  //        criteriaBuilder.equal(
  //            jobExecution.get(JobExecution_.jobExecutionState), JobExecutionState.Pending);
  //    Predicate scheduledJobExecutions =
  //        criteriaBuilder.equal(
  //            jobExecution.get(JobExecution_.jobExecutionState), JobExecutionState.Scheduled);
  //
  //    Predicate incompleteJobExecutions =
  //        criteriaBuilder.or(pendingJobExecutions, scheduledJobExecutions);
  //
  //    jobCriteriaQueryConfiguration
  //        .getCriteriaQuery()
  //        .where(criteriaBuilder.or(noJobExecutions, incompleteJobExecutions));
  //    return (entityManager
  //        .createQuery(jobCriteriaQueryConfiguration.getCriteriaQuery())
  //        .getResultList());
  //  }
  //
  //  public Job findPendingJobByExecutor(JobExecutor jobExecutor) {
  //    final CriteriaQueryConfiguration<Job> jobCriteriaQueryConfiguration = getCriteriaQuery();
  //    // find pending jobs
  //    Predicate jobExecutorCondition =
  //        criteriaBuilder.equal(
  //            jobCriteriaQueryConfiguration.getRoot().get(Job_.jobExecutor), jobExecutor);
  //    Predicate pendingStateCondition =
  //        criteriaBuilder.equal(
  //            jobCriteriaQueryConfiguration.getRoot().get(Job_.scheduleState),
  //            JobExecutionState.Pending);
  //    Predicate entityCondition =
  //
  // criteriaBuilder.isNull(jobCriteriaQueryConfiguration.getRoot().get(Job_.entityReference));
  //
  //    jobCriteriaQueryConfiguration
  //        .getCriteriaQuery()
  //        .where(criteriaBuilder.and(jobExecutorCondition, pendingStateCondition,
  // entityCondition));
  //    return (entityManager
  //        .createQuery(jobCriteriaQueryConfiguration.getCriteriaQuery())
  //        .getSingleResult());
  //  }
  //
  //  public List<Job> findRecurringJobs() {
  //    final CriteriaQueryConfiguration<Job> jobCriteriaQueryConfiguration = getCriteriaQuery();
  //
  //    Join<Job, CronSchedule> schedule =
  // jobCriteriaQueryConfiguration.getRoot().join(Job_.schedule);
  //
  //    // find pending jobs
  //    Predicate hasCron = criteriaBuilder.isNotNull(schedule.get(Schedule_.cronExpression));
  //
  //    Predicate hasFixedDelay = criteriaBuilder.greaterThan(schedule.get(Schedule_.fixedDelay),
  // 0);
  //
  //    jobCriteriaQueryConfiguration
  //        .getCriteriaQuery()
  //        .where(criteriaBuilder.or(hasCron, hasFixedDelay));
  //    return (entityManager
  //        .createQuery(jobCriteriaQueryConfiguration.getCriteriaQuery())
  //        .getResultList());
  //  }
  //
  //  public Job findPendingJobByExecutorAndResource(
  //      JobExecutor jobExecutor, EntityReference entityReference) {
  //    final CriteriaQueryConfiguration<Job> jobCriteriaQueryConfiguration = getCriteriaQuery();
  //    // find pending jobs
  //    Predicate jobExecutorCondition =
  //        criteriaBuilder.equal(
  //            jobCriteriaQueryConfiguration.getRoot().get(Job_.jobExecutor), jobExecutor);
  //    Predicate pendingStateCondition =
  //        criteriaBuilder.equal(
  //            jobCriteriaQueryConfiguration.getRoot().get(Job_.scheduleState),
  //            JobExecutionState.Pending);
  //    Predicate entityCondition =
  //        criteriaBuilder.equal(
  //            jobCriteriaQueryConfiguration.getRoot().get(Job_.entityReference), entityReference);
  //
  //    jobCriteriaQueryConfiguration
  //        .getCriteriaQuery()
  //        .where(criteriaBuilder.and(jobExecutorCondition, pendingStateCondition,
  // entityCondition));
  //    return (entityManager
  //        .createQuery(jobCriteriaQueryConfiguration.getCriteriaQuery())
  //        .getSingleResult());
  //  }
  //
  //    public Job findPendingJobByExecutorAndResourceAndScheduleAndQueue(
  //        JobExecutor jobExecutor,
  //        EntityReference entityReference,
  //        AbstractSchedule schedule,
  //        Queue queue) {
  //      final CriteriaQueryConfiguration<Job> jobCriteriaQueryConfiguration = getCriteriaQuery();
  //      // find pending jobs
  //      Predicate jobExecutorCondition =
  //          criteriaBuilder.equal(
  //              jobCriteriaQueryConfiguration.getRoot().get(Job_.jobExecutor), jobExecutor);
  //      Predicate pendingStateCondition =
  //          criteriaBuilder.equal(
  //              jobCriteriaQueryConfiguration.getRoot().get(Job_.scheduleState),
  //              JobExecutionState.Pending);
  //      Predicate entityCondition =
  //          criteriaBuilder.equal(
  //              jobCriteriaQueryConfiguration.getRoot().get(Job_.entityReference),
  // entityReference);
  //
  ////      Predicate scheduleCondition =
  ////          criteriaBuilder.equal(jobCriteriaQueryConfiguration.getRoot().get(Job_.schedule),
  // schedule);
  //
  //      Predicate queueCondition =
  //          criteriaBuilder.equal(jobCriteriaQueryConfiguration.getRoot().get(Job_.queue), queue);
  //
  //      jobCriteriaQueryConfiguration
  //          .getCriteriaQuery()
  //          .where(
  //              criteriaBuilder.and(
  //                  jobExecutorCondition,
  //                  pendingStateCondition,
  //                  entityCondition,
  //                  scheduleCondition,
  //                  queueCondition));
  //      return (entityManager
  //          .createQuery(jobCriteriaQueryConfiguration.getCriteriaQuery())
  //          .getSingleResult());
  //    }

  public Job findJobByExecutorAndResource(
      JobExecutor jobExecutor, EntityReference entityReference) {
    final CriteriaQueryConfiguration<Job> jobCriteriaQueryConfiguration = getCriteriaQuery();
    // find pending jobs
    Predicate jobExecutorCondition =
        criteriaBuilder.equal(
            jobCriteriaQueryConfiguration.getRoot().get(Job_.jobExecutor), jobExecutor);
    Predicate entityCondition =
        criteriaBuilder.equal(
            jobCriteriaQueryConfiguration.getRoot().get(Job_.entityReference), entityReference);

    jobCriteriaQueryConfiguration
        .getCriteriaQuery()
        .where(criteriaBuilder.and(jobExecutorCondition, entityCondition));
    return (entityManager
        .createQuery(jobCriteriaQueryConfiguration.getCriteriaQuery())
        .getSingleResult());
  }
}
