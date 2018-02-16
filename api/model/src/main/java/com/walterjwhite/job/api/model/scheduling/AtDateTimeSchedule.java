package com.walterjwhite.job.api.model.scheduling;

import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;

/** Simply schedule the job to run at the specified date/time */
@Entity
public class AtDateTimeSchedule extends AbstractSchedule {
  @Column protected LocalDateTime onDateTime;

  public AtDateTimeSchedule(LocalDateTime onDateTime) {
    super();
    this.onDateTime = onDateTime;
  }

  public AtDateTimeSchedule() {
    super();
    onDateTime = LocalDateTime.now();
  }

  public LocalDateTime getOnDateTime() {
    return onDateTime;
  }

  public void setOnDateTime(LocalDateTime onDateTime) {
    this.onDateTime = onDateTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    AtDateTimeSchedule that = (AtDateTimeSchedule) o;
    return Objects.equals(onDateTime, that.onDateTime);
  }

  @Override
  public int hashCode() {

    return Objects.hash(super.hashCode(), onDateTime);
  }
}
