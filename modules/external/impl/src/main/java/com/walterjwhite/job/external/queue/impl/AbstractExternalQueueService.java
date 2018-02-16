package com.walterjwhite.job.external.queue.impl;

import com.walterjwhite.datastore.api.model.entity.AbstractEntity;
import com.walterjwhite.job.api.model.Queue;
import com.walterjwhite.job.external.queue.api.ExternalQueueService;
import javax.inject.Inject;

public abstract class AbstractExternalQueueService implements ExternalQueueService {
  protected final EntityService entityService;

  @Inject
  protected AbstractExternalQueueService(EntityService entityService) {
    super();
    this.entityService = entityService;
  }

  @Override
  public void write(Queue queue, AbstractEntity entity) throws Exception {
    doWrite(queue, entityService.write(entity));
  }

  @Override
  public void prepare(Queue queue, AbstractEntity entity) {}

  protected abstract void doWrite(Queue queue, byte[] data);
}
