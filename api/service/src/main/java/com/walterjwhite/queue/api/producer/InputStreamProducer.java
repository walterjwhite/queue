package com.walterjwhite.queue.api.producer;

import java.io.InputStream;

public interface InputStreamProducer<InputStreamType extends InputStream> {
  InputStreamType get() throws Exception;
}
