package com.walterjwhite.job.external.queue.impl.file;

import com.walterjwhite.queue.api.producer.InputStreamProducer;
import java.io.File;
import java.io.FileInputStream;

public class FileInputStreamProducer implements InputStreamProducer<FileInputStream> {
  protected final File source;
  protected final File renameTo;

  public FileInputStreamProducer(File source, File renameTo) {
    super();
    this.source = source;
    this.renameTo = renameTo;
  }

  @Override
  public FileInputStream get() throws Exception {
    return new FileInputStreamWrapper(source, renameTo);
  }
}
