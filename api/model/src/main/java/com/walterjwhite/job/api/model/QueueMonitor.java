package com.walterjwhite.job.api.model;

import com.walterjwhite.datastore.api.model.entity.AbstractNamedEntity;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class QueueMonitor extends AbstractNamedEntity {
  @OneToMany protected Set<Queue> queues = new HashSet<>();

  public QueueMonitor(String name, Set<Queue> queues) {
    this(name);
    this.queues.addAll(queues);
  }

  public QueueMonitor(String name) {
    super(name);
  }

  public QueueMonitor() {
    super();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<Queue> getQueues() {
    return queues;
  }

  public void setQueues(Set<Queue> queues) {
    this.queues = queues;
  }
}
