package com.walterjwhite.queue.jpa.service;

import com.walterjwhite.datastore.api.event.enumeration.JPAActionType;
import com.walterjwhite.datastore.api.model.entity.AbstractEntity;

public interface JPAEventQueueService {

  /**
   * Creates all corresponding jobs for the given entity. An entity may have any number of jobs
   * (JobExecutor) for it
   *
   * @param entity
   * @return
   */
  void queue(AbstractEntity entity, JPAActionType jpaActionType);
}
