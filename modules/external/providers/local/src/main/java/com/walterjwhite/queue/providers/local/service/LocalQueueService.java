package com.walterjwhite.queue.providers.local.service;

import com.walterjwhite.job.external.queue.impl.service.AbstractExternalQueueService;
import com.walterjwhite.job.external.queue.impl.service.EntityService;
import com.walterjwhite.queue.api.model.Queue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import javax.inject.Inject;
import javax.inject.Singleton;

// @RegisteredProvider(ExternalQueueService.class)
@Singleton
public class LocalQueueService extends AbstractExternalQueueService {
  @Inject
  public LocalQueueService(EntityService entityService) {
    super(entityService);
  }

  @Override
  protected OutputStream getOutputStream(Queue queue, OutputStream outputStream)
      throws FileNotFoundException {
    return new FileOutputStream(getOutputFile(queue));
  }

  protected File getOutputFile(Queue queue) {
    return LocalQueueUtil.getTargetFile(
        LocalQueueUtil.getTargetDirectory(queue, QueueStatus.New), queue.getName());
  }
}
