package com.walterjwhite.job.api.model;

import com.walterjwhite.datastore.api.model.entity.EntityType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class EntityJobExecutor extends JobExecutor {
  /** The entity type this executor handles */
  @ManyToOne(optional = false)
  @JoinColumn(nullable = false)
  protected EntityType entityType;

  public EntityJobExecutor(String callableClassName, EntityType entityType) {
    super(callableClassName);
    this.entityType = entityType;
  }

  public EntityJobExecutor() {
    super();
  }

  public EntityType getEntityType() {
    return entityType;
  }

  public void setEntityType(EntityType entityType) {
    this.entityType = entityType;
  }
}
