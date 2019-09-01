package com.walterjwhite.queue.event.service;

import com.walterjwhite.datastore.api.model.entity.AbstractEntity;
import com.walterjwhite.queue.event.enumeration.EventActionType;
import com.walterjwhite.queue.event.model.QueuedEvent;
import java.util.Set;

public interface EventService {
  /**
   * Fires off Set<QueuedEvent> for the given entity. One event is fired for each @SubscribeTo event
   * handler.
   *
   * @param entity
   * @return
   */
  Set<QueuedEvent> queue(AbstractEntity entity, EventActionType eventActionType);
}
