package com.walterjwhite.job.external.queue.impl.service;

import com.walterjwhite.datastore.api.model.entity.AbstractEntity;
import com.walterjwhite.job.external.queue.api.ExternalQueueService;
import com.walterjwhite.queue.api.model.Queue;
import java.io.OutputStream;
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
    try (final OutputStream outputStream = getOutputStream(queue, null)) {
      entityService.write(entity, outputStream);
    }
  }

  @Override
  public void prepare(Queue queue, AbstractEntity entity) {}

  // protected abstract void doWrite(Queue queue, OutputStream outputStream);

  protected abstract OutputStream getOutputStream(Queue queue, OutputStream outputStream)
      throws Exception;
}
