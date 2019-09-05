package com.walterjwhite.queue.event.service;

import com.walterjwhite.datastore.api.model.entity.AbstractEntity;
import com.walterjwhite.queue.event.enumeration.EventActionType;
import com.walterjwhite.queue.event.model.QueuedEvent;
import java.util.Set;

public interface EventService {
  /**
   * Fires off events for the given entity. One event is fired for each @SubscribeTo event handler.
   *
   * @param entity the entity to queue up actions
   * @param eventActionType the type of action being performed causing this event
   * @return all of the related queued events responding to this event
   */
  Set<QueuedEvent> queue(AbstractEntity entity, EventActionType eventActionType);
}
