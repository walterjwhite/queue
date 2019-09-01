package com.walterjwhite.queue.jpa.guice;

import com.walterjwhite.datastore.api.annotation.Supports;
import com.walterjwhite.infrastructure.datastore.modules.criteria.CriteriaQueryConfiguration;
import com.walterjwhite.infrastructure.datastore.modules.criteria.query.JpaCriteriaQueryBuilder;
import com.walterjwhite.queue.api.model.AbstractQueued;
import com.walterjwhite.queue.api.model.AbstractQueued_;
import com.walterjwhite.queue.api.model.JobExecution;
import com.walterjwhite.queue.api.model.JobExecution_;
import com.walterjwhite.queue.jpa.query.FindAssignableQueuedJobsQuery;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

@Supports(FindAssignableQueuedJobsQuery.class)
public class AssignableJobsQueryBuilder
    extends JpaCriteriaQueryBuilder<AbstractQueued, List, FindAssignableQueuedJobsQuery> {

  @Override
  public List get(EntityManager entityManager, FindAssignableQueuedJobsQuery query) {
    return null;
  }

  @Override
  protected Predicate buildPredicate(
      CriteriaBuilder criteriaBuilder,
      CriteriaQueryConfiguration<AbstractQueued, List> criteriaQueryConfiguration,
      FindAssignableQueuedJobsQuery queryConfiguration) {
    final Join<AbstractQueued, JobExecution> jobExecutionJoin =
        criteriaQueryConfiguration.getRoot().join(AbstractQueued_.currentJobExecution);

    return criteriaBuilder.and(
        isQueued(criteriaBuilder, criteriaQueryConfiguration, queryConfiguration),
        isAssignable(
            criteriaBuilder, criteriaQueryConfiguration, queryConfiguration, jobExecutionJoin));
  }

  protected Predicate isQueued(
      CriteriaBuilder criteriaBuilder,
      CriteriaQueryConfiguration<AbstractQueued, List> criteriaQueryConfiguration,
      FindAssignableQueuedJobsQuery queryConfiguration) {
    return criteriaBuilder.equal(
        criteriaQueryConfiguration.getRoot().get(AbstractQueued_.queueState),
        queryConfiguration.getQueueState());
  }

  protected Predicate isAssignable(
      CriteriaBuilder criteriaBuilder,
      CriteriaQueryConfiguration<AbstractQueued, List> criteriaQueryConfiguration,
      FindAssignableQueuedJobsQuery queryConfiguration,
      Join<AbstractQueued, JobExecution> jobExecutionJoin) {
    return criteriaBuilder.in(
        jobExecutionJoin
            .get(JobExecution_.executionState)
            .in(queryConfiguration.getExecutionStates()));
  }
}
