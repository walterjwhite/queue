package com.walterjwhite.job.impl;

import java.util.concurrent.TimeUnit;

public class ScheduleInstance {
  protected final long initialDelay;
  protected final long fixedDelay;
  protected final TimeUnit units;

  public ScheduleInstance(long initialDelay, long fixedDelay, TimeUnit units) {
    this.initialDelay = initialDelay;
    this.fixedDelay = fixedDelay;
    this.units = units;
  }

  public long getInitialDelay() {
    return initialDelay;
  }

  public long getFixedDelay() {
    return fixedDelay;
  }

  public TimeUnit getUnits() {
    return units;
  }

  @Override
  public String toString() {
    return "ScheduleInstance{"
        + "initialDelay="
        + initialDelay
        + ", fixedDelay="
        + fixedDelay
        + ", units="
        + units
        + '}';
  }
}
