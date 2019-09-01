package com.walterjwhite.queue.jpa.guice;

import com.walterjwhite.datastore.api.annotation.Supports;
import com.walterjwhite.infrastructure.datastore.modules.criteria.CriteriaQueryConfiguration;
import com.walterjwhite.infrastructure.datastore.modules.criteria.query.JpaCriteriaQueryBuilder;
import com.walterjwhite.queue.api.model.AbstractQueued;
import com.walterjwhite.queue.api.model.AbstractQueued_;
import com.walterjwhite.queue.api.model.JobExecution;
import com.walterjwhite.queue.api.model.JobExecution_;
import com.walterjwhite.queue.jpa.query.FindAbortedQueuedQuery;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

@Supports(FindAbortedQueuedQuery.class)
public class AbortedJobsQueryBuilder
    extends JpaCriteriaQueryBuilder<AbstractQueued, List, FindAbortedQueuedQuery> {

  @Override
  protected Predicate buildPredicate(
      CriteriaBuilder criteriaBuilder,
      CriteriaQueryConfiguration<AbstractQueued, List> criteriaQueryConfiguration,
      FindAbortedQueuedQuery queryConfiguration) {
    final Join<AbstractQueued, JobExecution> jobExecutionJoin =
        criteriaQueryConfiguration.getRoot().join(AbstractQueued_.currentJobExecution);

    return criteriaBuilder.and(
        isQueued(criteriaBuilder, criteriaQueryConfiguration, queryConfiguration),
        isExecutionStateSupported(
            criteriaBuilder, criteriaQueryConfiguration, queryConfiguration, jobExecutionJoin),
        isExecutionTimedOut(criteriaBuilder, jobExecutionJoin));
  }

  protected Predicate isQueued(
      CriteriaBuilder criteriaBuilder,
      CriteriaQueryConfiguration<AbstractQueued, List> criteriaQueryConfiguration,
      FindAbortedQueuedQuery queryConfiguration) {
    return criteriaBuilder.equal(
        criteriaQueryConfiguration.getRoot().get(AbstractQueued_.queueState),
        queryConfiguration.getQueueState());
  }

  protected Predicate isExecutionStateSupported(
      CriteriaBuilder criteriaBuilder,
      CriteriaQueryConfiguration<AbstractQueued, List> criteriaQueryConfiguration,
      FindAbortedQueuedQuery queryConfiguration,
      Join<AbstractQueued, JobExecution> jobExecutionJoin) {
    return criteriaBuilder.in(
        jobExecutionJoin
            .get(JobExecution_.executionState)
            .in(queryConfiguration.getExecutionStates()));
  }

  protected Predicate isExecutionTimedOut(
      CriteriaBuilder criteriaBuilder, Join<AbstractQueued, JobExecution> jobExecutionJoin) {
    return criteriaBuilder.greaterThan(
        jobExecutionJoin.get(JobExecution_.timeoutDateTime), LocalDateTime.now());
  }
}
