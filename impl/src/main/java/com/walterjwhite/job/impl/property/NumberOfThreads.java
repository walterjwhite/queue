package com.walterjwhite.job.impl.property;

import com.walterjwhite.google.guice.property.property.DefaultValue;
import com.walterjwhite.google.guice.property.property.GuiceProperty;

public interface NumberOfThreads extends GuiceProperty {
  @DefaultValue int Default = 2;
}
