package com.walterjwhite.queue.api.property;

import com.walterjwhite.property.api.annotation.DefaultValue;
import com.walterjwhite.property.api.property.ConfigurableProperty;

public interface JobRetryAttempts extends ConfigurableProperty {
  @DefaultValue int Default = 2;
}
