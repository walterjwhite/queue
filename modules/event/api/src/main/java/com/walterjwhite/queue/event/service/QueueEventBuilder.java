package com.walterjwhite.queue.event.service;

import com.walterjwhite.datastore.api.model.entity.AbstractEntity;
import com.walterjwhite.queue.event.model.QueuedEvent;

public interface QueueEventBuilder {
  QueuedEvent build(AbstractEntity entity, final Class jobSubscriberClass);
}
