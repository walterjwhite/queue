package com.walterjwhite.job.external.queue.api;

import com.walterjwhite.datastore.api.model.entity.AbstractEntity;
import com.walterjwhite.queue.api.model.Queue;

public interface ExternalQueueService {
  /**
   * Implementation is taking an entity that was already serialized, compressed, and encrypted, and
   * then writing that.
   *
   * @param queue queue to write data
   * @param entity entity to queue
   */
  void write(Queue queue, AbstractEntity entity) throws Exception;

  void prepare(Queue queue, AbstractEntity entity);
}
