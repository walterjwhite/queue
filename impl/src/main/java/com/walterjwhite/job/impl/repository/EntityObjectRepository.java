package com.walterjwhite.job.impl.repository;

import com.walterjwhite.datastore.criteria.AbstractRepository;
import com.walterjwhite.job.api.model.EntityObject;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;

public class EntityObjectRepository extends AbstractRepository<EntityObject> {
  @Inject
  public EntityObjectRepository(EntityManager entityManager, CriteriaBuilder criteriaBuilder) {
    super(entityManager, criteriaBuilder, EntityObject.class);
  }
}
