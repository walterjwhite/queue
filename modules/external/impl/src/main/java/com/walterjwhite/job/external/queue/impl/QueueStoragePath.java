package com.walterjwhite.job.external.queue.impl;

import com.walterjwhite.google.guice.property.property.DefaultValue;
import com.walterjwhite.google.guice.property.property.GuiceProperty;

public interface QueueStoragePath extends GuiceProperty {
  @DefaultValue String Default = "/tmp/queue";
}
