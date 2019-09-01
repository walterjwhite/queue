package com.walterjwhite.queue.jpa.guice;

import com.walterjwhite.datastore.api.annotation.Supports;
import com.walterjwhite.infrastructure.datastore.modules.criteria.CriteriaQueryConfiguration;
import com.walterjwhite.infrastructure.datastore.modules.criteria.query.JpaCriteriaQueryBuilder;
import com.walterjwhite.queue.api.model.*;
import com.walterjwhite.queue.jpa.query.FindRecurringAssignableQueuedJobsQuery;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

@Supports(FindRecurringAssignableQueuedJobsQuery.class)
public class RecurringAssignableJobsQueryBuilder
    extends JpaCriteriaQueryBuilder<QueuedJob, List, FindRecurringAssignableQueuedJobsQuery> {

  @Override
  protected Predicate buildPredicate(
      CriteriaBuilder criteriaBuilder,
      CriteriaQueryConfiguration<QueuedJob, List> criteriaQueryConfiguration,
      FindRecurringAssignableQueuedJobsQuery queryConfiguration) {
    final Join<QueuedJob, JobExecution> jobExecutionJoin =
        criteriaQueryConfiguration.getRoot().join(AbstractQueued_.currentJobExecution);

    final Join<QueuedJob, ScheduleInstance> scheduleInstanceJoin =
        criteriaQueryConfiguration.getRoot().join(QueuedJob_.scheduleInstance);

    return criteriaBuilder.and(
        isQueued(criteriaBuilder, criteriaQueryConfiguration, queryConfiguration),
        isRecurringAssignable(criteriaBuilder, queryConfiguration, jobExecutionJoin),
        isRecurring(criteriaBuilder, scheduleInstanceJoin));
  }

  protected Predicate isQueued(
      CriteriaBuilder criteriaBuilder,
      CriteriaQueryConfiguration<QueuedJob, List> criteriaQueryConfiguration,
      FindRecurringAssignableQueuedJobsQuery queryConfiguration) {
    return criteriaBuilder.equal(
        criteriaQueryConfiguration.getRoot().get(QueuedJob_.queueState),
        queryConfiguration.getQueueState());
  }

  protected Predicate isRecurringAssignable(
      CriteriaBuilder criteriaBuilder,
      FindRecurringAssignableQueuedJobsQuery queryConfiguration,
      Join<QueuedJob, JobExecution> jobExecutionJoin) {
    return criteriaBuilder.in(
        jobExecutionJoin
            .get(JobExecution_.executionState)
            .in(queryConfiguration.getExecutionStates()));
  }

  protected Predicate isRecurring(
      CriteriaBuilder criteriaBuilder,
      final Join<QueuedJob, ScheduleInstance> scheduleInstanceJoin) {
    return criteriaBuilder.gt(scheduleInstanceJoin.get(ScheduleInstance_.fixedDelay), 0);
  }
}
