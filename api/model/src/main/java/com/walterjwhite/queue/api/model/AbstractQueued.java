package com.walterjwhite.queue.api.model;

import com.walterjwhite.datastore.api.model.entity.AbstractEntity;
import com.walterjwhite.queue.api.enumeration.QueueState;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

// @NoArgsConstructor
@Data
@ToString(doNotUseGetters = true)
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
// @Embeddable
// @MappedSuperclass
public class AbstractQueued extends AbstractEntity implements Unqueueable {
  protected transient boolean system;

  @Column(nullable = false)
  protected Class jobExecutorClass;

  @ManyToOne(optional = false)
  @JoinColumn(nullable = false, updatable = false)
  protected Worker originatingWorker;

  @EqualsAndHashCode.Exclude
  @Column(nullable = false, updatable = false)
  protected LocalDateTime queueDateTime;

  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @OneToMany(mappedBy = "queued")
  protected List<JobExecution> jobExecutions = new ArrayList<>();

  @ManyToOne @JoinColumn protected JobExecution currentJobExecution;

  @EqualsAndHashCode.Exclude
  @ManyToOne
  @JoinColumn(updatable = false)
  protected Queue queue;

  @Enumerated @Column protected QueueState queueState;
  //  /**
  //   * Used by the workers to determine if the job can be picked up for assignment.
  //   *
  //   * @return
  //   */
  //  public boolean isAssignable() {
  //    return getExecutionState().isAssignable();
  //  }
  //
  //  public ExecutionState getExecutionState() {
  ////    if (jobExecutions == null || jobExecutions.isEmpty()) return ExecutionState.Queued;
  ////
  ////    return jobExecutions.get(jobExecutions.size() - 1).getExecutionState();
  //    if(currentJobExecution == null) return ExecutionState.Queued;
  //
  //    return currentJobExecution.getExecutionState();
  //  }
}
