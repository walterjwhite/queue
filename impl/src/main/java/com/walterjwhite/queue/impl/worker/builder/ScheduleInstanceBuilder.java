package com.walterjwhite.queue.impl.worker.builder;

import com.walterjwhite.queue.api.annotation.Job;
import com.walterjwhite.queue.api.model.ScheduleInstance;
import java.util.Set;

public interface ScheduleInstanceBuilder {
  void getDelay(Set<ScheduleInstance> scheduleInstances, Job job);
}
