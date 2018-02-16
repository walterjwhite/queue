package com.walterjwhite.job.external.queue.impl.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileInputStreamWrapper extends FileInputStream {
  protected final File source;
  protected final File renameTo;

  public FileInputStreamWrapper(File file, File renameTo) throws FileNotFoundException {
    super(file);
    this.source = file;
    this.renameTo = renameTo;
  }

  @Override
  public int read() throws IOException {
    final int result = super.read();
    handleRename(result);

    return (result);
  }

  @Override
  public int read(byte[] bytes) throws IOException {
    final int result = super.read(bytes);
    handleRename(result);

    return (result);
  }

  @Override
  public int read(byte[] bytes, int i, int i1) throws IOException {
    final int result = super.read(bytes, i, i1);
    handleRename(result);

    return (result);
  }

  protected void handleRename(final int result) {
    if (result == -1) {
      renameFile();
    }
  }

  protected void renameFile() {
    source.renameTo(renameTo);
  }
}
