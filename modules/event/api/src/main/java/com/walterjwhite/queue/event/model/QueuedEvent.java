package com.walterjwhite.queue.event.model;

import com.walterjwhite.datastore.api.model.entity.EntityReference;
import com.walterjwhite.queue.api.model.AbstractQueued;
import com.walterjwhite.queue.event.enumeration.EventActionType;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * A QueuedJob represents an instance of a scheduled queuedJob execution 1. what executor should be
 * used 2. what parameter (entityReference) should be used: TODO: support multiple resources 3. when
 * to run (immediately, @ date/time, or cron expression)
 */
@NoArgsConstructor
@Data
@ToString(doNotUseGetters = true)
// @PersistenceCapable
@Entity
public class QueuedEvent extends AbstractQueued {
  /**
   * If this is a "QueuedJob" and not an "EventProcessor", then this will be empty, otherwise, it
   * will have the "event" that triggered this queuedJob
   */
  protected EntityReference entityReference;

  protected EventActionType eventActionType;

  public QueuedEvent(
      Class jobExecutorClass, EntityReference entityReference, EventActionType eventActionType) {
    this();
    this.jobExecutorClass = jobExecutorClass;
    this.entityReference = entityReference;
    this.queueDateTime = LocalDateTime.now();
    this.eventActionType = eventActionType;
  }
}
