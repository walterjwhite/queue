package com.walterjwhite.job.api.model.scheduling;

import com.walterjwhite.datastore.api.model.entity.AbstractUUIDEntity;
import com.walterjwhite.job.api.model.Job;
import com.walterjwhite.job.api.model.JobExecution;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Inheritance(strategy = InheritanceType.JOINED)
// @MappedSuperclass
@Entity
public abstract class AbstractSchedule extends AbstractUUIDEntity {
  public static final int PAST_SCHEDULED_RUNTIME = -1;
  public static final int NO_FUTURE_RUNS = -2;

  @ManyToOne @JoinColumn protected Job job;

  @Column protected int remainingRetries;

  @OneToMany(cascade = CascadeType.ALL)
  protected List<JobExecution> jobExecutions = new ArrayList<>();

  public Job getJob() {
    return job;
  }

  public void setJob(Job job) {
    this.job = job;
  }

  public List<JobExecution> getJobExecutions() {
    return jobExecutions;
  }

  public void setJobExecutions(List<JobExecution> jobExecutions) {
    this.jobExecutions = jobExecutions;
  }

  public int getRemainingRetries() {
    return remainingRetries;
  }

  public void setRemainingRetries(int remainingRetries) {
    this.remainingRetries = remainingRetries;
  }

  //  @Override
  //  public boolean equals(Object o) {
  //    if (this == o) return true;
  //    if (o == null || getClass() != o.getClass()) return false;
  //    if (!super.equals(o)) return false;
  //    AbstractSchedule that = (AbstractSchedule) o;
  //    return Objects.equals(job, that.job);
  //  }
  //
  //  @Override
  //  public int hashCode() {
  //    return Objects.hash(super.hashCode(), job);
  //  }
}
