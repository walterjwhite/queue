package com.walterjwhite.job.api.model;

import com.walterjwhite.datastore.api.model.entity.AbstractEntity;
import com.walterjwhite.datastore.api.model.entity.EntityReference;
import com.walterjwhite.job.api.enumeration.Durability;
import com.walterjwhite.job.api.model.scheduling.AbstractSchedule;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.persistence.*;

/**
 * A Job represents an instance of a scheduled job execution 1. what executor should be used 2. what
 * parameter (entityReference) should be used: TODO: support multiple resources 3. when to run
 * (immediately, @ date/time, or cron expression)
 */
@Entity
public class Job extends AbstractEntity {
  @ManyToOne(optional = false)
  @JoinColumn(nullable = false, updatable = false)
  protected JobExecutor jobExecutor;

  @ManyToOne
  @JoinColumn(nullable = false, updatable = false)
  protected EntityReference entityReference;

  @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
  protected List<AbstractSchedule> schedules = new ArrayList<>();

  @ManyToOne
  @JoinColumn(updatable = false)
  protected Queue queue;

  @Column protected int executionTimeLimit;

  @Column protected TimeUnit executionTimeLimitUnits;

  @Column protected Durability durability = Durability.Persistent;

  // retryAttempts is set via property (over overridden), applies to each schedule
  @Column protected int retryAttempts;

  // shall we wait (block) or just return this request?
  @Column protected boolean synchronous = false;

  /** Used for jobs that run once "NOW" */
  public Job(JobExecutor jobExecutor) {
    this();
    this.jobExecutor = jobExecutor;
  }

  public Job(
      JobExecutor jobExecutor, EntityReference entityReference, AbstractSchedule... schedules) {
    this();
    this.jobExecutor = jobExecutor;

    if (schedules != null && schedules.length > 0) {
      for (AbstractSchedule abstractSchedule : schedules) this.schedules.add(abstractSchedule);
    }

    this.entityReference = entityReference;
  }

  public Job() {
    super();
  }

  public JobExecutor getJobExecutor() {
    return jobExecutor;
  }

  public void setJobExecutor(JobExecutor jobExecutor) {
    this.jobExecutor = jobExecutor;
  }

  public EntityReference getEntityReference() {
    return entityReference;
  }

  public void setEntityReference(EntityReference entityReference) {
    this.entityReference = entityReference;
  }

  public List<AbstractSchedule> getSchedules() {
    return schedules;
  }

  public void setSchedules(List<AbstractSchedule> schedules) {
    this.schedules = schedules;
  }

  public Queue getQueue() {
    return queue;
  }

  public void setQueue(Queue queue) {
    this.queue = queue;
  }

  public int getExecutionTimeLimit() {
    return executionTimeLimit;
  }

  public void setExecutionTimeLimit(int executionTimeLimit) {
    this.executionTimeLimit = executionTimeLimit;
  }

  public TimeUnit getExecutionTimeLimitUnits() {
    return executionTimeLimitUnits;
  }

  public void setExecutionTimeLimitUnits(TimeUnit executionTimeLimitUnits) {
    this.executionTimeLimitUnits = executionTimeLimitUnits;
  }

  public Durability getDurability() {
    return durability;
  }

  public void setDurability(Durability durability) {
    this.durability = durability;
  }

  public int getRetryAttempts() {
    return retryAttempts;
  }

  public void setRetryAttempts(int retryAttempts) {
    this.retryAttempts = retryAttempts;
  }

  public boolean isSynchronous() {
    return synchronous;
  }

  public void setSynchronous(boolean synchronous) {
    this.synchronous = synchronous;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Job job = (Job) o;
    return Objects.equals(jobExecutor, job.jobExecutor)
        && Objects.equals(entityReference, job.entityReference)
        && Objects.equals(schedules, job.schedules);
  }

  @Override
  public int hashCode() {

    return Objects.hash(jobExecutor, entityReference, schedules);
  }
}
