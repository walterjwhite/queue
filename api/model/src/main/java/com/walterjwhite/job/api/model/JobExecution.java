package com.walterjwhite.job.api.model;

import com.walterjwhite.datastore.api.model.entity.AbstractEntity;
import com.walterjwhite.job.api.enumeration.JobExecutionState;
import com.walterjwhite.job.api.model.scheduling.AbstractSchedule;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.*;

@Entity
public class JobExecution extends AbstractEntity {
  protected transient boolean retryable;

  @ManyToOne(optional = false, cascade = CascadeType.MERGE)
  @JoinColumn(nullable = false, updatable = false)
  protected AbstractSchedule schedule;

  @Enumerated(EnumType.STRING)
  @Column
  protected JobExecutionState jobExecutionState = JobExecutionState.Pending;

  @Column(nullable = false, updatable = false)
  protected LocalDateTime startDateTime;

  @Column protected LocalDateTime endDateTime;

  @Lob @Column protected Throwable throwable;

  public JobExecution(AbstractSchedule schedule) {
    super();
    this.schedule = schedule;
    this.startDateTime = LocalDateTime.now();
  }

  public JobExecution() {
    super();
  }

  public AbstractSchedule getSchedule() {
    return schedule;
  }

  public void setSchedule(AbstractSchedule schedule) {
    this.schedule = schedule;
  }

  public LocalDateTime getStartDateTime() {
    return startDateTime;
  }

  public void setStartDateTime(LocalDateTime startDateTime) {
    this.startDateTime = startDateTime;
  }

  public LocalDateTime getEndDateTime() {
    return endDateTime;
  }

  public void setEndDateTime(LocalDateTime endDateTime) {
    this.endDateTime = endDateTime;
  }

  public Throwable getThrowable() {
    return throwable;
  }

  public void setThrowable(Throwable throwable) {
    this.throwable = throwable;
  }

  public JobExecutionState getJobExecutionState() {
    return jobExecutionState;
  }

  public void setJobExecutionState(JobExecutionState jobExecutionState) {
    this.jobExecutionState = jobExecutionState;
  }

  public boolean isRetryable() {
    return retryable;
  }

  public void setRetryable(boolean retryable) {
    this.retryable = retryable;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    JobExecution that = (JobExecution) o;
    return Objects.equals(startDateTime, that.startDateTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(startDateTime);
  }
}
