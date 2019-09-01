package com.walterjwhite.job.impl.property.property;

import com.walterjwhite.property.api.annotation.DefaultValue;
import com.walterjwhite.property.api.property.ConfigurableProperty;

public interface AsyncEventBusName extends ConfigurableProperty {
  @DefaultValue String Default = "AsyncEventBus";
}
