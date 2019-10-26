package com.walterjwhite.job.external.queue.impl.service;

import com.google.common.eventbus.EventBus;
import com.walterjwhite.datastore.api.model.entity.AbstractEntity;
import com.walterjwhite.file.modules.watcher.DirectoryWatcherService;
import com.walterjwhite.file.modules.watcher.SimpleDirectoryWatcherService;
import com.walterjwhite.infrastructure.inject.core.service.StartupAware;
import com.walterjwhite.interruptable.Interruptable;
import com.walterjwhite.interruptable.annotation.InterruptableService;
import com.walterjwhite.job.external.queue.api.ExternalQueueService;
import com.walterjwhite.job.external.queue.impl.property.QueueStoragePath;
import com.walterjwhite.property.impl.annotation.Property;
import com.walterjwhite.queue.api.model.Queue;
import java.io.File;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * NoOp is not supported for this class. If we run send from the CLI, it will actually send
 * messages. If we run send from the daemon, it will also send messages.
 */
@Singleton
@InterruptableService
public class MessageWriterService implements StartupAware, Interruptable {
  protected final File messageDirectory;
  protected final File messageProcessingDirectory;
  protected final File messageProcessedDirectory;

  protected final EventBus eventBus;

  protected final DirectoryWatcherService directoryWatcherService;
  protected final ExternalQueueService externalQueueService;

  /*
  TODO:
  1. inject this with CLI / properties
  2. delete processed messages for security (alternatively, encrypt them once processed)
  3. provide message encryption application
  4. provide message decryption application
  5. provide JSON / YAML examples
  6. provide database store (use h2 for simplicity)
     a. integrated elasticsearch
     b. integrated mahout for categorization, and running a sanity check to ensure we're running safe messages

   */

  @Inject
  public MessageWriterService(
      EventBus eventBus,
      ExternalQueueService externalQueueService,
      @Property(QueueStoragePath.class) String queueStoragePath)
      throws IOException {
    super();

    this.eventBus = eventBus;

    final String messageDirectory = queueStoragePath + "/new";
    final String messageProcessingDirectory = queueStoragePath + "/processing";
    final String messageProcessedDirectory = queueStoragePath + "/processed";

    this.messageDirectory = new File(messageDirectory);
    if (!this.messageDirectory.exists()) {
      this.messageDirectory.mkdirs();
    }

    this.messageProcessingDirectory = new File(messageProcessingDirectory);
    if (!this.messageProcessingDirectory.exists()) {
      this.messageProcessingDirectory.mkdirs();
    }

    this.messageProcessedDirectory = new File(messageProcessedDirectory);
    if (!this.messageProcessedDirectory.exists()) {
      this.messageProcessedDirectory.mkdirs();
    }

    this.externalQueueService = externalQueueService;

    directoryWatcherService = new SimpleDirectoryWatcherService();
    directoryWatcherService.register(
        new DirectoryWatcherService.OnFileChangeListener() {
          @Override
          public void onFileCreate(String filePath) {
            try {
              queueMessage(new File(messageDirectory + File.separator + filePath));
            } catch (Exception e) {
              handleQueueException(e);
            }
          }
        },
        messageDirectory,
        "*");
    // registerQueuer();
  }
  //
  //  protected void registerQueuer(){
  //
  //  }

  protected void handleQueueException(Exception e) {}

  protected void queueMessage(final File source) {
    try {
      // move the file to prevent it from being processed elsewhere
      final File processingFile = getTargetFilename(messageProcessingDirectory, source);
      if (!source.renameTo(processingFile)) throw new IOException("Error renaming file");

      final AbstractEntity message = null;
      //          messageHelperService.getMessage(FileUtils.readFileToByteArray(processingFile),
      // false);

      doWrite(null, message);

      // finally, move the file to the processed directory
      // use System.currentTimeMillis to avoid naming collisions
      if (!processingFile.renameTo(getTargetFilename(messageProcessedDirectory, processingFile)))
        throw new IOException("Error renaming file");
    } catch (Exception e) {
      handleQueueException(e);
    }
  }

  protected void doWrite(Queue queue, AbstractEntity entity) throws Exception {
    // eventBus.post(new PreSendMessage(message));
    externalQueueService.prepare(queue, entity);
    externalQueueService.write(queue, entity);
  }

  private static File getTargetFilename(final File parentDirectory, final File sourceFile) {
    return (new File(
        parentDirectory.getAbsolutePath()
            + File.separator
            + sourceFile.getName()
            + ""
            + System.currentTimeMillis()));
  }

  /**
   * if the message requires any *special* processing, do it now this includes uploading a file to a
   * 3rd party provider
   */
  // TODO: use an interceptor(s) here
  /*
  protected void preProcess(AbstractMessage message) {
    final List<PreprocessorMapping> preprocessorMappings =
        PreprocessorMapping.getForType(message.getClass());
    if (preprocessorMappings != null && !preprocessorMappings.isEmpty()) {
      for (PreprocessorMapping preprocessorMapping : preprocessorMappings) {
        LOGGER.info("preprocessing with:" + preprocessorMapping.getPreprocessor());

        final Preprocessor preprocessor =
            null; //preprocessorInstance.select(preprocessorMapping.getPreprocessor()).get();
        preprocessor.process(message);

        LOGGER.info("preprocessed with:" + preprocessorMapping.getPreprocessor());
      }
    }
  }
  */

  @Override
  public void interrupt() {
    directoryWatcherService.stop();
  }

  @Override
  public void onStartup() {
    directoryWatcherService.start();
  }
}
