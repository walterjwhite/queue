package com.walterjwhite.job.api.model.scheduling;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class CronSchedule extends AbstractSchedule {
  // daily
  // monthly
  // yearly
  // weekly
  // minute hour (day of month) month (day of week)
  @Column protected String cronExpression;

  public CronSchedule() {
    super();
  }

  public String getCronExpression() {
    return cronExpression;
  }

  public void setCronExpression(String cronExpression) {
    this.cronExpression = cronExpression;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CronSchedule that = (CronSchedule) o;
    return Objects.equals(cronExpression, that.cronExpression);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), cronExpression);
  }
}
