package com.walterjwhite.queue.impl;

import com.walterjwhite.queue.api.producer.InputStreamProducer;
import java.io.InputStream;

public class DefaultInputStreamProducer implements InputStreamProducer {
  protected final InputStream inputStream;

  public DefaultInputStreamProducer(InputStream inputStream) {
    this.inputStream = inputStream;
  }

  @Override
  public InputStream get() {
    return inputStream;
  }
}
