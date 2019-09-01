package com.walterjwhite.queue.impl.worker.property;

import com.walterjwhite.property.api.annotation.DefaultValue;
import com.walterjwhite.property.api.property.ConfigurableProperty;

// TODO: tie this into a queue such that for a high-priority queue, we can have 100 threads
// then for a low-priority queue, we have 2 threads
public interface NumberOfThreads extends ConfigurableProperty {
  @DefaultValue int Default = Runtime.getRuntime().availableProcessors() * 2;
}
