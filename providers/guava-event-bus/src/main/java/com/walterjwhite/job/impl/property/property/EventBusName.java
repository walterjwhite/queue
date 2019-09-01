package com.walterjwhite.job.impl.property.property;

import com.walterjwhite.property.api.annotation.DefaultValue;
import com.walterjwhite.property.api.property.ConfigurableProperty;

public interface EventBusName extends ConfigurableProperty {
  @DefaultValue String Default = "EventBus";
}
