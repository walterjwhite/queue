package com.walterjwhite.queue.providers.local.service;

import com.google.common.util.concurrent.AbstractIdleService;
import com.walterjwhite.file.modules.watcher.DirectoryWatcherService;
import com.walterjwhite.file.modules.watcher.SimpleDirectoryWatcherService;
import com.walterjwhite.job.api.enumeration.QueueType;
import com.walterjwhite.job.api.model.Queue;
import com.walterjwhite.job.api.model.QueueMonitor;
import com.walterjwhite.job.external.queue.api.ExternalQueueBridgeService;
import com.walterjwhite.job.external.queue.impl.file.FileInputStreamProducer;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Watches all configured queues */
public class AsynchronousLocalQueueReaderService extends AbstractIdleService {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(AsynchronousLocalQueueReaderService.class);

  protected final QueueMonitor queueMonitor;
  protected final Set<DirectoryWatcherService> directoryWatcherServices = new HashSet<>();
  protected final ExternalQueueBridgeService externalQueueBridgeService;

  @Inject
  public AsynchronousLocalQueueReaderService(
      QueueMonitor queueMonitor, ExternalQueueBridgeService externalQueueBridgeService)
      throws IOException {
    super();
    this.queueMonitor = queueMonitor;
    this.externalQueueBridgeService = externalQueueBridgeService;

    setupDirectoryWatchers(queueMonitor);
  }

  protected void setupDirectoryWatchers(QueueMonitor queueMonitor) throws IOException {
    for (Queue queue : queueMonitor.getQueues()) {
      directoryWatcherServices.add(setupDirectoryWatcher(queue));
    }

    // also need a generic queue directory so that we can send outgoing messages from the
    // command-line or simply by dropping a file in a directory
    // NOTE: these files must be encrypted and compressed, otherwise, we will fail to read them
    directoryWatcherServices.add(setupDirectoryWatcher(new Queue("outbound", QueueType.Outbound)));
  }

  protected DirectoryWatcherService setupDirectoryWatcher(Queue queue) throws IOException {
    // prepare directories
    for (QueueStatus queueStatus : QueueStatus.values()) {
      LocalQueueUtil.getTargetDirectory(queue, queueStatus).mkdirs();
    }

    DirectoryWatcherService directoryWatcherService = new SimpleDirectoryWatcherService();
    directoryWatcherService.register(
        new DirectoryWatcherService.OnFileChangeListener() {
          @Override
          public void onFileCreate(String filePath) {
            try {
              externalQueueBridgeService.readRaw(
                  queue,
                  new FileInputStreamProducer(
                      new File(
                          LocalQueueUtil.getTargetDirectory(queue, QueueStatus.New)
                                  .getAbsolutePath()
                              + File.separator
                              + filePath),
                      LocalQueueUtil.getTargetFile(
                          LocalQueueUtil.getTargetDirectory(queue, QueueStatus.Processed),
                          filePath)));
            } catch (Exception e) {
              LOGGER.error("error processing file", e);
            }
          }
        },
        LocalQueueUtil.getTargetDirectory(queue, QueueStatus.New).getAbsolutePath(),
        "*");

    return (directoryWatcherService);
  }

  @Override
  protected void startUp() throws Exception {
    for (DirectoryWatcherService directoryWatcherService : directoryWatcherServices)
      directoryWatcherService.start();
  }

  @Override
  protected void shutDown() throws Exception {
    for (DirectoryWatcherService directoryWatcherService : directoryWatcherServices)
      directoryWatcherService.stop();
  }
}
