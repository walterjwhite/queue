package com.walterjwhite.queue;

import com.walterjwhite.queue.event.service.EventService;
import javax.jdo.PersistenceManager;
import javax.jdo.listener.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Observes JDO events and relies on the event service to do the legwork 1. find any subscribers for
 * the given event 2. create those "events", which will in turn rely on JDO for querying and
 * persisting the event
 */
@Getter
@RequiredArgsConstructor
public class JDOEventQueuer
    implements CreateLifecycleListener, StoreLifecycleListener, DeleteLifecycleListener {
  protected final PersistenceManager persistenceManager;
  protected final EventService eventService;

  @Override
  public void postCreate(InstanceLifecycleEvent event) {
    // check if not auditable ...
  }

  @Override
  public void preStore(InstanceLifecycleEvent event) {
    System.out.println("preStore:" + event);
  }

  @Override
  public void postStore(InstanceLifecycleEvent event) {
    System.out.println("postStore:" + event);
  }

  @Override
  public void preDelete(InstanceLifecycleEvent event) {}

  @Override
  public void postDelete(InstanceLifecycleEvent event) {}
}
