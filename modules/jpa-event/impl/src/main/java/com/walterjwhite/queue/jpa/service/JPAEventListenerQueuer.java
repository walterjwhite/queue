package com.walterjwhite.queue.jpa.service;

import com.walterjwhite.datastore.api.event.enumeration.JPAActionType;
import com.walterjwhite.datastore.api.model.entity.AbstractEntity;
import com.walterjwhite.datastore.api.repository.ObserverEntityManager;
import com.walterjwhite.datastore.api.repository.observer.persist.PostPersistObserver;
import com.walterjwhite.datastore.api.repository.observer.remove.PostRemoveObserver;
import com.walterjwhite.datastore.api.repository.observer.update.PostUpdateObserver;
import com.walterjwhite.queue.api.model.Unqueueable;
import javax.inject.Inject;

public class JPAEventListenerQueuer
    implements PostPersistObserver, PostUpdateObserver, PostRemoveObserver {
  protected final JPAEventQueueService jpaEventQueueService;

  @Inject
  public JPAEventListenerQueuer(JPAEventQueueService jpaEventQueueService) {
    this.jpaEventQueueService = jpaEventQueueService;
  }

  @Override
  public void postPersist(ObserverEntityManager observerEntityManager, AbstractEntity entity) {
    queueEventProcessorCallableJob(entity, JPAActionType.Persist);
  }

  @Override
  public void postRemove(ObserverEntityManager observerEntityManager, AbstractEntity entity) {
    queueEventProcessorCallableJob(entity, JPAActionType.Remove);
  }

  @Override
  public void postUpdate(ObserverEntityManager observerEntityManager, AbstractEntity entity) {
    queueEventProcessorCallableJob(entity, JPAActionType.Update);
  }

  protected void queueEventProcessorCallableJob(
      AbstractEntity entity, JPAActionType jpaActionType) {
    if (!isProcess(entity)) return;

    jpaEventQueueService.queue(entity, jpaActionType);
  }

  protected boolean isProcess(AbstractEntity entity) {
    return (!Unqueueable.class.isAssignableFrom(entity.getClass()));
  }
}
