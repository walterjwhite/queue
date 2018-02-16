package com.walterjwhite.job.api.model.scheduling;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class FixedDelaySchedule extends AbstractSchedule {
  @Column protected long fixedDelay;

  @Column protected long initialDelay;

  @Column protected TimeUnit timeUnit;

  public long getFixedDelay() {
    return fixedDelay;
  }

  public void setFixedDelay(long fixedDelay) {
    this.fixedDelay = fixedDelay;
  }

  public long getInitialDelay() {
    return initialDelay;
  }

  public void setInitialDelay(long initialDelay) {
    this.initialDelay = initialDelay;
  }

  public TimeUnit getTimeUnit() {
    return timeUnit;
  }

  public void setTimeUnit(TimeUnit timeUnit) {
    this.timeUnit = timeUnit;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    FixedDelaySchedule that = (FixedDelaySchedule) o;
    return fixedDelay == that.fixedDelay
        && initialDelay == that.initialDelay
        && timeUnit == that.timeUnit;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), fixedDelay, initialDelay, timeUnit);
  }
}
