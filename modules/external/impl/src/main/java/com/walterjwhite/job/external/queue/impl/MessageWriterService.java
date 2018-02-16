package com.walterjwhite.job.external.queue.impl;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractIdleService;
import com.walterjwhite.datastore.api.model.entity.AbstractEntity;
import com.walterjwhite.file.modules.watcher.DirectoryWatcherService;
import com.walterjwhite.file.modules.watcher.SimpleDirectoryWatcherService;
import com.walterjwhite.google.guice.property.property.Property;
import com.walterjwhite.job.api.model.Queue;
import com.walterjwhite.job.external.queue.api.ExternalQueueService;
import java.io.File;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NoOp is not supported for this class. If we run send from the CLI, it will actually send
 * messages. If we run send from the daemon, it will also send messages.
 */
@Singleton
public class MessageWriterService extends AbstractIdleService {
  private static final Logger LOGGER = LoggerFactory.getLogger(MessageWriterService.class);

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

    LOGGER.info("initializing message writer.");

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
    directoryWatcherService.register( // May throw
        new DirectoryWatcherService.OnFileChangeListener() {
          @Override
          public void onFileCreate(String filePath) {
            try {
              //              LOGGER.info("source:" + filePath);
              //              LOGGER.info("source:" + messageDirectory + File.separator + filePath);
              queueMessage(new File(messageDirectory + File.separator + filePath));
            } catch (Exception e) {
              LOGGER.error("error processing file", e);
            }
          }
        },
        messageDirectory,
        "*");
  }

  protected void queueMessage(final File source) {
    try {
      // move the file to prevent it from being processed elsewhere
      final File processingFile = getTargetFilename(messageProcessingDirectory, source);
      if (!source.renameTo(processingFile)) throw (new IOException("Error renaming file"));

      final AbstractEntity message = null;
      //          messageHelperService.getMessage(FileUtils.readFileToByteArray(processingFile),
      // false);

      doWrite(null, message);

      // finally, move the file to the processed directory
      // use System.currentTimeMillis to avoid naming collisions
      if (!processingFile.renameTo(getTargetFilename(messageProcessedDirectory, processingFile)))
        throw (new IOException("Error renaming file"));
    } catch (Exception e) {
      LOGGER.error("Error processing:", e);
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
  protected void startUp() throws Exception {
    directoryWatcherService.start();
  }

  @Override
  protected void shutDown() throws Exception {
    directoryWatcherService.stop();
  }
}
