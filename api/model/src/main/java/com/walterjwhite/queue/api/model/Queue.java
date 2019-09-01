package com.walterjwhite.queue.api.model;

import com.walterjwhite.datastore.api.model.entity.AbstractNamedEntity;
import com.walterjwhite.queue.api.enumeration.QueueType;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(doNotUseGetters = true, callSuper = true)
// @PersistenceCapable
@NoArgsConstructor
@Entity
public class Queue extends AbstractNamedEntity implements Unqueueable {
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  protected QueueType queueType;

  public Queue(String name, QueueType queueType) {
    super(name);
    this.queueType = queueType;
  }
}
