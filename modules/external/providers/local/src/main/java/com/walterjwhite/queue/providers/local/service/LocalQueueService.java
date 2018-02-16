package com.walterjwhite.queue.providers.local.service;

import com.walterjwhite.job.api.model.Queue;
import com.walterjwhite.job.external.queue.impl.AbstractExternalQueueService;
import com.walterjwhite.job.external.queue.impl.EntityService;
import java.io.File;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// @RegisteredProvider(ExternalQueueService.class)
@Singleton
public class LocalQueueService extends AbstractExternalQueueService {
  private static final Logger LOGGER = LoggerFactory.getLogger(LocalQueueService.class);

  @Inject
  public LocalQueueService(EntityService entityService) {
    super(entityService);
  }

  @Override
  public void doWrite(Queue queue, byte[] data) {
    LOGGER.info("writing message:" + queue);

    final File outputFile =
        LocalQueueUtil.getTargetFile(
            LocalQueueUtil.getTargetDirectory(queue, QueueStatus.New), queue.getName());

    LOGGER.info("output file:" + outputFile);
    try {
      FileUtils.writeByteArrayToFile(outputFile, data);
    } catch (Exception e) {
      LOGGER.warn("Error writing contents", e);
    }
  }
}
