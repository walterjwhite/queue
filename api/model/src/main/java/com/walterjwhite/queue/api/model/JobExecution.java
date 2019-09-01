package com.walterjwhite.queue.api.model;

import com.walterjwhite.datastore.api.model.entity.AbstractEntity;
import com.walterjwhite.queue.api.enumeration.ExecutionState;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(doNotUseGetters = true)
// @PersistenceCapable
@Entity
@NoArgsConstructor
public class JobExecution extends AbstractEntity implements Unqueueable {
  @ManyToOne(optional = false)
  @JoinColumn(nullable = false, updatable = false)
  protected AbstractQueued queued;

  @Column(nullable = false, updatable = false)
  protected int attemptIndex;

  @EqualsAndHashCode.Exclude
  @Column(updatable = false)
  protected LocalDateTime startDateTime;

  @EqualsAndHashCode.Exclude
  @Column(insertable = false)
  protected LocalDateTime endDateTime;

  @EqualsAndHashCode.Exclude
  @Column(updatable = false)
  protected LocalDateTime timeoutDateTime;

  /**
   * Used by the worker to indicate it is still working If the worker dies unexpectedly, this will
   * be compared against the current time and a given timeout. if greater than that, then this job
   * is marked as aborted and retried
   */
  @EqualsAndHashCode.Exclude
  @Column(insertable = false)
  protected LocalDateTime updateDateTime;

  @EqualsAndHashCode.Exclude
  @Lob
  @Column(insertable = false)
  protected Throwable throwable;

  @Enumerated
  @Column(insertable = false)
  protected ExecutionState executionState;

  @EqualsAndHashCode.Exclude
  @ManyToOne(optional = false)
  @JoinColumn(nullable = false, updatable = false)
  protected Worker worker;

  public JobExecution(AbstractQueued queued) {
    super();
    this.queued = queued;
    this.attemptIndex = queued.getJobExecutions().size();
    this.startDateTime = LocalDateTime.now();
  }
}
