package com.walterjwhite.queue.impl.worker.builder;

import com.walterjwhite.queue.api.annotation.Job;
import com.walterjwhite.queue.api.model.ScheduleInstance;
import java.util.Arrays;
import java.util.Set;

public class FixedDelayScheduleInstanceBuilder implements ScheduleInstanceBuilder {
  @Override
  public void getDelay(Set<ScheduleInstance> scheduleInstances, Job job) {
    Arrays.stream(job.fixedDelay())
        .forEach(
            fixedDelay ->
                scheduleInstances.add(
                    new ScheduleInstance(
                        fixedDelay.initialDelay(),
                        fixedDelay.fixedDelay(),
                        fixedDelay.timeUnit())));
  }
}
