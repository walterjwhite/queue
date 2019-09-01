package com.walterjwhite.queue.jpa.service;

import com.walterjwhite.datastore.api.event.enumeration.JPAActionType;
import com.walterjwhite.datastore.api.model.entity.AbstractEntity;
import com.walterjwhite.infrastructure.inject.core.helper.ApplicationHelper;
import com.walterjwhite.queue.api.model.Unqueueable;

/**
 * Must be registered via JPA orm.xml to function. NOTE: in order to avoid cycles, this listener
 * ignores any events from classes marked with Unqueueable
 */
public class JPAEventListenerEventQueuer {
  protected transient JPAEventQueueService jpaEventQueueService;

  @PostPersist
  public void onPostPersist(Object o) {
    queueEventProcessorCallableJob((AbstractEntity) o, JPAActionType.Persist);
  }

  @PostUpdate
  public void onPostUpdate(Object o) {
    queueEventProcessorCallableJob((AbstractEntity) o, JPAActionType.Update);
  }

  @PostRemove
  public void onPostRemove(Object o) {
    queueEventProcessorCallableJob((AbstractEntity) o, JPAActionType.Remove);
  }

  // TODO: cannot do this because it is doing more work in a transaction ...
  protected void queueEventProcessorCallableJob(
      AbstractEntity entity, JPAActionType jpaActionType) {
    if (!isProcess(entity)) return;

    setupEventQueueService();
    jpaEventQueueService.queue(entity, jpaActionType);
  }

  protected boolean isProcess(AbstractEntity entity) {
    return (!Unqueueable.class.isAssignableFrom(entity.getClass()));
  }

  protected void setupEventQueueService() {
    if (jpaEventQueueService == null)
      jpaEventQueueService =
          ApplicationHelper.getApplicationInstance()
              .getInjector()
              .getInstance(JPAEventQueueService.class);
  }
}
