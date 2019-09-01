package com.walterjwhite.job.impl.repository;

import com.google.inject.persist.Transactional;
import com.walterjwhite.datastore.api.repository.GenericEntityRepository;
import com.walterjwhite.datastore.modules.criteria.CriteriaQueryConfiguration;
import com.walterjwhite.job.api.model.JobExecutor;
import com.walterjwhite.job.api.model.JobExecutor_;
import com.walterjwhite.queue.api.job.CallableJob;
import javax.inject.Inject;

public class JobExecutorEntityRepository extends GenericEntityRepository<JobExecutor> {
  @Inject
  public JobExecutorEntityRepository(EntityManager entityManager, CriteriaBuilder criteriaBuilder) {
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
