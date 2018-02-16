package com.walterjwhite.job.api.model;

import com.walterjwhite.datastore.api.model.entity.AbstractNamedEntity;
import com.walterjwhite.job.api.enumeration.QueueType;
import javax.persistence.*;

@Entity
public class Queue extends AbstractNamedEntity {
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  protected QueueType queueType;

  public Queue(String name, QueueType queueType) {
    super(name);
    this.queueType = queueType;
  }

  public Queue() {
    super();
  }

  public QueueType getQueueType() {
    return queueType;
  }

  public void setQueueType(QueueType queueType) {
    this.queueType = queueType;
  }
}
