package com.walterjwhite.job.impl.property.property;

import com.walterjwhite.property.api.annotation.DefaultValue;
import com.walterjwhite.property.api.property.ConfigurableProperty;

public interface NumberOfEventBusThreads extends ConfigurableProperty {
  @DefaultValue int Default = 2;
}
