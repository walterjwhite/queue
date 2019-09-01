package com.walterjwhite.queue.impl.worker.property;

import com.walterjwhite.property.api.annotation.DefaultValue;
import com.walterjwhite.property.api.property.ConfigurableProperty;

public interface ExecutorServiceShutdownTimeoutValue extends ConfigurableProperty {
  @DefaultValue int Default = 30;
}
