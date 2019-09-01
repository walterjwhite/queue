package com.walterjwhite.queue.providers.local.service;

import com.walterjwhite.queue.api.model.Queue;
import java.io.File;

public class LocalQueueUtil {

  public static final String QUEUE_PATH = "/tmp/queue/store";

  private LocalQueueUtil() {}

  public static File getTargetDirectory(final Queue queue, final QueueStatus queueStatus) {
    return (new File(
        QUEUE_PATH
            + File.separator
            + queue.getName()
            + File.separator
            + queueStatus.name().toLowerCase()));
  }

  public static File getTargetFile(final File directory, final File file) {
    final File destination =
        new File(
            directory.getAbsolutePath()
                + File.separator
                + file.getName()
                + "."
                + System.currentTimeMillis());
    destination.getParentFile().mkdirs();

    return (destination);
  }

  public static File getTargetFile(final File directory, final String fileName) {
    final File destination =
        new File(
            directory.getAbsolutePath()
                + File.separator
                + fileName
                + "."
                + System.currentTimeMillis());
    destination.getParentFile().mkdirs();

    return (destination);
  }

  public static File getTargetFileForQueueName(final File directory, final String queueName) {
    final File destination =
        new File(
            directory.getAbsolutePath()
                + File.separator
                + queueName
                + File.separator
                + System.currentTimeMillis());
    destination.getParentFile().mkdirs();

    return (destination);
  }

  public static File move(final File f, final Queue queue, final QueueStatus queueStatus) {
    final File destination =
        LocalQueueUtil.getTargetFile(LocalQueueUtil.getTargetDirectory(queue, queueStatus), f);
    f.renameTo(destination);

    return (destination);
  }
}
