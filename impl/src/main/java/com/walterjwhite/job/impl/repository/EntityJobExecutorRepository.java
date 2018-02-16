package com.walterjwhite.job.impl.repository;

import com.walterjwhite.datastore.api.model.entity.EntityType;
import com.walterjwhite.datastore.criteria.AbstractRepository;
import com.walterjwhite.datastore.criteria.CriteriaQueryConfiguration;
import com.walterjwhite.job.api.model.EntityJobExecutor;
import com.walterjwhite.job.api.model.EntityJobExecutor_;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityJobExecutorRepository extends AbstractRepository<EntityJobExecutor> {
  private static final Logger LOGGER = LoggerFactory.getLogger(EntityJobExecutorRepository.class);

  @Inject
  public EntityJobExecutorRepository(EntityManager entityManager, CriteriaBuilder criteriaBuilder) {
    super(entityManager, criteriaBuilder, EntityJobExecutor.class);
  }

  // TODO: periodically retrieve jobs and throw them into the executor service.
  //  @Transactional
  //  @Transactional(ignore = {NoResultException.class})
  public EntityJobExecutor findByResourceType(EntityType entityType) {
    final CriteriaQueryConfiguration<EntityJobExecutor> jobCriteriaQueryConfiguration =
        getCriteriaQuery();

    Predicate condition =
        criteriaBuilder.equal(
            jobCriteriaQueryConfiguration.getRoot().get(EntityJobExecutor_.entityType), entityType);
    jobCriteriaQueryConfiguration.getCriteriaQuery().where(condition);

    return entityManager
        .createQuery(jobCriteriaQueryConfiguration.getCriteriaQuery())
        .getSingleResult();
  }
}
