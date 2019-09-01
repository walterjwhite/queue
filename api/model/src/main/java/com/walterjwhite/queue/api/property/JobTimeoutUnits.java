package com.walterjwhite.queue.api.property;

import com.walterjwhite.property.api.annotation.DefaultValue;
import com.walterjwhite.property.api.property.ConfigurableProperty;
import java.time.temporal.ChronoUnit;

public interface JobTimeoutUnits extends ConfigurableProperty {
  @DefaultValue ChronoUnit Default = ChronoUnit.MILLIS;
}
