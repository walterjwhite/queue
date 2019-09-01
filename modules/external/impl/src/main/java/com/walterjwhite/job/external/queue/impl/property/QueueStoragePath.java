package com.walterjwhite.job.external.queue.impl.property;

import com.walterjwhite.property.api.annotation.DefaultValue;
import com.walterjwhite.property.api.property.ConfigurableProperty;

public interface QueueStoragePath extends ConfigurableProperty {
  @DefaultValue String Default = "/tmp/queue";
}
