package com.walterjwhite.queue.impl.worker.property;

import com.walterjwhite.property.api.annotation.DefaultValue;
import com.walterjwhite.property.api.property.ConfigurableProperty;

public interface JobExecutionHeartbeatTimeoutValue extends ConfigurableProperty {
  @DefaultValue long Default = 1;
}
