package com.walterjwhite.job.impl.repository;

import com.google.inject.persist.Transactional;
import com.walterjwhite.datastore.criteria.AbstractRepository;
import com.walterjwhite.datastore.criteria.CriteriaQueryConfiguration;
import com.walterjwhite.job.api.model.JobExecutor;
import com.walterjwhite.job.api.model.JobExecutor_;
import com.walterjwhite.queue.api.job.CallableJob;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobExecutorRepository extends AbstractRepository<JobExecutor> {
  private static final Logger LOGGER = LoggerFactory.getLogger(JobExecutorRepository.class);

  @Inject
  public JobExecutorRepository(EntityManager entityManager, CriteriaBuilder criteriaBuilder) {
    super(entityManager, criteriaBuilder, JobExecutor.class);
  }

  public JobExecutor findOrCreate(Class<? extends CallableJob> callableJobClass) {
    final CriteriaQueryConfiguration<JobExecutor> jobCriteriaQueryConfiguration =
        getCriteriaQuery();

    Predicate condition =
        criteriaBuilder.equal(
            jobCriteriaQueryConfiguration.getRoot().get(JobExecutor_.name),
            callableJobClass.getName());
    jobCriteriaQueryConfiguration.getCriteriaQuery().where(condition);

    try {
      return entityManager
          .createQuery(jobCriteriaQueryConfiguration.getCriteriaQuery())
          .getSingleResult();
    } catch (NoResultException e) {
      return createNewJobExecutor(callableJobClass);
    }
  }

  @Transactional
  protected JobExecutor createNewJobExecutor(Class<? extends CallableJob> callableJobClass) {
    return (persist(new JobExecutor(callableJobClass.getName())));
  }
}
